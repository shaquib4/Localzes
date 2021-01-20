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
import kotlinx.android.synthetic.main.activity_seller_orders.*
import util.ConnectionManager

class SellerOrdersActivity : AppCompatActivity() {
    private lateinit var recyclerShopOrders: RecyclerView
    private lateinit var sellerOrderAdapter: AdapterSellerOrders
    private lateinit var mSellerOrders: List<ModelOrderDetails>
    private lateinit var sellerOrderDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var imgBackPending: ImageView
    private lateinit var relativeOrders: RelativeLayout
    private lateinit var listAdapter: AdapterListOrder
    private lateinit var listOrders: List<ModalSellerOrderList>
    private lateinit var rl_search_Pending: EditText
    private lateinit var rl_search_Pending1: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_orders)
        recyclerShopOrders = findViewById(R.id.recyclerShopOrders)
        imgBackPending = findViewById(R.id.imgBackPending)
        relativeOrders = findViewById(R.id.rl_Pending_Orders)
        listOrders = ArrayList<ModalSellerOrderList>()
        rl_search_Pending = findViewById(R.id.search_act_Pending)
        rl_search_Pending1 = findViewById(R.id.search_act_Pending1)
        recyclerShopOrders.layoutManager = LinearLayoutManager(this)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        mSellerOrders = ArrayList<ModelOrderDetails>()
        rl_search_Pending.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchCartOrders(s.toString())
            }

        })
        rl_search_Pending1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchListOrders(s.toString())
            }

        })
        sellerOrderDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("Orders")
        sellerOrderDatabase.child("Orders").orderByChild("orderStatus").equalTo("Pending")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    cartPendingNo.text = "(${snapshot.childrenCount})"
                }
            })
        sellerOrderDatabase.child("OrdersLists").orderByChild("orderStatus").equalTo("Pending")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    listPendingNo.text = "(${snapshot.childrenCount})"
                }
            })
        rl_cartPending.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)) {
                rl_pending.visibility = View.VISIBLE
                rl_Seller_Orders_retry.visibility = View.GONE
                pendingCartOrder()
            } else {
                rl_pending.visibility = View.GONE
                rl_Seller_Orders_retry.visibility = View.VISIBLE
            }
        }
        rl_listPending.setOnClickListener {
            rl_pending.visibility = View.VISIBLE
            rl_Seller_Orders_retry.visibility = View.GONE
            if (ConnectionManager().checkConnectivity(this)) {
                pendingListOrder()
            } else {
                rl_pending.visibility = View.GONE
                rl_Seller_Orders_retry.visibility = View.VISIBLE
            }
        }

        imgBackPending.setOnClickListener {
            val intent = Intent(this, Home_seller::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun searchListOrders(str: String) {
        val user = auth.currentUser
        val uid = user!!.uid
        val querySellerDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("OrdersLists")
                .orderByChild("orderId").startAt(str).endAt(str + "\uf8ff")
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
                    if (i.child("orderStatus").value.toString() == "Pending") {
                        (listOrders as ArrayList<ModalSellerOrderList>).add(obj)
                    }
                }
                listAdapter = AdapterListOrder(this@SellerOrdersActivity, listOrders)
                recyclerShopOrders.adapter = listAdapter
            }
        })
    }

    private fun pendingListOrder() {
        rl_search_Pending.visibility = View.GONE
        rl_search_Pending1.visibility = View.VISIBLE
        txtCartPending.setTextColor(this.resources.getColor(R.color.black))
        cartPendingNo.setTextColor(this.resources.getColor(R.color.black))
        txtlistPending.setTextColor(this.resources.getColor(R.color.colorPrimary))
        listPendingNo.setTextColor(this.resources.getColor(R.color.colorPrimary))
        val user = auth.currentUser
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
                        if (i.child("orderStatus").value.toString() == "Pending") {
                            (listOrders as ArrayList<ModalSellerOrderList>).add(obj)
                        }
                    }
                    if (listOrders.isEmpty()) {
                        recyclerShopOrders.visibility = View.GONE
                    } else {
                        relativeOrders.visibility = View.GONE
                        recyclerShopOrders.visibility = View.VISIBLE
                        (listOrders as ArrayList<ModalSellerOrderList>).reverse()
                        listAdapter = AdapterListOrder(this@SellerOrdersActivity, listOrders)
                        recyclerShopOrders.adapter = listAdapter
                    }
                }

            })
    }

    private fun pendingCartOrder() {
        rl_search_Pending1.visibility = View.GONE
        rl_search_Pending.visibility = View.VISIBLE
        txtCartPending.setTextColor(this.resources.getColor(R.color.colorPrimary))
        cartPendingNo.setTextColor(this.resources.getColor(R.color.colorPrimary))
        txtlistPending.setTextColor(this.resources.getColor(R.color.black))
        listPendingNo.setTextColor(this.resources.getColor(R.color.black))
        if (ConnectionManager().checkConnectivity(this)) {
            rl_pending.visibility = View.VISIBLE
            rl_Seller_Orders_retry.visibility = View.GONE
            sellerOrderDatabase.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    (mSellerOrders as ArrayList<ModelOrderDetails>).clear()
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
                        if (i.child("orderStatus").value.toString() == "Pending") {
                            (mSellerOrders as ArrayList<ModelOrderDetails>).add(obj)
                        }
                    }
                    if (mSellerOrders.isEmpty()) {
                        recyclerShopOrders.visibility = View.GONE
                    } else {
                        relativeOrders.visibility = View.GONE
                        recyclerShopOrders.visibility = View.VISIBLE
                        (mSellerOrders as ArrayList<ModelOrderDetails>).reverse()
                        sellerOrderAdapter =
                            AdapterSellerOrders(
                                this@SellerOrdersActivity,
                                mSellerOrders
                            )
                        recyclerShopOrders.adapter = sellerOrderAdapter
                    }
                }
            })
        } else {
            rl_pending.visibility = View.GONE
            rl_Seller_Orders_retry.visibility = View.VISIBLE
        }
    }

    private fun searchCartOrders(str: String) {

        val user = auth.currentUser
        val uid = user!!.uid
        val querySellerDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("Orders")
                .orderByChild("orderId").startAt(str).endAt(str + "\uf8ff")
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
                    if (i.child("orderStatus").value.toString() == "Pending") {
                        (listOrders as ArrayList<ModalSellerOrderList>).add(obj)
                    }
                }
                sellerOrderAdapter =
                    AdapterSellerOrders(
                        this@SellerOrdersActivity,
                        mSellerOrders
                    )
                recyclerShopOrders.adapter = sellerOrderAdapter
            }

        })
    }

    override fun onBackPressed() {
        val intent = Intent(this, Home_seller::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        if (ConnectionManager().checkConnectivity(this)) {
            rl_pending.visibility = View.VISIBLE
            rl_Seller_Orders_retry.visibility = View.GONE
            pendingCartOrder()
        } else {
            rl_pending.visibility = View.GONE
            rl_Seller_Orders_retry.visibility = View.VISIBLE
        }
    }
}