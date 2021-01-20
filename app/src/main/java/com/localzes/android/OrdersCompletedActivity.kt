package com.localzes.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.localzes.android.Adapters.AdapterListOrder
import com.localzes.android.Adapters.AdapterSellerOrders
import com.localzes.android.Modals.ModalSellerOrderList
import com.localzes.android.Modals.ModelOrderDetails
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
    private lateinit var listAdapter: AdapterListOrder
    private lateinit var listOrders: List<ModalSellerOrderList>
    private lateinit var searchCartCompletedOrders: EditText
    private lateinit var searchListCompletedOrders: EditText
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
        searchCartCompletedOrders = findViewById(R.id.searchCartCompletedOrders)
        searchListCompletedOrders = findViewById(R.id.searchListCompletedOrders)
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
                    listCompletedNo.text = "(${snapshot.childrenCount})"
                }
            })
        rl_cartCompleted.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)) {
                completedCartOrders()
            } else {
                rl_CompletedOrder.visibility = View.GONE
                rl_retryCompletedOrder.visibility = View.VISIBLE
            }
        }
        searchListCompletedOrders.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchListCompleted(s.toString())
            }

        })
        searchCartCompletedOrders.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchCartCompleted(s.toString())
            }

        })
        rl_listCompleted.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)) {
                rl_CompletedOrder.visibility = View.VISIBLE
                rl_retryCompletedOrder.visibility = View.GONE
                completedListOrders()
            } else {
                rl_CompletedOrder.visibility = View.GONE
                rl_retryCompletedOrder.visibility = View.VISIBLE
            }
        }

        back.setOnClickListener {
            val intent = Intent(this, Home_seller::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun searchCartCompleted(s: String) {
        val user = orderAuth.currentUser
        val uid = user!!.uid
        val querySellerDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("Orders")
                .orderByChild("orderId").startAt(s).endAt(s + "\uF8FF")
        querySellerDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (ordersCompletedList as ArrayList<ModelOrderDetails>).clear()
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
                        (ordersCompletedList as ArrayList<ModelOrderDetails>).add(obj)
                    }
                }
                adapterOrdersCompleted =
                    AdapterSellerOrders(
                        this@OrdersCompletedActivity,
                        ordersCompletedList
                    )
                recyclerOrdersCompleted.adapter = adapterOrdersCompleted
            }
        })
    }

    private fun searchListCompleted(s: String) {
        val user = orderAuth.currentUser
        val uid = user!!.uid
        val querySellerDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("OrdersLists")
                .orderByChild("orderId").startAt(s).endAt(s + "\uF8FF")
        querySellerDatabase.addValueEventListener(object : ValueEventListener {
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
                        i.child("orderByMobile").value.toString(),
                        i.child("paymentMode").value.toString()
                    )
                    if (i.child("orderStatus").value.toString() == "Completed") {
                        (listOrders as ArrayList<ModalSellerOrderList>).add(obj)
                    }
                }
                listAdapter = AdapterListOrder(this@OrdersCompletedActivity, listOrders)
                recyclerOrdersCompleted.adapter = listAdapter
            }
        })
    }

    private fun completedListOrders() {
        searchCartCompletedOrders.visibility = View.GONE
        searchListCompletedOrders.visibility = View.VISIBLE
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
                            i.child("orderByMobile").value.toString(),
                            i.child("paymentMode").value.toString()


                        )
                        if (i.child("orderStatus").value.toString() == "Completed") {
                            (listOrders as ArrayList<ModalSellerOrderList>).add(obj)
                        }
                    }
                    (listOrders as ArrayList<ModalSellerOrderList>).reverse()
                    if (listOrders.isEmpty()) {
                        recyclerOrdersCompleted.visibility = View.GONE
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
        searchListCompletedOrders.visibility = View.GONE
        searchCartCompletedOrders.visibility = View.VISIBLE
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
                            (ordersCompletedList as ArrayList<ModelOrderDetails>).reverse()
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