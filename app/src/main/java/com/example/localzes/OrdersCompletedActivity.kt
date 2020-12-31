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
import com.example.localzes.Adapters.AdapterSellerOrders
import com.example.localzes.Modals.ModalSellerOrderList
import com.example.localzes.Modals.ModelOrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_orders_completed.*
import kotlinx.android.synthetic.main.activity_seller_orders.*
import util.ConnectionManager

class OrdersCompletedActivity : AppCompatActivity() {
    private lateinit var orderAuth: FirebaseAuth
    private lateinit var orderDatabaseReference: DatabaseReference
    private lateinit var ordersCompletedList: List<ModelOrderDetails>
    private lateinit var recyclerOrdersCompleted: RecyclerView
    private lateinit var adapterOrdersCompleted: AdapterSellerOrders
    private lateinit var orderCompleted: RelativeLayout
    private lateinit var back: ImageView
    private lateinit var listAdapter: AdapterListOrder
    private lateinit var listOrders: List<ModalSellerOrderList>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_completed)
        orderAuth = FirebaseAuth.getInstance()
        val user = orderAuth.currentUser
        val uid = user!!.uid
        retryCompletedOrders.setOnClickListener {
            this.recreate()
        }
        listOrders = ArrayList<ModalSellerOrderList>()
        ordersCompletedList = ArrayList<ModelOrderDetails>()
        orderCompleted = findViewById(R.id.rl_Completed_Orders)
        recyclerOrdersCompleted = findViewById(R.id.recyclerOrdersCompleted)
        back = findViewById(R.id.imgBackCompletedOrders)
        recyclerOrdersCompleted.layoutManager = LinearLayoutManager(this)
        orderDatabaseReference = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        orderDatabaseReference.child("Orders").orderByChild("orderStatus").equalTo("Completed")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {


                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    cartCompletedNo.text = "(${snapshot.childrenCount})"
                }
            })
        orderDatabaseReference.child("OrdersLists").orderByChild("orderStatus").equalTo("Completed")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    listCompletedNo.text="(${snapshot.childrenCount})"
                }
            })
        rl_cartCompleted.setOnClickListener {
            completedCartOrders()
        }
        rl_listCompleted.setOnClickListener {
            completedListOrders()
        }

        back.setOnClickListener {
            val intent = Intent(this, Home_seller::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun completedListOrders() {
        txtCartCompleted.setTextColor(this.resources.getColor(R.color.black))
        cartCompletedNo.setTextColor(this.resources.getColor(R.color.black))
        txtlistCompleted.setTextColor(this.resources.getColor(R.color.colorPrimary))
        listCompletedNo.setTextColor(this.resources.getColor(R.color.colorPrimary))
        val user = orderAuth.currentUser
        val uid = user!!.uid

        FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("OrdersLists")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    (listOrders as ArrayList<ModalSellerOrderList>).clear()
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
                        if (i.child("orderStatus").value.toString() == "Pending") {
                            (listOrders as ArrayList<ModalSellerOrderList>).add(obj)
                        }
                    }
                    if (listOrders.isEmpty()) {
                        recyclerShopOrders.visibility = View.GONE
                    } else {
                        orderCompleted.visibility = View.GONE
                        recyclerOrdersCompleted.visibility = View.VISIBLE
                        listAdapter = AdapterListOrder(this@OrdersCompletedActivity, listOrders)
                        recyclerOrdersCompleted.adapter = listAdapter
                    }
                }

            })
    }

    private fun completedCartOrders() {
        txtCartCompleted.setTextColor(this.resources.getColor(R.color.colorPrimary))
        cartCompletedNo.setTextColor(this.resources.getColor(R.color.colorPrimary))
        txtlistCompleted.setTextColor(this.resources.getColor(R.color.black))
        listCompletedNo.setTextColor(this.resources.getColor(R.color.black))
        if (ConnectionManager().checkConnectivity(this)) {
            rl_CompletedOrder.visibility = View.VISIBLE
            rl_retryCompletedOrder.visibility = View.GONE
            orderDatabaseReference.child("Orders")
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
                                if (i.child("orderStatus").value.toString() == "Completed") {
                                    (ordersCompletedList as ArrayList<ModelOrderDetails>).add(obj)
                                }

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
                })
        } else {
            rl_CompletedOrder.visibility = View.GONE
            rl_retryCompletedOrder.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, Home_seller::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        completedCartOrders()
    }
}