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
import kotlinx.android.synthetic.main.activity_orders_completed.*
import util.ConnectionManager

class OrdersCompletedActivity : AppCompatActivity() {
    private lateinit var orderAuth: FirebaseAuth
    private lateinit var orderDatabaseReference: DatabaseReference
    private lateinit var ordersCompletedList: List<ModelOrderDetails>
    private lateinit var recyclerOrdersCompleted: RecyclerView
    private lateinit var adapterOrdersCompleted: AdapterSellerOrders
    private lateinit var orderCompleted: RelativeLayout
    private lateinit var back: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_completed)
        orderAuth = FirebaseAuth.getInstance()
        val user = orderAuth.currentUser
        val uid = user!!.uid
        retryCompletedOrders.setOnClickListener {
            this.recreate()
        }
        ordersCompletedList = ArrayList<ModelOrderDetails>()
        orderCompleted = findViewById(R.id.rl_Completed_Orders)
        recyclerOrdersCompleted = findViewById(R.id.recyclerOrdersCompleted)
        back = findViewById(R.id.imgBackCompletedOrders)
        recyclerOrdersCompleted.layoutManager = LinearLayoutManager(this)
        orderDatabaseReference = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        if (ConnectionManager().checkConnectivity(this)){
            rl_CompletedOrder.visibility=View.VISIBLE
            rl_retryCompletedOrder.visibility=View.GONE
            orderDatabaseReference.child("Orders").orderByChild("orderStatus").equalTo("Completed")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        (ordersCompletedList as ArrayList<ModelOrderDetails>).clear()
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
                            (ordersCompletedList as ArrayList<ModelOrderDetails>).add(obj)

                        }
                        if (ordersCompletedList.isEmpty()) {
                            recyclerOrdersCompleted.visibility = View.GONE
                        } else {
                            orderCompleted.visibility = View.GONE
                            recyclerOrdersCompleted.visibility = View.VISIBLE
                            adapterOrdersCompleted =
                                AdapterSellerOrders(
                                    this@OrdersCompletedActivity,
                                    ordersCompletedList
                                )
                            recyclerOrdersCompleted.adapter = adapterOrdersCompleted
                        }
                    }
                }
            })}else{
            rl_CompletedOrder.visibility=View.GONE
            rl_retryCompletedOrder.visibility=View.VISIBLE
        }
        back.setOnClickListener {
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