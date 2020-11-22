package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OrderOutForDeliveryActivity : AppCompatActivity() {
    private lateinit var orderAuth: FirebaseAuth
    private lateinit var orderDatabaseReference: DatabaseReference
    private lateinit var ordersOutForDeliveryList: List<ModelOrderDetails>
    private lateinit var recyclerOutForDelivery: RecyclerView
    private lateinit var adapterOutForDelivery: AdapterSellerOrders
    private lateinit var backOutForDelivery:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_out_for_delivery)
        orderAuth = FirebaseAuth.getInstance()
        val user = orderAuth.currentUser
        val uid = user!!.uid
        ordersOutForDeliveryList = ArrayList<ModelOrderDetails>()
        recyclerOutForDelivery = findViewById(R.id.recyclerOutForDelivery)
        backOutForDelivery=findViewById(R.id.imgBackOutForDelivery)
        recyclerOutForDelivery.layoutManager = LinearLayoutManager(this)
        orderDatabaseReference = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        orderDatabaseReference.child("Orders").orderByChild("orderStatus")
            .equalTo("Out For Delivery").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        (ordersOutForDeliveryList as ArrayList<ModelOrderDetails>).clear()
                        for (i in snapshot.children) {
                            val obj = ModelOrderDetails(
                                i.child("orderId").value.toString(),
                                i.child("orderTime").value.toString(),
                                i.child("orderStatus").value.toString(),
                                i.child("orderCost").value.toString(),
                                i.child("orderBy").value.toString(),
                                i.child("orderTo").value.toString(),
                                i.child("orderQuantity").value.toString(),
                                i.child("deliveryAddress").value.toString()
                            )
                            (ordersOutForDeliveryList as ArrayList<ModelOrderDetails>).add(obj)
                        }
                        adapterOutForDelivery=AdapterSellerOrders(this@OrderOutForDeliveryActivity,ordersOutForDeliveryList)
                        recyclerOutForDelivery.adapter=adapterOutForDelivery
                    }
                }
            })
    }
}