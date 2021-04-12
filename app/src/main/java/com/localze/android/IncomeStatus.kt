package com.localze.android

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.Request
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
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
    private lateinit var progress: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income_status)
        paymentMode = intent.getStringExtra("IncomeType").toString()
        listButton = findViewById(R.id.btnList)
        cartButton = findViewById(R.id.btnCart)
        recyclerIncome = findViewById(R.id.recycler_income_status)
        progress = ProgressDialog(this)
        progress.setTitle("Please Wait")
        progress.setCanceledOnTouchOutside(false)
        listIncomeDetails = ArrayList<ModalIncomeStatus>()
        recyclerIncome.layoutManager = LinearLayoutManager(this)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid

        listButton.setOnClickListener {
            if (paymentMode == "COD") {
                 progress.setMessage("Fetching Details")
                 progress.show()
                showCODOrders(uid, "List", progress)
            }
            if (paymentMode == "PAYTM") {
                progress.setMessage("Fetching Details")
                progress.show()
                showPaytmOrders(uid, "List", progress)
            }
            if (paymentMode == "RAZORPAY") {
                progress.setMessage("Fetching Details")
                progress.show()
                showRazorpayOrders(uid, "List", progress)
            }
        }
        cartButton.setOnClickListener {
            if (paymentMode == "COD") {
                 progress.setMessage("Fetching Details")
                 progress.show()
                showCODOrders(uid, "Cart", progress)
            }
            if (paymentMode == "PAYTM") {
                progress.setMessage("Fetching Details")
                progress.show()
                showPaytmOrders(uid, "Cart", progress)
            }
            if (paymentMode == "RAZORPAY") {
                progress.setMessage("Fetching Details")
                progress.show()
                showRazorpayOrders(uid, "Cart", progress)
            }
        }
    }

    private fun showRazorpayOrders(
        uid: String,
        s: String,
        progress: ProgressDialog
    ) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        if (s == "Cart") {
            databaseReference.child("Orders").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {


                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    (listIncomeDetails as ArrayList<ModalIncomeStatus>).clear()
                    for (i in snapshot.children) {
                        if (i.child("paymentMode").value.toString() == "Paid Online") {
                            sendRequest(
                                i.child("transferId").value.toString(),
                                s,
                                uid,
                                i.child("orderId").value.toString()
                            )
                            val obj = ModalIncomeStatus(
                                i.child("orderCost").value.toString(),
                                i.child("transferId").value.toString(),
                                i.child("settlementId").value.toString(),
                                i.child("orderId").value.toString(),
                                i.child("orderByName").value.toString(),
                                "Razorpay",
                                i.child("orderByMobile").value.toString(),
                                i.child("orderTime").value.toString()
                            )
                            (listIncomeDetails as ArrayList<ModalIncomeStatus>).add(obj)

                        }
                    }
                    progress.dismiss()
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
                                sendRequest(
                                    i.child("transferId").value.toString(),
                                    s,
                                    uid,
                                    i.child("orderId").value.toString()
                                )
                                val obj = ModalIncomeStatus(
                                    i.child("orderCost").value.toString(),
                                    i.child("transferId").value.toString(),
                                    i.child("settlementId").value.toString(),
                                    i.child("orderId").value.toString(),
                                    i.child("orderByName").value.toString(),
                                    "Razorpay",
                                    i.child("orderByMobile").value.toString(),
                                    i.child("orderTime").value.toString()
                                )
                                (listIncomeDetails as ArrayList<ModalIncomeStatus>).add(obj)

                            }
                        }
                        progress.dismiss()
                        adapterIncome = AdapterIncomeStatus(this@IncomeStatus, listIncomeDetails)
                        recyclerIncome.adapter = adapterIncome
                    }

                })
        }


    }

    private fun sendRequest(
        trfId: String,
        s: String,
        uid: String,
        orderId: String
    ) {
        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET,
            "https://api.razorpay.com/v1/transfers/${trfId}",
            null,
            Response.Listener {
                val settlementId = it.get("recipient_settlement_id")
                val headers = HashMap<String, Any>()
                headers["settlementId"] = settlementId
                if (s == "Cart") {
                    FirebaseDatabase.getInstance().reference.child("seller").child(uid)
                        .child("Orders").child(orderId).updateChildren(headers)

                } else {
                    FirebaseDatabase.getInstance().reference.child("seller").child(uid)
                        .child("OrdersLists").child(orderId).updateChildren(headers)

                }

            },
            Response.ErrorListener {

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] =
                    "Basic cnpwX2xpdmVfdTdtUURuMGh6aE9Ick06ZU15aDRScE1CSHpMcVZRRDMxbGE5MGdi"
                return headers
            }

        }
        Volley.newRequestQueue(this).add(jsonObjectRequest)

    }

    private fun showPaytmOrders(
        uid: String,
        s: String,
        progress: ProgressDialog
    ) {
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
                                i.child("orderByMobile").value.toString(),
                                i.child("orderTime").value.toString()
                            )
                            (listIncomeDetails as ArrayList<ModalIncomeStatus>).add(obj)
                        }
                    }
                    progress.dismiss()
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
                                    i.child("orderByMobile").value.toString(),
                                    i.child("orderTime").value.toString()
                                )
                                (listIncomeDetails as ArrayList<ModalIncomeStatus>).add(obj)
                            }
                        }
                        progress.dismiss()
                        adapterIncome = AdapterIncomeStatus(this@IncomeStatus, listIncomeDetails)
                        recyclerIncome.adapter = adapterIncome
                    }

                })
        }
    }

    private fun showCODOrders(
        uid: String,
        s: String,
        progress: ProgressDialog
    ) {
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
                                i.child("orderByMobile").value.toString(),
                                i.child("orderTime").value.toString()
                            )
                            (listIncomeDetails as ArrayList<ModalIncomeStatus>).add(obj)
                        }
                    }
                    progress.dismiss()
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
                                    i.child("orderByMobile").value.toString(),
                                    i.child("orderTime").value.toString()
                                )
                                (listIncomeDetails as ArrayList<ModalIncomeStatus>).add(obj)
                            }
                        }
                         progress.dismiss()
                        adapterIncome = AdapterIncomeStatus(this@IncomeStatus, listIncomeDetails)
                        recyclerIncome.adapter = adapterIncome
                    }

                })
        }


    }

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        progress = ProgressDialog(this)
        progress.setTitle("Please Wait")
        progress.setCanceledOnTouchOutside(false)
        val user = auth.currentUser
        val uid = user!!.uid
        paymentMode = intent.getStringExtra("IncomeType").toString()
        if (paymentMode == "COD") {
            progress.setMessage("Fetching Details")
            progress.show()
            showCODOrders(uid, "Cart", progress)
        }
        if (paymentMode == "PAYTM") {
            progress.setMessage("Fetching Details")
            progress.show()
            showPaytmOrders(uid, "Cart", progress)
        }
        if (paymentMode == "RAZORPAY") {
            progress.setMessage("Fetching Details")
            progress.show()
            showRazorpayOrders(uid, "Cart", progress)
        }
    }

    override fun onBackPressed() {
        val intent=Intent(this,IncomeType::class.java)
        startActivity(intent)
        finish()
    }
}