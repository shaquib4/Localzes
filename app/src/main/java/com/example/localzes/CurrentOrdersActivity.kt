package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterUserOrderHistory
import com.example.localzes.Modals.ModelOrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CurrentOrdersActivity : AppCompatActivity() {
    private lateinit var recyclerOrderDetails: RecyclerView
    private lateinit var userCurrentOrderAdapter: AdapterUserOrderHistory
    private lateinit var currentOrderHistoryDatabase: DatabaseReference
    private lateinit var userAuth: FirebaseAuth
    private lateinit var mOrderedItem: List<ModelOrderDetails>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_orders)
        mOrderedItem = ArrayList<ModelOrderDetails>()
        recyclerOrderDetails = findViewById(R.id.recyclerCurrentOrders)
        recyclerOrderDetails.layoutManager=LinearLayoutManager(this)
        userAuth = FirebaseAuth.getInstance()
        val user = userAuth.currentUser
        val uid = user!!.uid
        currentOrderHistoryDatabase =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("MyOrders")
        /*currentOrderHistoryDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    (userCurrentOrdersHistory as ArrayList<ModelUserOrderDetails>).clear()
                    val a = i.child("orderId").value.toString()

                    val databaseReference: DatabaseReference =
                        FirebaseDatabase.getInstance().reference.child("users").child(uid)
                            .child("MyOrders").child(a)
                            .child("orderedItems")
                    databaseReference.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            (mOrderedItem as ArrayList<ModelOrderedItems>).clear()
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
                    if (a == "Accepted" || a == "Pending" || a == "Out For Delivery") {
                        (userCurrentOrdersHistory as ArrayList<ModelUserOrderDetails>).add(obj)
                    }
                }
                userCurrentOrderAdapter =
                    AdapterUserOrderHistory(this@CurrentOrdersActivity, userCurrentOrdersHistory)
                recyclerOrderDetails.adapter = userCurrentOrderAdapter
            }
        })*/
        currentOrderHistoryDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                    (mOrderedItem as ArrayList<ModelOrderDetails>).clear()
                    for (i in snapshot.children) {
                        val obj = ModelOrderDetails(
                            i.child("orderId").value.toString(),
                            i.child("orderTime").value.toString(),
                            i.child("orderStatus").value.toString(),
                            i.child("orderCost").value.toString(),
                            i.child("orderBy").value.toString(),
                            i.child("orderTo").value.toString(),
                            i.child("orderQuantity").value.toString(),
                            i.child("deliveryAddress").value.toString(),
                            i.child("paymentMode").value.toString()
                        )
                        if (i.child("orderStatus").value.toString() == "Pending" || i.child("orderStatus").value.toString() == "Accepted" || i.child(
                                "orderStatus"
                            ).value.toString() == "Out For Delivery"
                        ) {
                            (mOrderedItem as ArrayList<ModelOrderDetails>).add(obj)
                        }
                    }
                    userCurrentOrderAdapter=
                        AdapterUserOrderHistory(
                            this@CurrentOrdersActivity,
                            mOrderedItem
                        )
                    recyclerOrderDetails.adapter=userCurrentOrderAdapter
            }
        })
    }
}