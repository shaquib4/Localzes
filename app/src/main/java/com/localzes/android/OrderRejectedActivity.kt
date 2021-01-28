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
import com.google.firebase.database.*
import com.localzes.android.Adapters.AdapterListOrder
import com.localzes.android.Adapters.AdapterSellerOrders
import com.localzes.android.Modals.ModalSellerOrderList
import com.localzes.android.Modals.ModelOrderDetails
import kotlinx.android.synthetic.main.activity_order_rejected.*

class OrderRejectedActivity : AppCompatActivity() {
    private lateinit var orderDatabaseReference: DatabaseReference
    private lateinit var ordersRejectedList: List<ModelOrderDetails>
    private lateinit var recyclerOrdersRejected: RecyclerView
    private lateinit var adapterOrderRejected: AdapterSellerOrders
    private lateinit var backRejected: ImageView
    private lateinit var orderRejected: RelativeLayout
    private lateinit var listAdapter: AdapterListOrder
    private lateinit var listOrders: List<ModalSellerOrderList>
    private lateinit var editSearchListRejected: EditText
    private lateinit var editSearchCartRejected: EditText
    private var Uid: String? = "400"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_rejected)
        ordersRejectedList = ArrayList<ModelOrderDetails>()
        recyclerOrdersRejected = findViewById(R.id.recyclerOrdersRejected)
        backRejected = findViewById(R.id.imgBackRejected)
        editSearchCartRejected = findViewById(R.id.searchCartRejected)
        editSearchListRejected = findViewById(R.id.searchListRejected)
        orderRejected = findViewById(R.id.rl_RejectedOrder)
        recyclerOrdersRejected.layoutManager = LinearLayoutManager(this)
        listOrders = ArrayList<ModalSellerOrderList>()
        Uid = intent.getStringExtra("UID")
        orderDatabaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(Uid.toString())
