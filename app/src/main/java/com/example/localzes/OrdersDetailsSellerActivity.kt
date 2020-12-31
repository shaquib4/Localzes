package com.example.localzes

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.localzes.Adapters.AdapterOrderedItems
import com.example.localzes.Modals.ModelOrderedItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_orders_details_seller.*
import org.json.JSONObject
import util.ConnectionManager
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
    private var mStatus: String? = null
    private var bool: Boolean = false
    private lateinit var imgBackOrderDetails: ImageView
    private var selectedReason: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_details_seller)
        recyclerOrderedSellerItems = findViewById(R.id.recyclerOrderedSellerItem)
        mOrderDetails = ArrayList<ModelOrderedItems>()
        imgBackOrderDetails = findViewById(R.id.imgBackOrderDetails)
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
        retryOrdersDetails.setOnClickListener {
            this.recreate()
        }
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
                if (ConnectionManager().checkConnectivity(this@OrdersDetailsSellerActivity)) {

                    when (orderStatus) {
                        "Pending" -> {
                            txtOrderStatus.setTextColor(resources.getColor(R.color.colorAccent))
                            imgEdit.setOnClickListener {
                                if (ConnectionManager().checkConnectivity(this@OrdersDetailsSellerActivity)) {
                                    editOrderStatusDialog()
                                } else {
                                    rl_OrderDetails.visibility = View.GONE
                                    rl_retryOrderDetails.visibility = View.VISIBLE
                                }
                            }
                        }
                        "Accepted" -> {
                            txtOrderStatus.setTextColor(resources.getColor(R.color.green))
                            imgEdit.setOnClickListener {
                                if (ConnectionManager().checkConnectivity(this@OrdersDetailsSellerActivity)) {
                                    newEditOrderStatusDialog()
                                } else {
                                    rl_OrderDetails.visibility = View.GONE
                                    rl_retryOrderDetails.visibility = View.VISIBLE
                                }
                            }
                        }
                        "Out For Delivery" -> {
                            txtOrderStatus.setTextColor(resources.getColor(R.color.acidGreen))
                            imgEdit.setOnClickListener {
                                afterEditOrderStatusDialog()
                            }
                        }
                        "Rejected due to $selectedReason" -> {
                            txtOrderStatus.setTextColor(resources.getColor(R.color.red))
                            imgEdit.visibility = View.GONE
                        }
                    }
                } else {
                    rl_OrderDetails.visibility = View.GONE
                    rl_retryOrderDetails.visibility = View.VISIBLE
                }
                txtOrderId.text = "OD${orderId}"
                txtOrderStatus.text = orderStatus
                totalAmount.text = "₹${orderCost}"
                txtOrderDate.text = formattedDate
            }
        })
        if (ConnectionManager().checkConnectivity(this@OrdersDetailsSellerActivity)) {
            rl_OrderDetails.visibility = View.VISIBLE
            rl_retryOrderDetails.visibility = View.GONE
            databaseRef.child("Items").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    (mOrderDetails as ArrayList<ModelOrderedItems>).clear()
                    for (i in snapshot.children) {
                        val obj = ModelOrderedItems(
                            i.child("productId").value.toString(),
                            i.child("productTitle").value.toString(),
                            i.child("finalPrice").value.toString(),
                            i.child("priceEach").value.toString(),
                            i.child("finalQuantity").value.toString()
                        )
                        (mOrderDetails as ArrayList<ModelOrderedItems>).add(obj)
                    }
                    sellerOrdersDetails =
                        AdapterOrderedItems(
                            this@OrdersDetailsSellerActivity,
                            mOrderDetails
                        )
                    recyclerOrderedSellerItems.adapter = sellerOrdersDetails
                    if (snapshot.childrenCount.toInt() > 1) {
                        totalItems.text = "${snapshot.childrenCount} items"
                    } else {
                        totalItems.text = "${snapshot.childrenCount} item"
                    }
                }
            })
        } else {
            rl_OrderDetails.visibility = View.GONE
            rl_retryOrderDetails.visibility = View.VISIBLE
        }
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
        val reference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("Seller").child(uid).child("Orders")
                .child(orderIdTv.toString())
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.child("orderStatus").value.toString() == "Pending"
            }
        })
        imgBackOrderDetails.setOnClickListener {
            val intent = Intent(this, Home_seller::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun afterEditOrderStatusDialog() {
        val options = arrayOf("Payment Received", "Rejected")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Order Status")
        builder.setSingleChoiceItems(options, -1) { dialog, which ->
            val selectedItem = options[which]
            if (selectedItem == "Payment Received") {
                bool = true
                dialog.dismiss()
            } else {
                dialog.dismiss()
                val builderNew = AlertDialog.Builder(this@OrdersDetailsSellerActivity)
                builderNew.setTitle("Choose A Reason")
                val reasons =
                    arrayOf("Item is Out Of Stock", "Shop is closed Now", "Others")
                builderNew.setSingleChoiceItems(reasons, -1) { dialog, which ->
                    val selected = reasons[which]
                    dialog.dismiss()
                    selectedReason = selected
                }
                builderNew.create().show()
                editOrderStatus("$selectedItem due to $selectedReason")
            }
        }
        builder.create().show()
    }

    private fun newEditOrderStatusDialog() {
        val options: Array<String> = if (!bool) {
            arrayOf("Rejected", "Out For Delivery", "Payment Received")
        } else {
            arrayOf("Rejected", "Out For Delivery")
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Order Status")
        builder.setSingleChoiceItems(options, -1) { dialog, which ->
            val selectedItem = options[which]
            when (selectedItem) {
                "Payment Received" -> {
                    bool = true
                    dialog.dismiss()
                }
                "Rejected" -> {
                    dialog.dismiss()
                    val builderNew = AlertDialog.Builder(this@OrdersDetailsSellerActivity)
                    builderNew.setTitle("Choose A Reason")
                    val reasons =
                        arrayOf("Item is Out Of Stock", "Shop is closed Now", "Others")
                    builderNew.setSingleChoiceItems(reasons, -1) { dialog, which ->
                        val selected = reasons[which]
                        dialog.dismiss()
                        selectedReason = selected
                    }
                    builderNew.create().show()
                    editOrderStatus("$selectedItem due to $selectedReason")
                }
                else -> {
                    editOrderStatus(selectedItem)
                    dialog.dismiss()
                }
            }
        }
        builder.create().show()
    }

    private fun editOrderStatusDialog() {
        val options = arrayOf("Accepted", "Rejected")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Order Status")
        builder.setSingleChoiceItems(options, -1) { dialog, which ->
            val selectedItem = options[which]
            if (selectedItem == "Rejected") {
                dialog.dismiss()
                val newBuilder = AlertDialog.Builder(this)
                newBuilder.setTitle("Choose a Reason")
                val reasons =
                    arrayOf("Item is Out Of Stock", "Shop is closed Now", "Others")
                newBuilder.setSingleChoiceItems(reasons, -1) { dialog, which ->
                    val selected = reasons[which]
                    selectedReason = selected
                    editOrderStatus("$selectedItem due to $selectedReason")
                    dialog.dismiss()
                }
                newBuilder.create().show()
            } else {
                editOrderStatus(selectedItem)
                dialog.dismiss()
            }

        }
        builder.create().show()
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
                val message = "Order has been $selectedItem"
                val reference: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("users")
                reference.child(orderByTv.toString()).child("MyOrders").child(orderIdTv.toString())
                    .updateChildren(headers)
                prepareNotificationMessage(orderIdTv.toString(), message)
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun prepareNotificationMessage(orderId: String, message: String) {
        //when seller changes order Status,send notification to buyer
        //prepare data for notification
        val NOTIFICATION_TOPIC =
            "/topics/PUSH_NOTIFICATIONS" //must be same as subscribed by user
        val NOTIFICATION_TITLE = message
        var NOTIFICATION_MESSAGE: String = ""
        when (message) {
            "Order has been Accepted" -> {
                NOTIFICATION_MESSAGE = "Yay! Your order has been accepted"
            }
            "Order has been Out For Delivery" -> {
                NOTIFICATION_MESSAGE = "Your order is on the way"
            }
            "Order has been Rejected due to $selectedReason" -> {
                NOTIFICATION_MESSAGE =
                    "Sorry for the inconvenience.Your order is rejected due to $selectedReason"
            }
        }
        val NOTIFICATION_TYPE = "OrderStatusChanged"
        //prepare json(what to send and where to send)
        val notificationJs = JSONObject()
        val notificationBodyJs = JSONObject()
        try {
            //what to send
            notificationBodyJs.put("notificationType", NOTIFICATION_TYPE)
            notificationBodyJs.put("buyerId", orderByTv.toString())
            notificationBodyJs.put("sellerUid", (shopAuth.currentUser)!!.uid)
            notificationBodyJs.put("orderId", orderId)
            notificationBodyJs.put("notificationTitle", NOTIFICATION_TITLE)
            notificationBodyJs.put("notificationMessage", NOTIFICATION_MESSAGE)
            //where to send
            notificationJs.put("to", NOTIFICATION_TOPIC)//to all who subscribed this topic
            notificationJs.put("data", notificationBodyJs)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
        sendFcmNotification(notificationJs)


    }

    private fun sendFcmNotification(notificationJs: JSONObject) {
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = object : JsonObjectRequest(
            "https://fcm.googleapis.com/fcm/send",
            notificationJs,
            Response.Listener {
                //notification sent
            },
            Response.ErrorListener {
                //notification failed
                Toast.makeText(this, "Some error occurred", Toast.LENGTH_SHORT).show()

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] =
                    "key=AAAA0TgW0AY:APA91bGNGMLtISkxVjfP-Mvu6GCZeeTcoDzvFtUg0Pq1SrJ9SshsFXDuXR9i3-lOqtlUjVmGqmv4C0sSRbsIphiacRau5c1ERQEUBukLxV-EXGVGv1ZmTN796LyLs1Wd7s1Tnu60e_2D"
                return headers
            }
        }
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun onCheckboxClicked(view: View) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("Seller")
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked
            when (view.id) {
                R.id.checkbox_Completed -> {
                    if (checked) {
                        val builder = AlertDialog.Builder(this)
                        val new = builder.show()
                        builder.setTitle("Confirmation")
                        builder.setMessage("Are you sure your order has been successfully completed")
                        builder.setPositiveButton("Ok") { text, listener ->
                            view.visibility = View.GONE
                            new.dismiss()

                        }
                        builder.setNegativeButton("Cancel") { text, listener ->
                            view.isChecked = false
                            new.dismiss()
                        }
                        builder.create().show()
                    }
                }
            }
        }
    }
}