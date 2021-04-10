package com.localze.android

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.database.*
import org.json.JSONObject
import java.util.ArrayList
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.localze.android.Modals.ModelOrderDetails
import com.localze.android.Modals.UserCartDetails
import kotlinx.android.synthetic.main.activity_pay.*
import util.ConnectionManager

class PaymentActivity : AppCompatActivity() {
    var amount: TextView? = null
    var note: TextView? = null
    var name: TextView? = null
    var upivirtualid: TextView? = null
    private lateinit var send: RelativeLayout
    var TAG = "main"
    private var shopId: String? = "100"
    private var totalCost: String? = ""
    private var deliveryAmount: String? = ""
    private var uid: String? = "400"
    private var orderId: String? = "800"
    private var mode: String? = "900"
    private lateinit var progressDialog: ProgressDialog
    private lateinit var orderDetails: ModelOrderDetails
    private lateinit var cartProducts: List<UserCartDetails>
    private lateinit var itemCost: TextView
    private lateinit var deliveryFee: TextView
    val UPI_PAYMENT = 0
    private lateinit var userDatabase: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)
        shopId = intent.getStringExtra("shopId")
        totalCost = intent.getStringExtra("totalCost")
        deliveryAmount = intent.getStringExtra("deliveryFee")
        uid = intent.getStringExtra("orderBy")
        orderId = intent.getStringExtra("orderId")
        mode = intent.getStringExtra("platform")
        cartProducts = ArrayList<UserCartDetails>()
        send = findViewById(R.id.btnPayNow)
        amount = findViewById<View>(R.id.TxtTotalPrice) as TextView
        note = findViewById<View>(R.id.txtReason) as TextView
        name = findViewById<View>(R.id.txtSellerName) as TextView
        upivirtualid = findViewById<View>(R.id.txtUPI) as TextView
        userDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(shopId.toString())
        retryPayment.setOnClickListener {
            this.recreate()
        }
        itemCost = findViewById(R.id.txtPayAmount)
        deliveryFee = findViewById(R.id.charge)
        val c = totalCost.toString().toDouble() - deliveryAmount.toString().toDouble()
        itemCost.text = "₹${c.toString()}"
        deliveryFee.text = "₹${deliveryAmount.toString()}"
        if (ConnectionManager().checkConnectivity(this)) {
            rl_Payment.visibility = View.VISIBLE
            rl_retryPayment.visibility = View.GONE
            userDatabase.addValueEventListener(object : ValueEventListener {


                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val names = snapshot.child("shop_name").value.toString()
                    val upiId = snapshot.child("upi").value.toString()
                    name!!.text = names
                    upivirtualid!!.text = upiId
                    amount!!.text = "₹${totalCost.toString()}"
                }

            })
        } else {
            rl_Payment.visibility = View.GONE
            rl_retryPayment.visibility = View.VISIBLE
        }




        send.setOnClickListener { //Getting the values from the Texts
            rl_Payment.visibility = View.VISIBLE
            rl_retryPayment.visibility = View.GONE
            val sendf = intent.getStringExtra("send")

            if (TextUtils.isEmpty(name!!.text.toString().trim { it <= ' ' })) {
                Toast.makeText(this@PaymentActivity, " Name is invalid", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(
                    upivirtualid!!.text.toString().trim { it <= ' ' }
                )
            ) {
                Toast.makeText(this@PaymentActivity, " UPI ID is invalid", Toast.LENGTH_SHORT)
                    .show()
            } else if (TextUtils.isEmpty(note!!.text.toString().trim { it <= ' ' })) {
                Toast.makeText(this@PaymentActivity, " Note is invalid", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(amount!!.text.toString().trim { it <= ' ' })) {
                Toast.makeText(this@PaymentActivity, " Amount is invalid", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (ConnectionManager().checkConnectivity(this) && 1 == 1) {
                    payUsingUpi(
                        name!!.text.toString(), upivirtualid!!.text.toString(),
                        note!!.text.toString(), amount!!.text.toString()
                    )
                } else {
                    rl_Payment.visibility = View.GONE
                    rl_retryPayment.visibility = View.VISIBLE
                }

            }
        }
    }

    fun payUsingUpi(
        name: String,
        upiId: String,
        note: String,
        amount: String
    ) {
        Log.e("main ", "name $name--up--$upiId--$note--$amount")
        val uri = Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", upiId)
            .appendQueryParameter("pn", name)
            //.appendQueryParameter("mc", "")
            //.appendQueryParameter("tid", "02")
            //.appendQueryParameter("tr", "255846894")
            .appendQueryParameter("tn", note)
            .appendQueryParameter("am", amount)
            .appendQueryParameter("cu", "INR") //.appendQueryParameter("refUrl", "blueapp")
            .build()
        val upiPayIntent = Intent(Intent.ACTION_VIEW)
        upiPayIntent.data = uri

        // will always show a dialog to user to choose an app
        val chooser = Intent.createChooser(upiPayIntent, "Pay with")

        // check if intent resolves
        if (null != chooser.resolveActivity(packageManager)) {
            startActivityForResult(chooser, UPI_PAYMENT)
        } else {
            Toast.makeText(
                this@PaymentActivity,
                "No UPI app found, please install one to continue",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("main ", "response $resultCode")
        when (requestCode) {
            UPI_PAYMENT -> if (Activity.RESULT_OK == resultCode || resultCode == 11) {
                if (data != null) {
                    val trxt = data.getStringExtra("response")
                    Log.e("UPI", "onActivityResult: $trxt")
                    val dataList =
                        ArrayList<String>()
                    dataList.add(trxt.toString())
                    upiPaymentDataOperation(dataList)
                } else {
                    Log.e("UPI", "onActivityResult: " + "Return data is null")
                    val dataList =
                        ArrayList<String>()
                    dataList.add("nothing")
                    upiPaymentDataOperation(dataList)
                }
            } else {
                //when user simply back without payment
                Log.e("UPI", "onActivityResult: " + "Return data is null")
                val dataList =
                    ArrayList<String>()
                dataList.add("nothing")
                upiPaymentDataOperation(dataList)
            }
        }
    }

    private fun upiPaymentDataOperation(data: ArrayList<String>) {
        if (isConnectionAvailable(this@PaymentActivity)) {
            var str = data[0]
            Log.e("UPIPAY", "upiPaymentDataOperation: $str")
            var paymentCancel = ""
            if (str == null) str = "discard"
            var status = ""
            var approvalRefNo = ""
            val response = str.split("&".toRegex()).toTypedArray()
            for (i in response.indices) {
                val equalStr =
                    response[i].split("=".toRegex()).toTypedArray()
                if (equalStr.size >= 2) {
                    if (equalStr[0].toLowerCase() == "Status".toLowerCase()) {
                        status = equalStr[1].toLowerCase()
                    } else if (equalStr[0]
                            .toLowerCase() == "ApprovalRefNo".toLowerCase() || equalStr[0]
                            .toLowerCase() == "txnRef".toLowerCase()
                    ) {
                        approvalRefNo = equalStr[1]
                    }
                } else {
                    paymentCancel = "Payment cancelled by user."
                }
            }
            when {
                status == "success" -> {
                    if (mode.toString() == "Cart") {
                        progressDialog.setMessage("Processing Your Request....")
                        progressDialog.show()
                        val dataReference: DatabaseReference =
                            FirebaseDatabase.getInstance().reference.child("seller")
                                .child(shopId.toString()).child("Orders").child(orderId.toString())
                        val headers = HashMap<String, Any>()
                        headers["paymentMode"] = "Paid(Pay with Paytm)"
                        headers["transferId"] = ""
                        headers["settlementId"] = ""
                        dataReference.updateChildren(headers).addOnSuccessListener {
                            val userData: DatabaseReference =
                                FirebaseDatabase.getInstance().reference.child("users")
                                    .child(uid.toString()).child("MyOrders")
                                    .child(orderId.toString())
                            userData.updateChildren(headers).addOnSuccessListener {
                                progressDialog.dismiss()
                                prepareNotificationMessage(orderId.toString())
                            }
                        }
                    } else if (mode.toString() == "List") {
                        progressDialog.setMessage("Processing Your Request....")
                        progressDialog.show()
                        val dataReference: DatabaseReference =
                            FirebaseDatabase.getInstance().reference.child("seller")
                                .child(shopId.toString()).child("OrdersLists")
                                .child(orderId.toString())
                        val headers = HashMap<String, Any>()
                        headers["paymentMode"] = "Paid(Pay with Paytm)"
                        dataReference.updateChildren(headers).addOnSuccessListener {
                            val userData: DatabaseReference =
                                FirebaseDatabase.getInstance().reference.child("users")
                                    .child(uid.toString()).child("MyOrderList")
                                    .child(orderId.toString())
                            userData.updateChildren(headers).addOnSuccessListener {
                                progressDialog.dismiss()
                                prepareNewNotificationMessage(orderId.toString())
                            }
                        }


                    }

                    /*if (orderId.toString() == "800") {
                        //Code to handle successful transaction here.
                        progressDialog.setMessage("Placing Your Order....")
                        progressDialog.show()
                        val dataReference: DatabaseReference =
                            FirebaseDatabase.getInstance().reference.child("users")
                                .child(uid.toString()).child("Cart")
                        dataReference.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                (cartProducts as ArrayList<UserCartDetails>).clear()
                                for (i in snapshot.children) {
                                    val obj =
                                        UserCartDetails(
                                            i.child("productId").value.toString(),
                                            i.child("orderBy").value.toString(),
                                            i.child("productTitle").value.toString(),
                                            i.child("priceEach").value.toString(),
                                            i.child("finalPrice").value.toString(),
                                            i.child("finalQuantity").value.toString(),
                                            i.child("orderTo").value.toString(),
                                            i.child("productImageUrl").value.toString(),
                                            i.child("sellingPrice").value.toString(),
                                            i.child("finalsellingPrice").value.toString()
                                        )
                                    (cartProducts as ArrayList<UserCartDetails>).add(obj)
                                }
                            }
                        })
                        val timestamp = System.currentTimeMillis().toString()
                        val orderId = timestamp
                        val orderTime = timestamp
                        val orderStatus = "Pending"
                        val orderCost = totalCost.toString()
                        val orderBy = uid.toString()
                        val orderTo = shopId.toString()
                        val deliveryAddress = deliveryAddress.toString()
                        orderDetails =
                            ModelOrderDetails(
                                orderId,
                                orderTime,
                                orderStatus,
                                orderCost,
                                orderBy,
                                orderTo,
                                totalItem.toString(),
                                deliveryAddress,
                                "Paytm",
                                orderByName.toString(),
                                orderByMobile.toString()
                            )

                        val ref: DatabaseReference =
                            FirebaseDatabase.getInstance().reference.child("seller").child(orderTo)
                                .child("Orders")
                        ref.child(timestamp).setValue(orderDetails).addOnSuccessListener {
                            val reference: DatabaseReference =
                                FirebaseDatabase.getInstance().reference.child("users")
                                    .child(orderBy)
                                    .child("MyOrders")
                            reference.child(orderId).setValue(orderDetails)
                            for (i in 0 until cartProducts.size) {
                                val productId = cartProducts[i].productId
                                val orderedBy = cartProducts[i].orderBy
                                val productTitle = cartProducts[i].productTitle
                                val priceEach = cartProducts[i].priceEach
                                val finalPrice = cartProducts[i].finalPrice
                                val finalQuantity = cartProducts[i].finalQuantity
                                val orderedTo = cartProducts[i].orderTo
                                val sellingPrice = cartProducts[i].sellingPrice
                                val headers = HashMap<String, String>()
                                headers["productId"] = productId
                                headers["orderBy"] = orderedBy
                                headers["productTitle"] = productTitle
                                headers["priceEach"] = priceEach
                                headers["finalPrice"] = finalPrice
                                headers["finalQuantity"] = finalQuantity
                                headers["orderTo"] = orderedTo
                                headers["sellingPrice"] = sellingPrice
                                ref.child(timestamp).child("Items").child(productId)
                                    .setValue(headers)
                                reference.child(orderId).child("orderedItems").child(productId)
                                    .setValue(headers)
                            }
                            dataReference.removeValue()
                            progressDialog.dismiss()
                            prepareNotificationMessage(orderId)
                        }
                    } else {
                        progressDialog.setMessage("Placing Your Order....")
                        progressDialog.show()
                        val dataX: DatabaseReference =
                            FirebaseDatabase.getInstance().reference.child("seller")
                                .child(shopId.toString()).child("OrdersLists")
                                .child(orderId.toString())
                        val usersMap = HashMap<String, Any>()
                        usersMap["paymentMode"] = "Paytm"
                        dataX.updateChildren(usersMap).addOnCompleteListener {
                            val userData: DatabaseReference =
                                FirebaseDatabase.getInstance().reference.child("users")
                                    .child(uid.toString()).child("MyOrderList")
                                    .child(orderId.toString())
                            userData.updateChildren(usersMap).addOnCompleteListener {
                                progressDialog.dismiss()
                                prepareNewNotificationMessage(orderId.toString())
                            }
                        }
                    }*/
                    Log.e("UPI", "payment successful: $approvalRefNo")
                }
                "Payment cancelled by user." == paymentCancel -> {
                    Toast.makeText(
                        this@PaymentActivity,
                        "Payment cancelled by user.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    Log.e("UPI", "Cancelled by user: $approvalRefNo")
                }
                else -> {
                    Toast.makeText(
                        this@PaymentActivity,
                        "Transaction failed.Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("UPI", "failed payment: $approvalRefNo")
                }
            }
        } else {
            Log.e("UPI", "Internet issue: ")
            Toast.makeText(
                this@PaymentActivity,
                "Internet connection is not available. Please check and try again",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        fun isConnectionAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val netInfo = connectivityManager.activeNetworkInfo
                if (netInfo != null && netInfo.isConnected
                    && netInfo.isConnectedOrConnecting
                    && netInfo.isAvailable
                ) {
                    return true
                }
            }
            return false
        }
    }

    private fun prepareNewNotificationMessage(orderId: String) {
        val NOTIFICATION_TOPIC = "/topics/PUSH_NOTIFICATIONS"
        val NOTIFICATION_TITLE = "New Payment has been received"
        val NOTIFICATION_MESSAGE = "Amount of ₹${totalCost.toString()} received"
        val NOTIFICATION_TYPE = "PaymentMethod"
        val notificationJs = JSONObject()
        val notificationBodyJs = JSONObject()
        try {
            notificationBodyJs.put("notificationType", NOTIFICATION_TYPE)
            notificationBodyJs.put("buyerId", uid.toString())
            notificationBodyJs.put("sellerUid", shopId.toString())
            notificationBodyJs.put("orderId", orderId)
            notificationBodyJs.put("notificationTitle", NOTIFICATION_TITLE)
            notificationBodyJs.put("notificationMessage", NOTIFICATION_MESSAGE)
            //where to send
            notificationJs.put("to", NOTIFICATION_TOPIC)//to all who subscribed this topic
            notificationJs.put("data", notificationBodyJs)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
        sendFcmNotification(notificationJs, orderId)
    }

    private fun prepareNotificationMessage(orderId: String) {
        //when user places order,send notification to seller
        //prepare data for notification
        val NOTIFICATION_TOPIC =
            "/topics/PUSH_NOTIFICATIONS"//must be same as subscribed by user
        val NOTIFICATION_TITLE = "New payment has been received"
        val NOTIFICATION_MESSAGE = "Amount of ₹${totalCost.toString()} received"
        val NOTIFICATION_TYPE = "New Order"
        //prepare json(what to send and where to send)
        val notificationJs = JSONObject()
        val notificationBodyJs = JSONObject()
        try {
            //what to send
            notificationBodyJs.put("notificationType", NOTIFICATION_TYPE)
            notificationBodyJs.put("buyerId", uid.toString())
            notificationBodyJs.put("sellerUid", shopId.toString())
            notificationBodyJs.put("orderId", orderId)
            notificationBodyJs.put("notificationTitle", NOTIFICATION_TITLE)
            notificationBodyJs.put("notificationMessage", NOTIFICATION_MESSAGE)
            //where to send
            notificationJs.put("to", NOTIFICATION_TOPIC)//to all who subscribed this topic
            notificationJs.put("data", notificationBodyJs)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
        sendFcmNotification(notificationJs, orderId)

    }

    private fun sendFcmNotification(notificationJs: JSONObject, orderId: String) {

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            "https://fcm.googleapis.com/fcm/send",
            notificationJs,
            Response.Listener {
                //after sending fcm start order details activity
                val intent = Intent(this, NewActivity::class.java)
                startActivity(intent)

            },
            Response.ErrorListener {
                //if failed sending fcm,still start order details activity
                Toast.makeText(this, "Some error occured", Toast.LENGTH_SHORT).show()

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