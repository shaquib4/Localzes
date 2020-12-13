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

class PastOrdersActivity : AppCompatActivity() {
    private lateinit var recyclerOrderDetails: RecyclerView
    private lateinit var userPastOrderAdapter: AdapterUserOrderHistory
    private lateinit var pastOrderHistoryDatabase: DatabaseReference
    private lateinit var userAuth: FirebaseAuth
    private lateinit var mOrderedItem: List<ModelOrderDetails>
    private lateinit var relativePast:RelativeLayout
    private lateinit var imgBackPast:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_orders)
        relativePast=findViewById(R.id.rl_Past_orders)
        imgBackPast=findViewById(R.id.imgBackPast)
        userAuth = FirebaseAuth.getInstance()
        val user = userAuth.currentUser
        val uid = user!!.uid
        mOrderedItem = ArrayList<ModelOrderDetails>()
        pastOrderHistoryDatabase =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("MyOrders")
        recyclerOrderDetails = findViewById(R.id.recyclerPastOrders)
        recyclerOrderDetails.layoutManager = LinearLayoutManager(this)
        pastOrderHistoryDatabase.addValueEventListener(object : ValueEventListener {
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
                    if (i.child("orderStatus").value.toString() == "Completed") {
                        (mOrderedItem as ArrayList<ModelOrderDetails>).add(obj)
                    }

                }
                if(mOrderedItem.isEmpty()){
                    recyclerOrderDetails.visibility= View.GONE
                } else{
                    relativePast.visibility=View.GONE
                    recyclerOrderDetails.visibility=View.VISIBLE
                    userPastOrderAdapter =
                        AdapterUserOrderHistory(
                            this@PastOrdersActivity,
                            mOrderedItem
                        )
                    recyclerOrderDetails.adapter = userPastOrderAdapter
                }

            }
        })
        imgBackPast.setOnClickListener {
            val intent=Intent(this,Accounts::class.java)
            startActivity(intent)
            finish()
        }
    }
}