/*        orderDatabaseReference.child("Orders").orderByChild("orderStatus").equalTo("Accepted")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    cartRejectedNo.text = "(${snapshot.childrenCount})"
                }

            })
        orderDatabaseReference.child("OrdersLists").orderByChild("orderStatus").equalTo("Accepted")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    listRejectedNo.text = "(${snapshot.childrenCount})"
                }

            })*/
        rl_cartRejected.setOnClickListener {
            countCartOrders()
            countListOrders()
            rejectedCartOrder()

        }
        rl_listRejected.setOnClickListener {
            countListOrders()
            countCartOrders()
            rejectedListOrder()
        }
        backRejected.setOnClickListener {
            val intent = Intent(this, Home_seller::class.java)
            startActivity(intent)
            finish()
        }

        editSearchListRejected.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchListRejected(s.toString())
            }

        })
        editSearchCartRejected.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchCartRejected(s.toString())
            }

        })

    }

    private fun searchCartRejected(s: String) {
        val querySellerDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(Uid.toString())
                .child("Orders").orderByChild("orderId").startAt(s).endAt(s + "\uF8FF")
        querySellerDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (ordersRejectedList as ArrayList<ModelOrderDetails>).clear()
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
                    if (i.child("orderStatus").value.toString() == "Rejected due to Item is Out Of Stock" || i.child(
                            "orderStatus"
                        ).value.toString() == "Rejected due to Shop is closed Now" || i.child("orderStatus").value.toString() == "Rejected due to Others"
                    ) {
                        (ordersRejectedList as ArrayList<ModelOrderDetails>).add(obj)
                    }
                }
                adapterOrderRejected =
                    AdapterSellerOrders(this@OrderRejectedActivity, ordersRejectedList)
                recyclerOrdersRejected.adapter = adapterOrderRejected
            }
        })
    }

    private fun searchListRejected(s: String) {
        val querySellerDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(Uid.toString())
                .child("OrdersLists").orderByChild("orderId").startAt(s).endAt(s + "\uF8FF")
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
                    if (i.child("orderStatus").value.toString() == "Rejected due to Item is Out Of Stock" || i.child(
                            "orderStatus"
                        ).value.toString() == "Rejected due to Shop is closed Now" || i.child("orderStatus").value.toString() == "Rejected due to Others"
                    ) {
                        (listOrders as ArrayList<ModalSellerOrderList>).add(obj)
                    }
                }
                listAdapter = AdapterListOrder(this@OrderRejectedActivity, listOrders)
                recyclerOrdersRejected.adapter = listAdapter
            }
        })
    }

    private fun rejectedListOrder() {
        editSearchCartRejected.visibility = View.GONE
        editSearchListRejected.visibility = View.VISIBLE
        txtCartRejected.setTextColor(this.resources.getColor(R.color.black))
        cartRejectedNo.setTextColor(this.resources.getColor(R.color.black))
        txtlistRejected.setTextColor(this.resources.getColor(R.color.colorPrimary))
        listRejectedNo.setTextColor(this.resources.getColor(R.color.colorPrimary))
        FirebaseDatabase.getInstance().reference.child("seller").child(Uid.toString())
            .child("OrdersLists").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
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
                            if (i.child("orderStatus").value.toString() == "Rejected due to Item is Out Of Stock" || i.child(
                                    "orderStatus"
                                ).value.toString() == "Rejected due to Shop is closed Now" || i.child(
                                    "orderStatus"
                                ).value.toString() == "Rejected due to Others"
                            ) {
                                (listOrders as ArrayList<ModalSellerOrderList>).add(obj)

                            }
                        }
                        if (listOrders.isEmpty()) {
                            recyclerOrdersRejected.visibility = View.GONE

                        } else {
                            orderRejected.visibility = View.GONE
                            recyclerOrdersRejected.visibility = View.VISIBLE
                            listAdapter = AdapterListOrder(this@OrderRejectedActivity, listOrders)
                            recyclerOrdersRejected.adapter = listAdapter
                        }
                    }
                }
            })
    }

    private fun rejectedCartOrder() {
        editSearchCartRejected.visibility = View.GONE
        editSearchListRejected.visibility = View.VISIBLE
        txtCartRejected.setTextColor(this.resources.getColor(R.color.colorPrimary))
        cartRejectedNo.setTextColor(this.resources.getColor(R.color.colorPrimary))
        txtlistRejected.setTextColor(this.resources.getColor(R.color.black))
        listRejectedNo.setTextColor(this.resources.getColor(R.color.black))
        FirebaseDatabase.getInstance().reference.child("seller").child(Uid.toString())
            .child("Orders").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        (ordersRejectedList as ArrayList<ModelOrderDetails>).clear()
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
                            if (i.child("orderStatus").value.toString() == "Rejected due to Item is Out Of Stock" || i.child(
                                    "orderStatus"
                                ).value.toString() == "Rejected due to Shop is closed Now" || i.child(
                                    "orderStatus"
                                ).value.toString() == "Rejected due to Others"
                            ) {
                                ((ordersRejectedList as ArrayList<ModelOrderDetails>)).add(obj)
                            }
                        }
                        if (ordersRejectedList.isEmpty()) {
                            recyclerOrdersRejected.visibility = View.GONE
                        } else {
                            orderRejected.visibility = View.GONE
                            recyclerOrdersRejected.visibility = View.VISIBLE
                            (ordersRejectedList as ArrayList<ModelOrderDetails>).reverse()
                            adapterOrderRejected =
                                AdapterSellerOrders(this@OrderRejectedActivity, ordersRejectedList)
                            recyclerOrdersRejected.adapter = adapterOrderRejected
                        }
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
                    if (snapshot.exists()) {
                        (ordersRejectedList as ArrayList<ModelOrderDetails>).clear()
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
                            if (i.child("orderStatus").value.toString() == "Rejected due to Item is Out Of Stock" || i.child(
                                    "orderStatus"
                                ).value.toString() == "Rejected due to Shop is closed Now" || i.child(
                                    "orderStatus"
                                ).value.toString() == "Rejected due to Others"
                            ) {
                                ((ordersRejectedList as ArrayList<ModelOrderDetails>)).add(obj)
                            }
                        }
                        cartRejectedNo.text = "(${ordersRejectedList.size})"
                    }
                }
            })

    }

    private fun countListOrders() {
        FirebaseDatabase.getInstance().reference.child("seller").child(Uid.toString())
            .child("OrdersLists").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
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
                            if (i.child("orderStatus").value.toString() == "Rejected due to Item is Out Of Stock" || i.child(
                                    "orderStatus"
                                ).value.toString() == "Rejected due to Shop is closed Now" || i.child(
                                    "orderStatus"
                                ).value.toString() == "Rejected due to Others"
                            ) {
                                (listOrders as ArrayList<ModalSellerOrderList>).add(obj)

                            }
                        }
                        listRejectedNo.text = "(${listOrders.size})"
                    }
                }
            })
    }

    override fun onStart() {
        super.onStart()
        Uid = intent.getStringExtra("UID")
        countCartOrders()
        countListOrders()
        rejectedCartOrder()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, Home_seller::class.java)
        startActivity(intent)
        finish()
    }
}