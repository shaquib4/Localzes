package com.example.localzes

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterCreateList
import com.example.localzes.Modals.ModalSellerOrderList
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
    private var shopId: String? = "200"
    private var bool = true
    private lateinit var progressDialog: ProgressDialog
    private var orderByName: String? = "100"
    private var orderByMobile: String? = "300"
    private var deliveryAddress: String? = "400"
    private var userId: String? = "500"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_list)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        shopId = intent.getStringExtra("ShopListId")
        orderByName = intent.getStringExtra("orderByName")
        orderByMobile = intent.getStringExtra("orderByMobile")
        deliveryAddress = intent.getStringExtra("delivery")
        userId = intent.getStringExtra("userId")
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
            userDatabase.child("OrderList").child(timestamp).setValue(headers)
                .addOnCompleteListener {
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
        userDatabase.child("OrderList").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                bool = true
                for (i in snapshot.children) {


                    val itemId = i.child("itemId").value.toString()
                    val itemName = i.child("itemName").value.toString()
                    val itemQuan = i.child("itemQuantity").value.toString()

                    if (itemQuan == "" || itemName == "") {
                        bool = false

                    }

                }

            }
        })
        btnCn.setOnClickListener {
            if (bool) {
                progressDialog.setMessage("Placing Your Order")
                progressDialog.show()

                val timestamp = System.currentTimeMillis().toString()
                val orderId = timestamp
                val orderTime = timestamp
                val orderStatus = "Pending"
                val orderCost = ""
                val orderBy = userId.toString()
                val orderTo = shopId.toString()
                val deliveryAddress = deliveryAddress.toString()
                val totalItems = list.size.toString()
                val listStatus = ""
                val obj = ModalSellerOrderList(
                    orderId,
                    orderTime,
                    orderStatus,
                    orderCost,
                    orderBy,
                    orderTo,
                    deliveryAddress,
                    totalItems,
                    listStatus
                )
                val dataReference: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("users").child(userId.toString())
                        .child("MyOrderList")
                val ref: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("seller")
                        .child(shopId.toString()).child("OrdersLists")
                ref.child(orderId).setValue(obj)
                for (i in 0 until list.size) {
                    val itemId = list[i].itemId
                    val itemName = list[i].itemName
                    val itemQuantity = list[i].itemQuantity
                    val headers = HashMap<String, String>()
                    headers["itemId"] = itemId
                    headers["itemName"] = itemName
                    headers["itemQuantity"] = itemQuantity
                    ref.child(orderId).child("ListItems").child(itemId).setValue(headers)
                    dataReference.child(orderId).child("ListItems").child(itemId).setValue(headers)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this@CreateList, "Done", Toast.LENGTH_LONG).show()
                            }
                        }
                }
                progressDialog.dismiss()
            } else {
                Toast.makeText(this@CreateList, "Some fields are empty", Toast.LENGTH_LONG).show()
            }
        }
    }
}