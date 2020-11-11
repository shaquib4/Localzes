package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserOrdersActivity : AppCompatActivity() {
    private lateinit var mUserOrderHistory: List<ModelUserOrderDetails>
    private lateinit var recyclerOrderDetails: RecyclerView
    private lateinit var userOrderHistoryAdapter: AdapterUserOrderHistory
    private lateinit var orderHistoryDatabase: DatabaseReference
    private lateinit var userAuth: FirebaseAuth
    private lateinit var mOrderedItem: List<ModelOrderedItems>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_orders)
        recyclerOrderDetails = findViewById(R.id.recyclerUserOrderHistory)
        recyclerOrderDetails.layoutManager = LinearLayoutManager(this)
        userAuth = FirebaseAuth.getInstance()
        val user = userAuth.currentUser
        val uid = user!!.uid
        mUserOrderHistory = ArrayList<ModelUserOrderDetails>()
        mOrderedItem = ArrayList<ModelOrderedItems>()
        orderHistoryDatabase =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("MyOrders")
        orderHistoryDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    (mUserOrderHistory as ArrayList<ModelUserOrderDetails>).clear()
                    val databaseReference: DatabaseReference =
                        FirebaseDatabase.getInstance().reference.child("users").child(uid)
                            .child("MyOrders").child(i.child("orderId").value.toString())
                            .child("orderedItems")
                    databaseReference.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (i in snapshot.children) {
                                val obj = ModelOrderedItems(
                                    i.child("productId").value.toString(),
                                    i.child("productTitle").value.toString(),
                                    i.child("finalPrice").value.toString(),
                                    i.child("priceEach").value.toString(),
                                    i.child("finalQuantity").value.toString()
                                )
                                (mOrderedItem as ArrayList<ModelOrderedItems>).add(obj)
                            }
                        }

                    })
                    val obj = ModelUserOrderDetails(
                        i.child("orderId").value.toString(),
                        i.child("orderTime").value.toString(),
                        i.child("orderStatus").value.toString(),
                        i.child("orderCost").value.toString(),
                        i.child("orderBy").value.toString(),
                        i.child("orderTo").value.toString(),
                        i.child("orderQuantity").value.toString(),
                        mOrderedItem as ArrayList<ModelOrderedItems>
                    )
                    (mUserOrderHistory as ArrayList<ModelUserOrderDetails>).add(obj)
                }
                userOrderHistoryAdapter =
                    AdapterUserOrderHistory(this@UserOrdersActivity, mUserOrderHistory)
                recyclerOrderDetails.adapter = userOrderHistoryAdapter
            }
        })
    }
}