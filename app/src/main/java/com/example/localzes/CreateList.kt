package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.localzes.Modals.ModelList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_create_list.*

class CreateList : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var userDatabase: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_list)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        userDatabase = FirebaseDatabase.getInstance().reference.child("users").child(uid)
            .child("OrderList")

        btnAdd.setOnClickListener {
            val timestamp = System.currentTimeMillis().toString()
            val headers = HashMap<String, String>()
            headers["itemId"] = timestamp
            headers["itemName"] = ""
            headers["itemQuantity"] = ""
            userDatabase.child(timestamp).setValue(headers)
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
                        break
                    }
                }
            }
        })
    }
}