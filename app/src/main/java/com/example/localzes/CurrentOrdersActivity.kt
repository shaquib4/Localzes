package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterUserListOrderHistory
import com.example.localzes.Adapters.AdapterUserOrderHistory
import com.example.localzes.Modals.ModalSellerOrderList
import com.example.localzes.Modals.ModelOrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_current_orders.*
import kotlinx.android.synthetic.main.activity_orders_completed.*
import util.ConnectionManager

class CurrentOrdersActivity : AppCompatActivity() {
    private lateinit var recyclerOrderDetails: RecyclerView
    private lateinit var userCurrentOrderAdapter: AdapterUserOrderHistory
    private lateinit var currentOrderHistoryDatabase: DatabaseReference
    private lateinit var userAuth: FirebaseAuth
    private lateinit var mOrderedList: List<ModalSellerOrderList>
    private lateinit var adapterUserListOrder: AdapterUserListOrderHistory
    private lateinit var currentListOrderDatabase: DatabaseReference
    private lateinit var mOrderedItem: List<ModelOrderDetails>
    private lateinit var relativeCurrent: RelativeLayout
    private lateinit var imgBackCurrent: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_orders)
        mOrderedItem = ArrayList<ModelOrderDetails>()
        mOrderedList = ArrayList<ModalSellerOrderList>()
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
        currentListOrderDatabase =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("MyOrderList")
        currentOrderHistoryDatabase =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("MyOrders")
        rl_cartCurrent.setOnClickListener {
            currentCartOrder()
        }
        rl_listCurrent.setOnClickListener {
            currentListOrder()
        }


        imgBackCurrent.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
    }

    private fun currentListOrder() {
        txtCartCurrent.setTextColor(this.resources.getColor(R.color.black))
        cartCurrentNo.setTextColor(this.resources.getColor(R.color.black))
        txtlistCurrent.setTextColor(this.resources.getColor(R.color.colorPrimary))
        listCurrentNo.setTextColor(this.resources.getColor(R.color.colorPrimary))
        currentListOrderDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (mOrderedList as ArrayList<ModalSellerOrderList>).clear()
                for (i in snapshot.children) {
                    val obj = ModalSellerOrderList(
                        i.child("orderId").value.toString(),
                        i.child("orderTime").value.toString(),
                        i.child("orderStatus").value.toString(),
                        i.child("orderCost").value.toString(),
                        i.child("orderBy").value.toString(),
                        i.child("orderTo").value.toString(),
                        i.child("deliveryAddress").value.toString(),
                        i.child("totalItems").value.toString(),
                        i.child("listStatus").value.toString(),
                        i.child("orderByName").value.toString(),
                        i.child("orderByMobile").value.toString()
                    )
                    if (i.child("orderStatus").value.toString() == "Pending" || i.child("orderStatus").value.toString() == "Accepted" || i.child(
                            "orderStatus"
                        ).value.toString() == "Out For Delivery"
                    ) {
                        (mOrderedList as ArrayList<ModalSellerOrderList>).add(obj)
                    }
                }
                if (mOrderedList.isEmpty()) {
                    recyclerOrderDetails.visibility = View.GONE
                } else {
                    relativeCurrent.visibility = View.GONE
                    recyclerOrderDetails.visibility = View.VISIBLE
                    adapterUserListOrder =
                        AdapterUserListOrderHistory(this@CurrentOrdersActivity, mOrderedList)
                    recyclerOrderDetails.adapter = adapterUserListOrder
                }
            }
        })
    }

    private fun currentCartOrder() {
        txtCartCurrent.setTextColor(this.resources.getColor(R.color.colorPrimary))
        cartCurrentNo.setTextColor(this.resources.getColor(R.color.colorPrimary))
        txtlistCurrent.setTextColor(this.resources.getColor(R.color.black))
        listCurrentNo.setTextColor(this.resources.getColor(R.color.black))
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
    }

    override fun onBackPressed() {
        val intent = Intent(this, Accounts::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        currentCartOrder()
    }
}