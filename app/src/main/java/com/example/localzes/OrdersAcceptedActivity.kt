package com.example.localzes

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
import com.example.localzes.Adapters.AdapterListOrder
import com.example.localzes.Adapters.AdapterSellerOrders
import com.example.localzes.Modals.ModalSellerOrderList
import com.example.localzes.Modals.ModelOrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_orders_accepted.*
import kotlinx.android.synthetic.main.activity_seller_orders.*
import util.ConnectionManager

class OrdersAcceptedActivity : AppCompatActivity() {
    private lateinit var orderAuth: FirebaseAuth
    private lateinit var orderDatabaseReference: DatabaseReference
    private lateinit var ordersAcceptedList: List<ModelOrderDetails>
    private lateinit var recyclerOrdersAccepted: RecyclerView
    private lateinit var adapterOrderAccepted: AdapterSellerOrders
    private lateinit var backAccepted: ImageView
    private lateinit var orderAccepted: RelativeLayout
    private lateinit var listAdapter: AdapterListOrder
    private lateinit var listOrders: List<ModalSellerOrderList>
    private lateinit var editSearchCartAccepted: EditText
    private lateinit var editSearchListAccepted: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_accepted)
        orderAuth = FirebaseAuth.getInstance()
        val user = orderAuth.currentUser
        val uid = user!!.uid
        retryAcceptedOrders.setOnClickListener {
            this.recreate()
        }

        ordersAcceptedList = ArrayList<ModelOrderDetails>()
        recyclerOrdersAccepted = findViewById(R.id.recyclerOrdersAccepted)
        backAccepted = findViewById(R.id.imgBackAccepted)
        orderAccepted = findViewById(R.id.rl_Accepted_Orders)
        editSearchCartAccepted = findViewById(R.id.searchCartAccepted)
        editSearchListAccepted = findViewById(R.id.searchListAccepted)
        listOrders = ArrayList<ModalSellerOrderList>()
        recyclerOrdersAccepted.layoutManager = LinearLayoutManager(this)
        orderDatabaseReference = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        orderDatabaseReference.child("Orders").orderByChild("orderStatus").equalTo("Accepted")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    cartAcceptedNo.text = "(${snapshot.childrenCount})"
                }

            })
        orderDatabaseReference.child("OrdersLists").orderByChild("orderStatus").equalTo("Accepted")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    listAcceptedNo.text = "(${snapshot.childrenCount})"
                }
            })

        rl_listAccepted.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)) {
                rl_AcceptedOrder.visibility = View.VISIBLE
                rl_retryAcceptedOrder.visibility = View.GONE
                acceptedListOrder()
            } else {
                rl_AcceptedOrder.visibility = View.GONE
                rl_retryAcceptedOrder.visibility = View.VISIBLE
            }
        }
        rl_cartAccepted.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)) {
                acceptedCartOrder()
            } else {
                rl_AcceptedOrder.visibility = View.GONE
                rl_retryAcceptedOrder.visibility = View.VISIBLE
            }
        }
        editSearchListAccepted.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                searchListAcceptedOrders(s.toString())
            }

        })
        editSearchCartAccepted.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchCartAcceptedOrders(s.toString())
            }

        })

        backAccepted.setOnClickListener {
            val intent = Intent(this, Home_seller::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun searchListAcceptedOrders(s: String) {
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
                    if (i.child("orderStatus").value.toString() == "Accepted") {
                        (listOrders as ArrayList<ModalSellerOrderList>).add(obj)
                    }
                }
                listAdapter = AdapterListOrder(this@OrdersAcceptedActivity, listOrders)
                recyclerOrdersAccepted.adapter = listAdapter
            }
        })
    }

    private fun searchCartAcceptedOrders(s: String) {
        val user = orderAuth.currentUser
        val uid = user!!.uid
        val querySellerDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("Orders")
                .orderByChild("orderId").startAt(s).endAt(s + "\uF8FF")
        querySellerDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (ordersAcceptedList as ArrayList<ModelOrderDetails>).clear()
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
                    if (i.child("orderStatus").value.toString() == "Accepted") {
                        (ordersAcceptedList as ArrayList<ModelOrderDetails>).add(obj)
                    }
                }
                adapterOrderAccepted =
                    AdapterSellerOrders(
                        this@OrdersAcceptedActivity,
                        ordersAcceptedList
                    )
                recyclerOrdersAccepted.adapter = adapterOrderAccepted
            }
        })
    }

    private fun acceptedListOrder() {
        editSearchCartAccepted.visibility = View.GONE
        editSearchListAccepted.visibility = View.VISIBLE
        txtCartAccepted.setTextColor(this.resources.getColor(R.color.black))
        cartAcceptedNo.setTextColor(this.resources.getColor(R.color.black))
        txtlistAccepted.setTextColor(this.resources.getColor(R.color.colorPrimary))
        listAcceptedNo.setTextColor(this.resources.getColor(R.color.colorPrimary))
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
                        if (i.child("orderStatus").value.toString() == "Accepted") {
                            (listOrders as ArrayList<ModalSellerOrderList>).add(obj)
                        }
                    }
                    if (listOrders.isEmpty()) {
                        recyclerOrdersAccepted.visibility = View.GONE
                    } else {
                        orderAccepted.visibility = View.GONE
                        recyclerOrdersAccepted.visibility = View.VISIBLE
                        listAdapter = AdapterListOrder(this@OrdersAcceptedActivity, listOrders)
                        recyclerOrdersAccepted.adapter = listAdapter
                    }
                }

            })
    }

    private fun acceptedCartOrder() {
        editSearchListAccepted.visibility = View.GONE
        editSearchCartAccepted.visibility = View.VISIBLE
        txtCartAccepted.setTextColor(this.resources.getColor(R.color.colorPrimary))
        cartAcceptedNo.setTextColor(this.resources.getColor(R.color.colorPrimary))
        txtlistAccepted.setTextColor(this.resources.getColor(R.color.black))
        listAcceptedNo.setTextColor(this.resources.getColor(R.color.black))
        if (ConnectionManager().checkConnectivity(this)) {
            rl_AcceptedOrder.visibility = View.VISIBLE
            rl_retryAcceptedOrder.visibility = View.GONE
            orderDatabaseReference.child("Orders")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            (ordersAcceptedList as ArrayList<ModelOrderDetails>).clear()
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
                                if (i.child("orderStatus").value.toString() == "Accepted") {
                                    (ordersAcceptedList as ArrayList<ModelOrderDetails>).add(obj)
                                }
                            }
                            if (ordersAcceptedList.isEmpty()) {
                                recyclerOrdersAccepted.visibility = View.GONE
                                orderAccepted.visibility = View.VISIBLE
                            } else {
                                orderAccepted.visibility = View.GONE
                                recyclerOrdersAccepted.visibility = View.VISIBLE
                                adapterOrderAccepted =
                                    AdapterSellerOrders(
                                        this@OrdersAcceptedActivity,
                                        ordersAcceptedList
                                    )
                                recyclerOrdersAccepted.adapter = adapterOrderAccepted
                            }

                        }
                    }
                })
        } else {
            rl_AcceptedOrder.visibility = View.GONE
            rl_retryAcceptedOrder.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, Home_seller::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        if (ConnectionManager().checkConnectivity(this)) {
            rl_AcceptedOrder.visibility = View.VISIBLE
            rl_retryAcceptedOrder.visibility = View.GONE
            acceptedCartOrder()
        } else {
            rl_AcceptedOrder.visibility = View.GONE
            rl_retryAcceptedOrder.visibility = View.VISIBLE
        }
    }
}