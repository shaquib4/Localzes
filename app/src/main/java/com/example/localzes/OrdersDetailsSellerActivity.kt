package com.example.localzes

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrdersDetailsSellerActivity : AppCompatActivity() {
    private lateinit var recyclerOrderedSellerItems: RecyclerView
    private lateinit var sellerOrdersDetails: AdapterOrderedItems
    private lateinit var mOrderDetails: List<ModelOrderedItems>
    private lateinit var txtOrderId: TextView
    private lateinit var txtOrderDate: TextView
    private lateinit var txtOrderStatus: TextView
    private lateinit var totalItems: TextView
    private lateinit var totalAmount: TextView
    private lateinit var deliveryAddress: TextView
    private lateinit var imgEdit: ImageView
    private lateinit var shopAuth: FirebaseAuth
    private var orderIdTv: String? = "100"
    private var orderByTv: String? = "200"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_details_seller)
        recyclerOrderedSellerItems = findViewById(R.id.recyclerOrderedSellerItem)
        mOrderDetails = ArrayList<ModelOrderedItems>()
        orderIdTv = intent.getStringExtra("orderIdTv")
        orderByTv = intent.getStringExtra("orderByTv")
        txtOrderId = findViewById(R.id.txtOrderId)
        txtOrderDate = findViewById(R.id.txtOrderDate)
        txtOrderStatus = findViewById(R.id.txtOrderStatus)
        totalItems = findViewById(R.id.txtItems)
        totalAmount = findViewById(R.id.txtOrderCost)
        deliveryAddress = findViewById(R.id.txtOrderDeliveryAddress)
        imgEdit = findViewById(R.id.imgEdit)
        shopAuth = FirebaseAuth.getInstance()
        val user = shopAuth.currentUser
        val uid = user!!.uid
        val databaseRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("Orders")
                .child(orderIdTv.toString())
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
                        txtOrderStatus.setTextColor(resources.getColor(R.color.colorAccent))
                    }
                    "Accepted" -> {
                        txtOrderStatus.setTextColor(resources.getColor(R.color.green))
                    }
                    "Out For Delivery" -> {
                        txtOrderStatus.setTextColor(resources.getColor(R.color.acidGreen))
                    }
                    "Cancelled" -> {
                        txtOrderStatus.setTextColor(resources.getColor(R.color.red))
                    }
                }
                txtOrderId.text = "OD${orderId}"
                txtOrderStatus.text = orderStatus
                totalAmount.text = "Rs. ${orderCost}"
                txtOrderDate.text = formattedDate
            }
        })
        databaseRef.child("Items").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (mOrderDetails as ArrayList<ModelOrderedItems>).clear()
                for (i in snapshot.children) {
                    val obj = ModelOrderedItems(
                        i.child("pId").value.toString(),
                        i.child("name").value.toString(),
                        i.child("cost").value.toString(),
                        i.child("price").value.toString(),
                        i.child("quantity").value.toString()
                    )
                    (mOrderDetails as ArrayList<ModelOrderedItems>).add(obj)
                }
                sellerOrdersDetails =
                    AdapterOrderedItems(this@OrdersDetailsSellerActivity, mOrderDetails)
                recyclerOrderedSellerItems.adapter = sellerOrdersDetails
                if (snapshot.childrenCount.toInt() > 1) {
                    totalItems.text = "${snapshot.childrenCount} items"
                } else {
                    totalItems.text = "${snapshot.childrenCount} item"
                }
            }
        })
        val database: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(orderByTv.toString())
                .child("current_address")
        database.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val address = snapshot.child("address").value.toString()
                deliveryAddress.text = address
            }
        })
        imgEdit.setOnClickListener {
            editOrderStatusDialog()

        }
    }

    private fun editOrderStatusDialog() {
        val options = arrayOf("Accepted", "Out For Delivery", "Cancelled")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Order Status")
        builder.setSingleChoiceItems(options, -1) { dialog, which ->
            val selectedItem = options[which]
            editOrderStatus(selectedItem)
            dialog.dismiss()
        }
    }

    private fun editOrderStatus(selectedItem: String) {
        val headers = hashMapOf<String, Any>()
        headers["orderStatus"] = selectedItem
        shopAuth = FirebaseAuth.getInstance()
        val user = shopAuth.currentUser
        val uid = user!!.uid
        val ref: DatabaseReference = FirebaseDatabase.getInstance().reference.child("seller")
        ref.child(uid).child("Orders").child(orderIdTv.toString()).updateChildren(headers)
            .addOnSuccessListener {
                Toast.makeText(this, "Order has been $selectedItem", Toast.LENGTH_SHORT).show()
                val reference: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("users")
                reference.child(orderByTv.toString()).child("MyOrders").child(orderIdTv.toString())
                    .updateChildren(headers)
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}