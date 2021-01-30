package com.localze.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.localze.android.Adapters.AdapterOrderedItems
import com.localze.android.Modals.ModelOrderedItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_orders_details_user.*
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
    private lateinit var deliveryFeeUser: TextView
    private lateinit var paymentModeInfo: TextView
    private lateinit var shopAddress: TextView
    private lateinit var recyclerOrderUsers: RecyclerView
    private lateinit var adapterOrderItems: AdapterOrderedItems
    private lateinit var orderItemsList: List<ModelOrderedItems>
    private lateinit var databaseRef: DatabaseReference
    private lateinit var userAuth: FirebaseAuth
    private var orderItemId: String? = "100"
    private var orderToId: String? = "200"
    private var totalCost: String? = "300"
    private lateinit var imgBackOrderDetails: ImageView
    private lateinit var imgMakeCall: ImageView
    private var shopMobileNumber: String = ""
    private var deliveryFee: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_details_user)
        orderItemId = intent.getStringExtra("orderId")
        orderToId = intent.getStringExtra("orderTo")
        totalCost = intent.getStringExtra("totalAmount")
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
        paymentModeInfo = findViewById(R.id.paymentStatusDetailUser)
        deliveryFeeUser = findViewById(R.id.deliveryFeeOrderUser)
        imgMakeCall = findViewById(R.id.imageMakeCall)
        orderItemsList = ArrayList<ModelOrderedItems>()
        userAuth = FirebaseAuth.getInstance()
        val user = userAuth.currentUser
        val uid = user!!.uid
        val refs=FirebaseDatabase.getInstance().reference.child("users").child(orderToId.toString()).child("current_address")
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val latitude=snapshot.child("latitude").value.toString()
                    val longitude=snapshot.child("longitude").value.toString()
                    btnDirShop.setOnClickListener {
                        val uri=Uri.parse("google.navigation:q=$latitude,$longitude")
                        val intent=Intent(Intent.ACTION_VIEW)
                        intent.data=uri
                        startActivity(intent)
                    }
                }
            })
        imgMakeCall.setOnClickListener {
            makePhoneCall()
        }
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
                    "Pending" -> {
                        orderStatusUser.setTextColor(resources.getColor(R.color.colorAccent))
                        btnPayCart.visibility = View.GONE
                    }
                    "Accepted" -> {
                        orderStatusUser.setTextColor(resources.getColor(R.color.green))
                        btnPayCart.visibility = View.VISIBLE
                    }
                    "Out For Delivery" -> {
                        orderStatusUser.setTextColor(resources.getColor(R.color.acidGreen))
                        btnPayCart.visibility = View.GONE
                    }
                    "Cancelled" -> {
                        orderStatusUser.setTextColor(resources.getColor(R.color.red))
                        btnPayCart.visibility = View.GONE
                    }
                }
                orderIdUser.text = "OD${orderId}"
                orderStatusUser.text = orderStatus
                orderDateUser.text = formattedDate
                paymentModeInfo.text = snapshot.child("paymentMode").value.toString()
                if (snapshot.child("paymentMode").value.toString() == "" && snapshot.child("orderStatus").value.toString() == "Accepted") {
                    btnPayCart.visibility = View.VISIBLE
                } else {
                    btnPayCart.visibility = View.GONE
                }
                if (snapshot.child("deliveryFee").exists()) {
                    deliveryFee = snapshot.child("deliveryFee").value.toString().toDouble()
                    if (deliveryFee==null){
                        deliveryFeeUser.text="0"
                    }else{
                        deliveryFeeUser.text = "₹" + snapshot.child("deliveryFee").value.toString()
                    }
                    totalAmountUser.text = "₹${orderCost}(Including Delivery Fee)"
                } else {
                    totalAmountUser.text = "₹${orderCost}"
                    deliveryFeeUser.text = "Not Available (updated soon)"
                }
                val reference: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("seller")
                        .child(orderToId.toString())
                reference.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val shop = snapshot.child("address").value.toString()
                        val shopsName = snapshot.child("shop_name").value.toString()
                        shopMobileNumber = snapshot.child("phone").value.toString()
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

        btnPayCart.setOnClickListener {
            if (deliveryFee != null) {
                val intent = Intent(this, continue_payment::class.java)
                intent.putExtra("shopId", orderToId.toString())
                intent.putExtra("totalCost", totalCost.toString())
                intent.putExtra("deliveryFee", deliveryFee.toString())
                intent.putExtra("orderId", orderItemId.toString())
                intent.putExtra("orderBy", uid)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Slow Internet,Press Button Again", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun makePhoneCall() {
        val number = shopMobileNumber
        val intent = Intent(Intent.ACTION_DIAL)
        intent.setData(Uri.parse("tel:" + number))
        val chooser = Intent.createChooser(intent, "Call Action Using")
        startActivity(chooser)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}