package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterOrderedItems
import com.example.localzes.Modals.ModelOrderedItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrdersDetailsUserActivity : AppCompatActivity() {
    private lateinit var orderIdUser: TextView
    private lateinit var orderDateUser: TextView
    private lateinit var orderStatusUser: TextView
    private lateinit var shopNameUser: TextView
    private lateinit var totalItemsUser: TextView
    private lateinit var totalAmountUser: TextView
    private lateinit var deliveryAddress: TextView
    private lateinit var shopAddress: TextView
    private lateinit var recyclerOrderUsers: RecyclerView
    private lateinit var adapterOrderItems: AdapterOrderedItems
    private lateinit var orderItemsList: List<ModelOrderedItems>
    private lateinit var databaseRef: DatabaseReference
    private lateinit var userAuth: FirebaseAuth
    private var orderItemId: String? = "100"
    private var orderToId: String? = "200"
    private lateinit var imgBackOrderDetails: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_details_user)
        orderItemId = intent.getStringExtra("orderId")
        orderToId = intent.getStringExtra("orderTo")
        orderIdUser = findViewById(R.id.txtOrderIdUser)
        orderDateUser = findViewById(R.id.txtOrderDateUser)
        orderStatusUser = findViewById(R.id.txtOrderStatusUser)
        shopNameUser = findViewById(R.id.txtShopNameOrder)
        totalItemsUser = findViewById(R.id.txtItemsUser)
        totalAmountUser = findViewById(R.id.txtOrderCostUser)
        deliveryAddress = findViewById(R.id.txtOrderDeliveryAddressUser)
        shopAddress = findViewById(R.id.txtOrderShopAddressUser)
        recyclerOrderUsers = findViewById(R.id.recycler_order_users)
        imgBackOrderDetails = findViewById(R.id.imgBackOrderDetails)
        orderItemsList = ArrayList<ModelOrderedItems>()
        userAuth = FirebaseAuth.getInstance()
        val user = userAuth.currentUser
        val uid = user!!.uid
        databaseRef =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("MyOrders")
                .child(orderItemId.toString())
        databaseRef.child("orderedItems").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (orderItemsList as ArrayList<ModelOrderedItems>).clear()
                for (i in snapshot.children) {
                    val obj = ModelOrderedItems(
                        i.child("productId").value.toString(),
                        i.child("productTitle").value.toString(),
                        i.child("finalPrice").value.toString(),
                        i.child("priceEach").value.toString(),
                        i.child("finalQuantity").value.toString()
                    )
                    (orderItemsList as ArrayList<ModelOrderedItems>).add(obj)
                }
                adapterOrderItems =
                    AdapterOrderedItems(
                        this@OrdersDetailsUserActivity,
                        orderItemsList
                    )
                recyclerOrderUsers.adapter = adapterOrderItems
                totalItemsUser.text = snapshot.childrenCount.toString()
            }

        })
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val orderId = snapshot.child("orderId").value.toString()
                val orderTime = snapshot.child("orderTime").value.toString()
                val orderStatus = snapshot.child("orderStatus").value.toString()
                val orderCost = snapshot.child("orderCost").value.toString()
                val orderBy = snapshot.child("orderBy").value.toString()
                val orderTo = snapshot.child("orderTo").value.toString()
                val sdf = SimpleDateFormat("dd/MM/yyyy,hh:mm a")
                val date = Date(orderTime.toLong())
                val formattedDate = sdf.format(date)
                when (orderStatus) {
                    "In Progress" -> {
                        orderStatusUser.setTextColor(resources.getColor(R.color.colorAccent))
                    }
                    "Accepted" -> {
                        orderStatusUser.setTextColor(resources.getColor(R.color.green))
                    }
                    "Out For Delivery" -> {
                        orderStatusUser.setTextColor(resources.getColor(R.color.acidGreen))
                    }
                    "Cancelled" -> {
                        orderStatusUser.setTextColor(resources.getColor(R.color.red))
                    }
                }
                orderIdUser.text = "OD${orderId}"
                orderStatusUser.text = orderStatus
                totalAmountUser.text = "₹${orderCost}"
                orderDateUser.text = formattedDate
                val reference: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("seller")
                        .child(orderToId.toString())
                reference.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val shop = snapshot.child("address").value.toString()
                        val shopsName = snapshot.child("shop_name").value.toString()
                        shopAddress.text = shop
                        shopNameUser.text = shopsName
                    }

                })
                val dataReference: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("seller")
                        .child(orderToId.toString()).child("Orders").child(orderId)
                dataReference.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val delivery = snapshot.child("deliveryAddress").value.toString()
                        deliveryAddress.text = delivery
                    }

                })
            }
        })
        imgBackOrderDetails.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}