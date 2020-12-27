package com.example.localzes

import android.app.ProgressDialog
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
    private lateinit var createListAdapter: AdapterCreateList
    private lateinit var list: List<ModelList>
    private lateinit var listRecycler: RecyclerView
    private lateinit var shopDatabase: DatabaseReference
    private var shopId: String? = null
    private var bool = true
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_list)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        shopId = intent.getStringExtra("ShopListId")
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)
        list = ArrayList<ModelList>()
        listRecycler = findViewById(R.id.recycler_list)
        listRecycler.layoutManager = LinearLayoutManager(this)
        userDatabase = FirebaseDatabase.getInstance().reference.child("users").child(uid)
        shopDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(shopId.toString())

        btnAdd.setOnClickListener {
            val timestamp = System.currentTimeMillis().toString()
            val headers = HashMap<String, String>()
            headers["itemId"] = timestamp
            headers["itemName"] = ""
            headers["itemQuantity"] = ""
            userDatabase.child("OrderList").child(timestamp).setValue(headers).addOnCompleteListener {
                if (it.isSuccessful) {
                    this.recreate()
                }
            }
        }
        userDatabase.child("OrderList").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (list as ArrayList<ModelList>).clear()
                for (i in snapshot.children) {

                    val obj = ModelList(
                        i.child("itemId").value.toString(),
                        i.child("itemName").value.toString(),
                        i.child("itemQuantity").value.toString()
                    )
                    (list as ArrayList<ModelList>).add(obj)

                }
                createListAdapter = AdapterCreateList(this@CreateList, list)
                listRecycler.adapter = createListAdapter
            }
        })
        for (j in list) {
            if (j.itemName == "" || j.itemQuantity == "") {
                bool = false
                break
            }
        }
        btnCn.setOnClickListener {
            if (bool) {
                progressDialog.setMessage("Placing Your Order")
                progressDialog.show()
                val dataReference: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("users").child(uid)
                        .child("MyOrderList")
                val ref: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("seller")
                        .child(shopId.toString()).child("OrdersLists")
                val orderId=System.currentTimeMillis().toString()
                for(i in 0 until list.size){
                    val itemId=list[i].itemId
                    val itemName=list[i].itemName
                    val itemQuantity=list[i].itemQuantity
                    val headers=HashMap<String,String>()
                    headers["itemId"]=itemId
                    headers["itemName"]=itemName
                    headers["itemQuantity"]=itemQuantity
                    ref.child(orderId).child(itemId).setValue(headers)
                    dataReference.child(orderId).child(itemId).setValue(headers)
                }
                progressDialog.dismiss()
            }
        }


    }
}