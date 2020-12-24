package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterSellerOrders
import com.example.localzes.Modals.ModelOrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_order_out_for_delivery.*
import util.ConnectionManager

class OrderOutForDeliveryActivity : AppCompatActivity() {
    private lateinit var orderAuth: FirebaseAuth
    private lateinit var orderDatabaseReference: DatabaseReference
    private lateinit var ordersOutForDeliveryList: List<ModelOrderDetails>
    private lateinit var recyclerOutForDelivery: RecyclerView
    private lateinit var adapterOutForDelivery: AdapterSellerOrders
    private lateinit var backOutForDelivery: ImageView
    private lateinit var relativeOutForDelivery: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_out_for_delivery)
        orderAuth = FirebaseAuth.getInstance()
        val user = orderAuth.currentUser
        val uid = user!!.uid
        retryDeliveryOrders.setOnClickListener {
            this.recreate()
        }
        ordersOutForDeliveryList = ArrayList<ModelOrderDetails>()
        recyclerOutForDelivery = findViewById(R.id.recyclerOutForDelivery)
        backOutForDelivery = findViewById(R.id.imgBackOutForDelivery)
        relativeOutForDelivery = findViewById(R.id.rl_Out_For_delivery)
        recyclerOutForDelivery.layoutManager = LinearLayoutManager(this)
        orderDatabaseReference = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        if (ConnectionManager().checkConnectivity(this)){
            rl_DeliveryOrder.visibility=View.VISIBLE
            rl_retryDeliveryOrder.visibility=View.GONE
            orderDatabaseReference.child("Orders").orderByChild("orderStatus")
            .equalTo("Out For Delivery").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        (ordersOutForDeliveryList as ArrayList<ModelOrderDetails>).clear()
                        for (i in snapshot.children) {
                            val obj =
                                ModelOrderDetails(
                                    i.child("orderId").value.toString(),
                                    i.child("orderTime").value.toString(),
                                    i.child("orderStatus").value.toString(),
                                    i.child("orderCost").value.toString(),
                                    i.child("orderBy").value.toString(),
                                    i.child("orderTo").value.toString(),
                                    i.child("orderQuantity").value.toString(),
                                    i.child("deliveryAddress").value.toString(),
                                    i.child("paymentMode").value.toString(),
                                    i.child("orderByName").value.toString(),
                                    i.child("orderByMobile").value.toString()
                                )
                            (ordersOutForDeliveryList as ArrayList<ModelOrderDetails>).add(obj)
                        }
                        if (ordersOutForDeliveryList.isEmpty()) {
                            recyclerOutForDelivery.visibility = View.GONE
                        } else {
                            relativeOutForDelivery.visibility = View.GONE
                            recyclerOutForDelivery.visibility = View.VISIBLE
                            adapterOutForDelivery =
                                AdapterSellerOrders(
                                    this@OrderOutForDeliveryActivity,
                                    ordersOutForDeliveryList
                                )
                            recyclerOutForDelivery.adapter = adapterOutForDelivery
                        }
                    }
                }
            })}else{
            rl_DeliveryOrder.visibility=View.GONE
            rl_retryDeliveryOrder.visibility=View.VISIBLE
        }
        backOutForDelivery.setOnClickListener {
            val intent = Intent(this, Home_seller::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, Home_seller::class.java)
        startActivity(intent)
        finish()
    }
}