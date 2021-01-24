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
    private lateinit var adapterOutForDeliveryList: AdapterListOrder
    private lateinit var listOrders: List<ModalSellerOrderList>
    private lateinit var searchCartOFD: EditText
    private lateinit var searchListOFD: EditText
    private var Uid: String? = "500"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_out_for_delivery)
        /*orderAuth = FirebaseAuth.getInstance()
        val user = orderAuth.currentUser
        val uid = user!!.uid*/
        Uid = intent.getStringExtra("UID")

        retryDeliveryOrders.setOnClickListener {
            this.recreate()
        }
        ordersOutForDeliveryList = ArrayList<ModelOrderDetails>()
        listOrders = ArrayList<ModalSellerOrderList>()
        recyclerOutForDelivery = findViewById(R.id.recyclerOutForDelivery)
        backOutForDelivery = findViewById(R.id.imgBackOutForDelivery)
        relativeOutForDelivery = findViewById(R.id.rl_Out_For_delivery)
        searchCartOFD = findViewById(R.id.searchCartOFD)
        searchListOFD = findViewById(R.id.searchListOFD)
        recyclerOutForDelivery.layoutManager = LinearLayoutManager(this)

        orderDatabaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(Uid.toString())
/*        orderDatabaseReference.child("Orders").orderByChild("orderStatus")
            .equalTo("Out For Delivery").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    cartOutNo.text = "(${snapshot.childrenCount})"

                }

            })
        orderDatabaseReference.child("OrdersLists").orderByChild("orderStatus")
            .equalTo("Out For Delivery").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    listOutNo.text = "(${snapshot.childrenCount})"
                }

            })*/
        rl_listOut.setOnClickListener {

            if (ConnectionManager().checkConnectivity(this)) {
                rl_Out_For_delivery.visibility = View.VISIBLE
                rl_retryDeliveryOrder.visibility = View.GONE
                countListOrders()
                countCartOrders()
                listOutForDeliveryOrders()
            } else {
                rl_Out_For_delivery.visibility = View.GONE
                rl_retryDeliveryOrder.visibility = View.VISIBLE
            }

        }
        rl_cartOut.setOnClickListener {

            if (ConnectionManager().checkConnectivity(this)) {
                countCartOrders()
                countListOrders()
                cartOutForDeliveryOrders()
            } else {
                rl_DeliveryOrder.visibility = View.GONE
                rl_retryDeliveryOrder.visibility = View.VISIBLE
            }

        }
        searchCartOFD.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchCartOrdersOFD(s.toString())
            }

        })
        searchListOFD.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchListOrdersOFD(s.toString())
            }

        })
        if (ConnectionManager().checkConnectivity(this)) {
            rl_DeliveryOrder.visibility = View.VISIBLE
            rl_retryDeliveryOrder.visibility = View.GONE

        } else {
            rl_DeliveryOrder.visibility = View.GONE
            rl_retryDeliveryOrder.visibility = View.VISIBLE
        }
        backOutForDelivery.setOnClickListener {
            val intent = Intent(this, Home_seller::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun searchListOrdersOFD(s: String) {
        val querySellerDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(Uid.toString())
                .child("OrdersLists")
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
                    if (i.child("orderStatus").value.toString() == "Out For Delivery") {
                        (listOrders as ArrayList<ModalSellerOrderList>).add(obj)
                    }
                }
                (listOrders as ArrayList<ModalSellerOrderList>).reverse()
                adapterOutForDeliveryList =
                    AdapterListOrder(this@OrderOutForDeliveryActivity, listOrders)
                recyclerOutForDelivery.adapter = adapterOutForDeliveryList
            }
        })
    }

    private fun searchCartOrdersOFD(s: String) {
        val querySellerDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(Uid.toString())
                .child("Orders")
                .orderByChild("orderId").startAt(s).endAt(s + "\uF8FF")
        querySellerDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
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
                        i.child("deliveryAddress").value.toString(),
                        i.child("paymentMode").value.toString(),
                        i.child("orderByName").value.toString(),
                        i.child("orderByMobile").value.toString()
                    )
                    if (i.child("orderStatus").value.toString() == "Out For Delivery") {
                        (ordersOutForDeliveryList as ArrayList<ModelOrderDetails>).add(obj)
                    }
                }
                (ordersOutForDeliveryList as ArrayList<ModelOrderDetails>).reverse()
                adapterOutForDelivery =
                    AdapterSellerOrders(
                        this@OrderOutForDeliveryActivity,
                        ordersOutForDeliveryList
                    )
                recyclerOutForDelivery.adapter = adapterOutForDelivery
            }

        })
    }

    private fun cartOutForDeliveryOrders() {
        searchListOFD.visibility = View.GONE
        searchCartOFD.visibility = View.VISIBLE
        txtCartOut.setTextColor(resources.getColor(R.color.colorPrimary))
        cartOutNo.setTextColor(resources.getColor(R.color.colorPrimary))
        txtlistOut.setTextColor(resources.getColor(R.color.black))
        listOutNo.setTextColor(resources.getColor(R.color.black))
        val user = orderAuth.currentUser
        val uid = user!!.uid
        FirebaseDatabase.getInstance().reference.child("seller").child(Uid.toString())
            .child("Orders").addValueEventListener(object : ValueEventListener {
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
                            if (i.child("orderStatus").value.toString() == "Out For Delivery") {
                                (ordersOutForDeliveryList as ArrayList<ModelOrderDetails>).add(obj)
                            }
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
            })
    }

    private fun listOutForDeliveryOrders() {
        searchCartOFD.visibility = View.GONE
        searchListOFD.visibility = View.VISIBLE
        txtCartOut.setTextColor(resources.getColor(R.color.black))
        cartOutNo.setTextColor(resources.getColor(R.color.black))
        txtlistOut.setTextColor(resources.getColor(R.color.colorPrimary))
        listOutNo.setTextColor(resources.getColor(R.color.colorPrimary))
        orderDatabaseReference.child("OrdersLists")
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
                        if (i.child("orderStatus").value.toString() == "Out For Delivery") {
                            (listOrders as ArrayList<ModalSellerOrderList>).add(obj)
                        }
                    }
                    if (listOrders.isEmpty()) {
                        recyclerOutForDelivery.visibility = View.GONE
                    } else {
                        relativeOutForDelivery.visibility = View.GONE
                        recyclerOutForDelivery.visibility = View.VISIBLE
                        adapterOutForDeliveryList =
                            AdapterListOrder(this@OrderOutForDeliveryActivity, listOrders)
                        recyclerOutForDelivery.adapter = adapterOutForDeliveryList
                    }

                }
            })

    }

    private fun countCartOrders() {
        FirebaseDatabase.getInstance().reference.child("seller").child(Uid.toString())
            .child("Orders").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
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
                        if (i.child("orderStatus").value.toString() == "Out For Delivery") {
                            (ordersOutForDeliveryList as ArrayList<ModelOrderDetails>).add(obj)
                        }
                    }
                    cartOutNo.text = "(${ordersOutForDeliveryList.size})"
                }

            })


    }

    private fun countListOrders() {
        FirebaseDatabase.getInstance().reference.child("seller").child(Uid.toString())
            .child("OrdersLists")
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
                        if (i.child("orderStatus").value.toString() == "Out For Delivery") {
                            (listOrders as ArrayList<ModalSellerOrderList>).add(obj)
                        }
                    }
                    listOutNo.text = "(${listOrders.size})"
                }

            })
    }

    override fun onStart() {
        super.onStart()
        countCartOrders()
        countListOrders()
        cartOutForDeliveryOrders()
    }

    override fun onBackPressed() {
        val intent = Intent(this, Home_seller::class.java)
        startActivity(intent)
        finish()
    }
}