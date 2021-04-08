package com.localze.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.localze.android.Adapters.AdapterIncomeStatus
import com.localze.android.Modals.ModalIncomeStatus

class IncomeStatus : AppCompatActivity() {
    private lateinit var adapterIncome: AdapterIncomeStatus
    private lateinit var recyclerIncome: RecyclerView
    private var paymentMode: String = ""
    private lateinit var listButton: Button
    private lateinit var cartButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var listIncomeDetails: List<ModalIncomeStatus>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income_status)
        paymentMode = intent.getStringExtra("IncomeType").toString()
        listButton = findViewById(R.id.btnList)
        cartButton = findViewById(R.id.btnCart)
        recyclerIncome = findViewById(R.id.recycler_income_status)
        listIncomeDetails = ArrayList<ModalIncomeStatus>()
        recyclerIncome.layoutManager = LinearLayoutManager(this)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid

        listButton.setOnClickListener {
            if (paymentMode == "COD") {
                showCODOrders(uid, "List")
            }
            if (paymentMode == "PAYTM") {
                showPaytmOrders(uid, "List")
            }
            if (paymentMode == "RAZORPAY") {
                showRazorpayOrders(uid, "List")
            }
        }
        cartButton.setOnClickListener {
            if (paymentMode == "COD") {
                showCODOrders(uid, "Cart")
            }
            if (paymentMode == "PAYTM") {
                showPaytmOrders(uid, "Cart")
            }
            if (paymentMode == "RAZORPAY") {
                showRazorpayOrders(uid, "Cart")
            }
        }
    }

    private fun showRazorpayOrders(uid: String, s: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        if (s == "Cart") {
            databaseReference.child("Orders").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {


                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    (listIncomeDetails as ArrayList<ModalIncomeStatus>).clear()
                    for (i in snapshot.children) {
                        if (i.child("paymentMode").value.toString() == "Paid Online") {
                            val obj = ModalIncomeStatus(
                                i.child("orderCost").value.toString(),
                                i.child("transferId").value.toString(),
                                i.child("settlementId").value.toString(),
                                i.child("orderId").value.toString(),
                                i.child("orderByName").value.toString(),
                                "Razorpay",
                                i.child("orderByMobile").value.toString()
                            )
                            (listIncomeDetails as ArrayList<ModalIncomeStatus>).add(obj)
                        }
                    }
                    adapterIncome = AdapterIncomeStatus(this@IncomeStatus, listIncomeDetails)
                    recyclerIncome.adapter = adapterIncome
                }
            })
        }
        if (s == "List") {
            databaseReference.child("OrdersLists")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        (listIncomeDetails as ArrayList<ModalIncomeStatus>).clear()
                        for (i in snapshot.children) {
                            if (i.child("paymentMode").value.toString() == "Paid Online") {
                                val obj = ModalIncomeStatus(
                                    i.child("orderCost").value.toString(),
                                    i.child("transferId").value.toString(),
                                    i.child("settlementId").value.toString(),
                                    i.child("orderId").value.toString(),
                                    i.child("orderByName").value.toString(),
                                    "Razorpay",
                                    i.child("orderByMobile").value.toString()
                                )
                                (listIncomeDetails as ArrayList<ModalIncomeStatus>).add(obj)
                            }
                        }
                        adapterIncome = AdapterIncomeStatus(this@IncomeStatus, listIncomeDetails)
                        recyclerIncome.adapter = adapterIncome
                    }

                })
        }


    }

    private fun showPaytmOrders(uid: String, s: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        if (s == "Cart") {
            databaseReference.child("Orders").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {


                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    (listIncomeDetails as ArrayList<ModalIncomeStatus>).clear()
                    for (i in snapshot.children) {
                        if (i.child("paymentMode").value.toString() == "Paid(Pay with Paytm)") {
                            val obj = ModalIncomeStatus(
                                i.child("orderCost").value.toString(),
                                i.child("transferId").value.toString(),
                                i.child("settlementId").value.toString(),
                                i.child("orderId").value.toString(),
                                i.child("orderByName").value.toString(),
                                "Paytm",
                                i.child("orderByMobile").value.toString()
                            )
                            (listIncomeDetails as ArrayList<ModalIncomeStatus>).add(obj)
                        }
                    }
                    adapterIncome = AdapterIncomeStatus(this@IncomeStatus, listIncomeDetails)
                    recyclerIncome.adapter = adapterIncome
                }
            })
        }
        if (s == "List") {
            databaseReference.child("OrdersLists")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        (listIncomeDetails as ArrayList<ModalIncomeStatus>).clear()
                        for (i in snapshot.children) {
                            if (i.child("paymentMode").value.toString() == "Paid(Pay with Paytm)") {
                                val obj = ModalIncomeStatus(
                                    i.child("orderCost").value.toString(),
                                    i.child("transferId").value.toString(),
                                    i.child("settlementId").value.toString(),
                                    i.child("orderId").value.toString(),
                                    i.child("orderByName").value.toString(),
                                    "Paytm",
                                    i.child("orderByMobile").value.toString()
                                )
                                (listIncomeDetails as ArrayList<ModalIncomeStatus>).add(obj)
                            }
                        }
                        adapterIncome = AdapterIncomeStatus(this@IncomeStatus, listIncomeDetails)
                        recyclerIncome.adapter = adapterIncome
                    }

                })
        }
    }

    private fun showCODOrders(uid: String, s: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        if (s == "Cart") {
            databaseReference.child("Orders").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    (listIncomeDetails as ArrayList<ModalIncomeStatus>).clear()
                    for (i in snapshot.children) {
                        if (i.child("paymentMode").value.toString() == "Unpaid(Cash on Delivery)") {
                            val obj = ModalIncomeStatus(
                                i.child("orderCost").value.toString(),
                                i.child("transferId").value.toString(),
                                i.child("settlementId").value.toString(),
                                i.child("orderId").value.toString(),
                                i.child("orderByName").value.toString(),
                                "Cash On Delivery",
                                i.child("orderByMobile").value.toString()
                            )
                            (listIncomeDetails as ArrayList<ModalIncomeStatus>).add(obj)
                        }
                    }
                    adapterIncome = AdapterIncomeStatus(this@IncomeStatus, listIncomeDetails)
                    recyclerIncome.adapter = adapterIncome
                }

            })
        }
        if (s == "List") {
            databaseReference.child("OrdersLists")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        (listIncomeDetails as ArrayList<ModalIncomeStatus>).clear()
                        for (i in snapshot.children) {
                            if (i.child("paymentMode").value.toString() == "Unpaid(Cash on Delivery)") {
                                val obj = ModalIncomeStatus(
                                    i.child("orderCost").value.toString(),
                                    i.child("transferId").value.toString(),
                                    i.child("settlementId").value.toString(),
                                    i.child("orderId").value.toString(),
                                    i.child("orderByName").value.toString(),
                                    "Cash On Delivery",
                                    i.child("orderByMobile").value.toString()
                                )
                                (listIncomeDetails as ArrayList<ModalIncomeStatus>).add(obj)
                            }
                        }
                        adapterIncome = AdapterIncomeStatus(this@IncomeStatus, listIncomeDetails)
                        recyclerIncome.adapter = adapterIncome
                    }

                })
        }


    }

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        paymentMode = intent.getStringExtra("PaymentMode").toString()
        if (paymentMode == "COD") {
            showCODOrders(uid, "Cart")
        }
        if (paymentMode == "PAYTM") {
            showPaytmOrders(uid, "Cart")
        }
        if (paymentMode == "RAZORPAY") {
            showRazorpayOrders(uid, "Cart")
        }
    }
}