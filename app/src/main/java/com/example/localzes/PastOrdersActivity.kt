package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterListOrder
import com.example.localzes.Adapters.AdapterUserListOrderHistory
import com.example.localzes.Adapters.AdapterUserOrderHistory
import com.example.localzes.Modals.ModalSellerOrderList
import com.example.localzes.Modals.ModelOrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_current_orders.*
import kotlinx.android.synthetic.main.activity_past_orders.*
import util.ConnectionManager

class PastOrdersActivity : AppCompatActivity() {
    private lateinit var recyclerOrderDetails: RecyclerView
    private lateinit var userPastOrderAdapter: AdapterUserOrderHistory
    private lateinit var pastOrderHistoryDatabase: DatabaseReference
    private lateinit var userAuth: FirebaseAuth
    private lateinit var mOrderedItem: List<ModelOrderDetails>
    private lateinit var mOrderedItemList: List<ModalSellerOrderList>
    private lateinit var pastOrderListHistoryAdapter: AdapterUserListOrderHistory
    private lateinit var relativePast: RelativeLayout
    private lateinit var imgBackPast: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_orders)
        relativePast = findViewById(R.id.rl_Past_orders)
        imgBackPast = findViewById(R.id.imgBackPast)
        userAuth = FirebaseAuth.getInstance()
        val user = userAuth.currentUser
        val uid = user!!.uid
        retryPastOrders.setOnClickListener {
            this.recreate()
        }
        mOrderedItem = ArrayList<ModelOrderDetails>()
        mOrderedItemList = ArrayList<ModalSellerOrderList>()
        pastOrderHistoryDatabase =
            FirebaseDatabase.getInstance().reference.child("users").child(uid)
        recyclerOrderDetails = findViewById(R.id.recyclerPastOrders)
        recyclerOrderDetails.layoutManager = LinearLayoutManager(this)
        rl_cartPast.setOnClickListener {
            cartPastOrders()
        }
        rl_listPast.setOnClickListener {
            listPastOrders()
        }
        if (ConnectionManager().checkConnectivity(this)) {
            rl_pastOrders.visibility = View.VISIBLE
            rl_retryPastOrders.visibility = View.GONE
        } else {
            rl_pastOrders.visibility = View.GONE
            rl_retryPastOrders.visibility = View.VISIBLE
        }
        imgBackPast.setOnClickListener {
            val intent = Intent(this, Accounts::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun listPastOrders() {
        txtCartPast.setTextColor(resources.getColor(R.color.black))
        cartPastNo.setTextColor(resources.getColor(R.color.black))
        txtlistPast.setTextColor(resources.getColor(R.color.colorPrimary))
        listPastNo.setTextColor(resources.getColor(R.color.colorPrimary))
        pastOrderHistoryDatabase.child("OrdersLists")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    (mOrderedItemList as ArrayList<ModalSellerOrderList>).clear()
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
                        if (i.child("orderStatus").value.toString() == "Completed") {
                            (mOrderedItemList as ArrayList<ModalSellerOrderList>).add(obj)
                        }
                    }
                    if (mOrderedItemList.isEmpty()) {
                        recyclerOrderDetails.visibility = View.GONE
                    } else {
                        relativePast.visibility = View.GONE
                        recyclerOrderDetails.visibility = View.VISIBLE
                        pastOrderListHistoryAdapter =
                            AdapterUserListOrderHistory(this@PastOrdersActivity, mOrderedItemList)
                        recyclerOrderDetails.adapter = pastOrderListHistoryAdapter
                    }
                }
            })

    }

    private fun cartPastOrders() {
        txtCartPast.setTextColor(resources.getColor(R.color.colorPrimary))
        cartPastNo.setTextColor(resources.getColor(R.color.colorPrimary))
        txtlistPast.setTextColor(resources.getColor(R.color.black))
        listPastNo.setTextColor(resources.getColor(R.color.black))
        pastOrderHistoryDatabase.child("MyOrders")
            .addValueEventListener(object : ValueEventListener {
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
                    if (mOrderedItem.isEmpty()) {
                        recyclerOrderDetails.visibility = View.GONE
                    } else {
                        relativePast.visibility = View.GONE
                        recyclerOrderDetails.visibility = View.VISIBLE
                        userPastOrderAdapter =
                            AdapterUserOrderHistory(
                                this@PastOrdersActivity,
                                mOrderedItem
                            )
                        recyclerOrderDetails.adapter = userPastOrderAdapter
                    }

                }
            })

    }

    override fun onStart() {
        super.onStart()
        cartPastOrders()
    }

    override fun onBackPressed() {
        val intent = Intent(this, Accounts::class.java)
        startActivity(intent)
        finish()
    }
}