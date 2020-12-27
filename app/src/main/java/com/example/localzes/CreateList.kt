package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterCreateList
import com.example.localzes.Modals.ModelList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_create_list.*

class CreateList : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var userDatabase: DatabaseReference
    private lateinit var createListAdapter:AdapterCreateList
    private lateinit var list:List<ModelList>
    private lateinit var listRecycler:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_list)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        list=ArrayList<ModelList>()
        listRecycler=findViewById(R.id.recycler_list)
        listRecycler.layoutManager=LinearLayoutManager(this)
        userDatabase = FirebaseDatabase.getInstance().reference.child("users").child(uid)
            .child("OrderList")

        btnAdd.setOnClickListener {
            val timestamp = System.currentTimeMillis().toString()
            val headers = HashMap<String, String>()
            headers["itemId"] = timestamp
            headers["itemName"] = ""
            headers["itemQuantity"] = ""
            userDatabase.child(timestamp).setValue(headers).addOnCompleteListener {
                if (it.isSuccessful){
                    this.recreate()
                }
            }
        }
        userDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    if (!(i.child("itemId").value.toString() == "" && i.child("itemName").value.toString() == "")) {
                        btnCn.setOnClickListener {
                            val obj = ModelList(
                                i.child("itemId").value.toString(),
                                i.child("itemName").value.toString(),
                                i.child("itemQuantity").value.toString()
                            )
                            val shopDatabaseReference=FirebaseDatabase.getInstance().reference

                        }
                    } else if (i.child("itemId").value.toString() == "" && i.child("itemName").value.toString() == "") {
                        btnCn.setOnClickListener {
                            Toast.makeText(
                                this@CreateList,
                                "Some fields are empty",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
            }
        })
        userDatabase.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (list as ArrayList<ModelList>).clear()
                for(i in snapshot.children){

                    val obj = ModelList(
                        i.child("itemId").value.toString(),
                        i.child("itemName").value.toString(),
                        i.child("itemQuantity").value.toString()
                    )
                    (list as ArrayList<ModelList>).add(obj)
                }
                createListAdapter= AdapterCreateList(this@CreateList,list)
                listRecycler.adapter=createListAdapter
            }
        })
    }
}