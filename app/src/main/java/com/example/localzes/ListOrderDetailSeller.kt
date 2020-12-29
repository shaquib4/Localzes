package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.localzes.Adapters.AdapterSellerListOrder
import com.example.localzes.Modals.ModelList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.json.JSONObject
import java.util.HashMap

class ListOrderDetailSeller : AppCompatActivity() {
    private lateinit var orderIdTv: TextView
    private lateinit var orderDateTv: TextView
    private lateinit var orderStatusTv: TextView
    private lateinit var itemsTv: TextView
    private lateinit var amountTv: TextView
    private lateinit var deliveryAddressTv: TextView
    private lateinit var recyclerOrderedList: RecyclerView
    private lateinit var adapterListOrder: AdapterSellerListOrder
    private lateinit var shopAuth: FirebaseAuth
    private lateinit var imgListEdit: ImageView
    private lateinit var list: List<ModelList>
    private var orderId = ""
    private var orderBy = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_order_detail_seller)
        orderIdTv = findViewById(R.id.txtListOrderId)
        orderDateTv = findViewById(R.id.txtListOrderDate)
        orderStatusTv = findViewById(R.id.txtListOrderStatus)
        itemsTv = findViewById(R.id.txtListItems)
        amountTv = findViewById(R.id.txtListOrderCost)
        deliveryAddressTv = findViewById(R.id.txtListOrderDeliveryAddress)
        recyclerOrderedList = findViewById(R.id.recyclerOrderedSellerItem)
        imgListEdit = findViewById(R.id.imgListEdit)
        list = ArrayList<ModelList>()
        recyclerOrderedList.layoutManager = LinearLayoutManager(this)
        shopAuth = FirebaseAuth.getInstance()
        val user = shopAuth.currentUser
        val uid = user!!.uid
        val databaseRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid)
                .child("OrdersLists")
        orderId = intent.getStringExtra("orderId").toString()
        orderBy = intent.getStringExtra("orderBy").toString()
        databaseRef.child(orderId).child("ListItems")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    (list as ArrayList<ModelList>).clear()
                    for (i in snapshot.children) {
                        val obj = ModelList(
                            i.child("itemId").value.toString(),
                            i.child("itemName").value.toString(),
                            i.child("itemQuantity").value.toString(),
                            i.child("itemCost").value.toString(),
                            i.child("shopId").value.toString()
                        )
                        (list as ArrayList<ModelList>).add(obj)
                    }
                    adapterListOrder =
                        AdapterSellerListOrder(this@ListOrderDetailSeller, list, orderId, orderBy)
                    recyclerOrderedList.adapter = adapterListOrder
                }
            })

    }

    private fun newEditOrderStatusDialog() {
        val options = arrayOf("Rejected", "Payment Received")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Order Status")
        builder.setSingleChoiceItems(options, -1) { dialog, which ->
            val selectedItem = options[which]

        }
        builder.create().show()
    }

    private fun editOrderStatusDialog() {
        val options = arrayOf("Rejected", "Out For Delivery", "Payment Received")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Order Status")
        builder.setSingleChoiceItems(options, -1) { dialog, which ->
            val selectedItem = options[which]
            editOrderStatus(selectedItem)
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun editOrderStatus(selectedItem: String) {
        val headers = hashMapOf<String, Any>()
        headers["orderStatus"] = selectedItem
    }

    private fun prepareNotificationMessage(orderId: String, message: String) {
        val NOTIFICATION_TOPIC = "/topics/PUSH_NOTIFICATIONS"
        val NOTIFICATION_TITLE = message
        var NOTIFICATION_MESSAGE = ""
        when (message) {
            "Order has been Accepted" -> {
                NOTIFICATION_MESSAGE = "Yay! Your order has been accepted"
            }
        }
        val NOTIFICATION_TYPE = "OrderStatusChanged"
        val notificationJs = JSONObject()
        val notificationBodyJs = JSONObject()
        try {
            notificationBodyJs.put("notificationType", NOTIFICATION_TYPE)
            notificationBodyJs.put("buyerId", "")
            notificationBodyJs.put("sellerUid", (shopAuth.currentUser)!!.uid)
            notificationBodyJs.put("orderId", orderId)
            notificationBodyJs.put("notificationTitle", NOTIFICATION_TITLE)
            notificationBodyJs.put("notificationMessage", NOTIFICATION_MESSAGE)
            notificationBodyJs.put("to", NOTIFICATION_TOPIC)
            notificationBodyJs.put("data", notificationBodyJs)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
        sendFcmNotification(notificationJs)
    }

    private fun sendFcmNotification(notificationJs: JSONObject) {
        val jsonObjectRequest =
            object : JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJs,
                Response.Listener {

                }, Response.ErrorListener {

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
}