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
                    orderHistoryDatabase.child(i.child("orderId").value.toString()).child("orderedItems")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {

                                for (j in snapshot.children) {
                                    val obj = ModelOrderedItems(
                                        j.child("productId").value.toString(),
                                        j.child("productTitle").value.toString(),
                                        j.child("finalPrice").value.toString(),
                                        j.child("priceEach").value.toString(),
                                        j.child("finalQuantity").value.toString()
                                    )
                                    (mOrderedItem as ArrayList<ModelOrderedItems>).add(obj)
                                }
                                orderHistoryDatabase.child(i.child("orderId").toString())
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onCancelled(error: DatabaseError) {

                                        }

                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val obj =
                                                ModelUserOrderDetails(
                                                    snapshot.child("orderId").value.toString(),
                                                    snapshot.child("orderTime").value.toString(),
                                                    snapshot.child("orderStatus").value.toString(),
                                                    snapshot.child("orderCost").value.toString(),
                                                    snapshot.child("orderBy").value.toString(),
                                                    snapshot.child("orderTo").value.toString(),
                                                    snapshot.child("orderQuantity").value.toString(),
                                                    mOrderedItem as ArrayList<ModelOrderedItems>
                                                )
                                            (mUserOrderHistory as ArrayList<ModelUserOrderDetails>).add(
                                                obj
                                            )
                                        }

                                    })
                                userOrderHistoryAdapter =
                                    AdapterUserOrderHistory(
                                        this@UserOrdersActivity,
                                        mUserOrderHistory
                                    )
                                recyclerOrderDetails.adapter = userOrderHistoryAdapter
                            }

                        })
                }
            }
        })
    }
}