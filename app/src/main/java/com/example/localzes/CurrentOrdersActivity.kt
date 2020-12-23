package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterUserOrderHistory
import com.example.localzes.Modals.ModelOrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_current_orders.*
import util.ConnectionManager

class CurrentOrdersActivity : AppCompatActivity() {
    private lateinit var recyclerOrderDetails: RecyclerView
    private lateinit var userCurrentOrderAdapter: AdapterUserOrderHistory
    private lateinit var currentOrderHistoryDatabase: DatabaseReference
    private lateinit var userAuth: FirebaseAuth
    private lateinit var mOrderedItem: List<ModelOrderDetails>
    private lateinit var relativeCurrent: RelativeLayout
    private lateinit var imgBackCurrent: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_orders)
        mOrderedItem = ArrayList<ModelOrderDetails>()
        recyclerOrderDetails = findViewById(R.id.recyclerCurrentOrders)
        relativeCurrent = findViewById(R.id.rl_Current_orders)
        imgBackCurrent = findViewById(R.id.imgBackCurrent)
        recyclerOrderDetails.layoutManager = LinearLayoutManager(this)
        userAuth = FirebaseAuth.getInstance()
        val user = userAuth.currentUser
        val uid = user!!.uid
        retryCurrentOrders.setOnClickListener {
            this.recreate()
        }
        currentOrderHistoryDatabase =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("MyOrders")
        if (ConnectionManager().checkConnectivity(this)){
            rl_currentOrders.visibility=View.VISIBLE
            rl_retryCurrentOrders.visibility=View.GONE
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
                        i.child("paymentMode").value.toString(),
                        i.child("orderByName").value.toString(),
                        i.child("orderByMobile").value.toString()
                    )
                    if (i.child("orderStatus").value.toString() == "Pending" || i.child("orderStatus").value.toString() == "Accepted" || i.child(
                            "orderStatus"
                        ).value.toString() == "Out For Delivery"
                    ) {
                        (mOrderedItem as ArrayList<ModelOrderDetails>).add(obj)
                    }
                }
                if (mOrderedItem.isEmpty()) {
                    recyclerOrderDetails.visibility = View.GONE
                } else {
                    relativeCurrent.visibility = View.GONE
                    recyclerOrderDetails.visibility = View.VISIBLE
                    userCurrentOrderAdapter =
                        AdapterUserOrderHistory(
                            this@CurrentOrdersActivity,
                            mOrderedItem
                        )
                    recyclerOrderDetails.adapter = userCurrentOrderAdapter
                }

            }
        })}else{
            rl_currentOrders.visibility=View.GONE
            rl_retryCurrentOrders.visibility=View.VISIBLE
        }
        imgBackCurrent.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, Accounts::class.java)
        startActivity(intent)
        finish()
    }
}