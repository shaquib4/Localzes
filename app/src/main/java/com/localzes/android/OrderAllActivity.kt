package com.localzes.android

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.localzes.android.Adapters.AdapterListOrder
import com.localzes.android.Adapters.AdapterSellerOrders
import com.localzes.android.Modals.ModalSellerOrderList
import com.localzes.android.Modals.ModelOrderDetails
import kotlinx.android.synthetic.main.activity_orders_seller.*
import kotlinx.android.synthetic.main.activity_seller_orders.*

class OrderAllActivity : AppCompatActivity() {
    private lateinit var orderDatabaseReference: DatabaseReference
    private lateinit var allOrdersList: List<ModelOrderDetails>
    private lateinit var recyclerAllOrders: RecyclerView
    private lateinit var adapterAllOrders: AdapterSellerOrders
    private lateinit var backAll: ImageView
    private lateinit var orderAll: RelativeLayout
    private lateinit var listAdapter: AdapterListOrder
    private lateinit var listOrders: List<ModalSellerOrderList>
    private lateinit var editSearchCartAll: EditText
    private lateinit var editSearchListAll: EditText
    private var Uid: String? = "400"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_seller)
        Uid = intent.getStringExtra("UID")
        orderDatabaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(Uid.toString())
        allOrdersList = ArrayList<ModelOrderDetails>()
        listOrders = ArrayList<ModalSellerOrderList>()
        recyclerAllOrders = findViewById(R.id.recyclerOrdersTotal)
        recyclerAllOrders.layoutManager = LinearLayoutManager(this)
        backAll = findViewById(R.id.imgBackTotal)
        orderAll = findViewById(R.id.rl_Total_Orders)
        editSearchCartAll = findViewById(R.id.searchCartTotal)
        editSearchListAll = findViewById(R.id.searchListTotal)
        rl_cartTotal.setOnClickListener {
            allCartOrder()
        }
        rl_listTotal.setOnClickListener {
            allListOrder()
        }
        backAll.setOnClickListener {
            val intent = Intent(this, Home_seller::class.java)
            startActivity(intent)
            finish()
        }
        orderDatabaseReference.child("Orders")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    cartTotalNo.text = "(${snapshot.childrenCount})"

                }

            })
        orderDatabaseReference.child("OrdersLists")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    listTotalNo.text = "(${snapshot.childrenCount})"

                }
            })

        editSearchListAll.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchListOrders(s.toString())

            }
        })
        editSearchCartAll.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchCartOrders(s.toString())
            }
        })
    }

    private fun searchCartOrders(s: String) {
        val querySellerDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(Uid.toString())
                .child("Orders")
                .orderByChild("orderId").startAt(s).endAt(s + "\uF8FF")
        querySellerDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (allOrdersList as ArrayList<ModelOrderDetails>).clear()
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
                    (allOrdersList as ArrayList<ModelOrderDetails>).add(obj)
                }
                adapterAllOrders = AdapterSellerOrders(this@OrderAllActivity, allOrdersList)
                recyclerAllOrders.adapter = adapterAllOrders
            }
        })
    }

    private fun searchListOrders(s: String) {
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
                    (listOrders as ArrayList<ModalSellerOrderList>).add(obj)
                }

                listAdapter = AdapterListOrder(this@OrderAllActivity, listOrders)
                recyclerAllOrders.adapter = listAdapter
            }

        })
    }

    private fun allListOrder() {
        editSearchCartAll.visibility = View.GONE
        editSearchListAll.visibility = View.VISIBLE
        txtCartTotal.setTextColor(this.resources.getColor(R.color.black))
        cartTotalNo.setTextColor(this.resources.getColor(R.color.black))
        txtlistTotal.setTextColor(this.resources.getColor(R.color.colorPrimary))
        listTotalNo.setTextColor(this.resources.getColor(R.color.colorPrimary))
        FirebaseDatabase.getInstance().reference.child("seller").child(Uid.toString())
            .child("OrdersLists").addValueEventListener(object : ValueEventListener {
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
                        (listOrders as ArrayList<ModalSellerOrderList>).add(obj)
                    }
                    (listOrders as ArrayList<ModalSellerOrderList>).reverse()
                    if (listOrders.isEmpty()) {
                        recyclerAllOrders.visibility = View.GONE

                    } else {
                        orderAll.visibility = View.GONE
                        recyclerAllOrders.visibility = View.VISIBLE
                        listAdapter =
                            AdapterListOrder(this@OrderAllActivity, listOrders)
                        recyclerAllOrders.adapter = listAdapter
                    }
                }
            })
    }

    private fun allCartOrder() {
        editSearchListAll.visibility = View.GONE
        editSearchCartAll.visibility = View.VISIBLE
        txtCartTotal.setTextColor(this.resources.getColor(R.color.colorPrimary))
        cartTotalNo.setTextColor(this.resources.getColor(R.color.colorPrimary))
        txtlistTotal.setTextColor(this.resources.getColor(R.color.black))
        listTotalNo.setTextColor(this.resources.getColor(R.color.black))
        FirebaseDatabase.getInstance().reference.child("seller").child(Uid.toString())
            .child("Orders")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    (allOrdersList as ArrayList<ModelOrderDetails>).clear()
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
                        (allOrdersList as ArrayList<ModelOrderDetails>).add(obj)
                    }
                    (allOrdersList as ArrayList<ModelOrderDetails>).reverse()
                    if (allOrdersList.isEmpty()) {
                        recyclerAllOrders.visibility = View.GONE
                    } else {
                        orderAll.visibility = View.GONE
                        recyclerAllOrders.visibility = View.VISIBLE
                        adapterAllOrders = AdapterSellerOrders(this@OrderAllActivity, allOrdersList)
                        recyclerAllOrders.adapter = adapterAllOrders
                    }

                }
            })
    }

    override fun onStart() {
        super.onStart()
        allCartOrder()
    }

    override fun onBackPressed() {
        val intent = Intent(applicationContext, Home_seller::class.java)
        startActivity(intent)
        finish()
    }
}