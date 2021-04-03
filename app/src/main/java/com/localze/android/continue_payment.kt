package com.localze.android

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.localze.android.Modals.ModelOrderDetails
import com.localze.android.Modals.UserCartDetails
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_continue_payment.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.MutableMap
import kotlin.collections.set

class continue_payment : AppCompatActivity() {

    private var radioGroup: RadioGroup? = null
    lateinit var button: Button
    private lateinit var productCharges: TextView
    private lateinit var shippingCharges: TextView
    private lateinit var totalCharges: TextView
    private lateinit var totalChargesTv: TextView
    private var shopId: String? = "100"
    private var totalCost: String? = "200"
    private var uid: String? = "400"
    private var orderId: String? = "800"
    private lateinit var progressDialog: ProgressDialog
    private lateinit var cartProducts: List<UserCartDetails>
    private lateinit var orderDetails: ModelOrderDetails
    private lateinit var imgBackContinue: ImageView
    private var orderDeliveryFee: String? = "900"
    private var uidLists: String = ""
    private var upiId: String = ""
    private var razorpayId: String = ""
    private var sellerRate: Double = 0.0
    private var userRate: Double = 0.0
    private var razorpayRate: Double = 0.0
    private var amoun = 0.0
    private var sellerAmount = 0.0
    private var sellerFinalAmount = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_continue_payment)
        productCharges = findViewById(R.id.txtPCPrice)
        shippingCharges = findViewById(R.id.txtSCPrice)
        totalCharges = findViewById(R.id.txtTotalPayPrice)
        totalChargesTv = findViewById(R.id.txtPrice)
        imgBackContinue = findViewById(R.id.imgBackContinue)
        shopId = intent.getStringExtra("shopId")
        totalCost = intent.getStringExtra("totalCost")
        uid = intent.getStringExtra("orderBy")
        orderId = intent.getStringExtra("orderId")
        orderDeliveryFee = intent.getStringExtra("deliveryFee")
        cartProducts = ArrayList<UserCartDetails>()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)
        radioGroup = findViewById(R.id.radioPayment)
        totalCharges.text = "₹${totalCost}"
        totalChargesTv.text = "₹${totalCost}"
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(uid.toString())
                .child("MyOrders").child(orderId.toString())
        val databaseRef =
            FirebaseDatabase.getInstance().reference.child("seller").child(shopId.toString())
        val databaseRefer = FirebaseDatabase.getInstance().reference.child("RazorpayRates")
        databaseRefer.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                razorpayRate = snapshot.child("razorpayCRate").value.toString().toDouble()
                userRate = snapshot.child("rateSeller").value.toString().toDouble()
                sellerRate = snapshot.child("rateCustomer").value.toString().toDouble()
            }

        })
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                upiId = snapshot.child("upi").value.toString()
                razorpayId = snapshot.child("razorpayId").value.toString()
            }

        })
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val productCharge = totalCost.toString()
                        .toDouble() - (snapshot.child("deliveryFee").value.toString()).toDouble()
                    productCharges.text = "₹$productCharge"
                    shippingCharges.text = "₹${snapshot.child("deliveryFee").value.toString()}"
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        })
        btnPayContinue.setOnClickListener {
            val id = radioGroup!!.checkedRadioButtonId
            val radioButton = findViewById<RadioButton>(id)
            when (radioButton.text) {
                "Pay on Delivery" -> {
                    progressDialog.setMessage("Processing Your Request...")
                    progressDialog.show()
                    val dataReference: DatabaseReference =
                        FirebaseDatabase.getInstance().reference.child("seller")
                            .child(shopId.toString()).child("Orders").child(orderId.toString())
                    val headers = HashMap<String, Any>()
                    headers["paymentMode"] = "Unpaid(Cash on Delivery)"
                    dataReference.updateChildren(headers).addOnSuccessListener {
                        val userRef: DatabaseReference =
                            FirebaseDatabase.getInstance().reference.child("users")
                                .child(uid.toString()).child("MyOrders").child(orderId.toString())
                        userRef.updateChildren(headers).addOnSuccessListener {
                            progressDialog.dismiss()
                            prepareNotificationMessage(orderId.toString())
                        }
                    }

                }
                "Pay with Paytm" -> {
                    /*if (upiId == "") {
                        Toast.makeText(
                            this,
                            "This shop does not accept payment via upi",
                            Toast.LENGTH_LONG
                        ).show()

                    } else {*/
                    val intent = Intent(this, PaymentActivity::class.java)
                    intent.putExtra("platform", "Cart")
                    intent.putExtra("shopId", shopId.toString())
                    intent.putExtra("totalCost", totalCost.toString())
                    intent.putExtra("orderId", orderId.toString())
                    intent.putExtra("orderBy", uid.toString())
                    intent.putExtra("deliveryFee", orderDeliveryFee.toString())
                    startActivity(intent)
                    finish()
                }
                "Pay with Razor Pay" -> {
                    /*if (razorpayId == "") {
                        Toast.makeText(
                            this,
                            "This shop does not accept payment via upi",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {*/
                    amoun =
                        (totalCost.toString().toDouble() * userRate * 1.18) + (totalCost.toString()
                            .toDouble())
                    sellerAmount = (amoun) - (amoun * razorpayRate * 1.18)
                    sellerFinalAmount = (sellerAmount) - (sellerAmount * sellerRate * 1.18)
                    val intent = Intent(this, PaymentRazorpay::class.java)
                    intent.putExtra("platform", "Cart")
                    intent.putExtra("shopId", shopId.toString())
                    intent.putExtra("totalCost", totalCost.toString())
                    intent.putExtra("orderBy", uid.toString())
                    intent.putExtra("orderId", orderId.toString())
                    intent.putExtra("razorpayId", razorpayId)
                    intent.putExtra("customerAmount", amoun.toString())
                    intent.putExtra("sellerAmount", sellerFinalAmount)
                    /*intent.putExtra("totalItem", totalItem.toString())
                    intent.putExtra("delivery", deliveryAddress.toString())
                    intent.putExtra("orderByName", orderByName.toString())
                    intent.putExtra("orderByMobile", orderByMobile.toString())*/
                    startActivity(intent)
                    finish()
                }
            }
        }
/*        retryContinuePayment.setOnClickListener {
            this.recreate()
        }
        if (ConnectionManager().checkConnectivity(this)) {
            btnPayContinue.setOnClickListener {
                rl_retryContinuePayment.visibility = View.GONE
                rl_continuePayment.visibility = View.VISIBLE
                val id = radioGroup!!.checkedRadioButtonId
                val radioButton = findViewById<RadioButton>(id)
                if (ConnectionManager().checkConnectivity(this) && 1 == 1) {
                    rl_retryContinuePayment.visibility = View.GONE
                    rl_continuePayment.visibility = View.VISIBLE
                    when (radioButton.text) {
                        "Pay on Delivery" -> {
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
                                    "Cash on Delivery",
                                    orderByName.toString(),
                                    orderByMobile.toString()
                                )

                            val ref: DatabaseReference =
                                FirebaseDatabase.getInstance().reference.child("seller")
                                    .child(orderTo)
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
                        }
                        "Pay with Paytm" -> {
                            val intent = Intent(this, PaymentActivity::class.java)
                            intent.putExtra("shopId", shopId.toString())
                            intent.putExtra("totalCost", totalCost.toString())
                            intent.putExtra("orderBy", uid.toString())
                            intent.putExtra("totalItem", totalItem.toString())
                            intent.putExtra("delivery", deliveryAddress.toString())
                            intent.putExtra("orderByName", orderByName.toString())
                            intent.putExtra("orderByMobile", orderByMobile.toString())
                            startActivity(intent)
                            finish()
                        }
*//*                        "Pay with Razor Pay" -> {
                            val intent=Intent(this,PaymentRazorpay::class.java)
                            intent.putExtra("shopId", shopId.toString())
                            intent.putExtra("totalCost", totalCost.toString())
                            intent.putExtra("orderBy", uid.toString())
                            intent.putExtra("totalItem", totalItem.toString())
                            intent.putExtra("delivery", deliveryAddress.toString())
                            intent.putExtra("orderByName", orderByName.toString())
                            intent.putExtra("orderByMobile", orderByMobile.toString())
                            startActivity(intent)
                            finish()
                        }*//*
                        else -> {
                            btnPayContinue.isClickable = false
                        }
                    }
                } else {
                    rl_retryContinuePayment.visibility = View.VISIBLE
                    rl_continuePayment.visibility = View.GONE
                }
            }
            productCharges.text = "₹" + totalCost.toString()
            totalChargesTv.text = "₹" + totalCost.toString() + "/-"
            totalCharges.text = "₹" + totalCost.toString()
            imgBackContinue.setOnClickListener {
                val intent = Intent(this, Cart::class.java)
                startActivity(intent)
                finish()
            }
        }*/ /*else {
            rl_retryContinuePayment.visibility = View.VISIBLE
            rl_continuePayment.visibility = View.GONE
        }*/
    }

    private fun prepareNotificationMessage(orderId: String) {
//when user places order,send notification to seller
        //prepare data for notification
        val databaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(shopId.toString())
                .child("MyStaff")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val uidList = arrayListOf<String>()
                for (i in snapshot.children) {
                    uidList.clear()
                    if (i.child("status").value.toString() == "Active" && (i.child("access").value.toString() == "Total Access" || i.child(
                            "access"
                        ).value.toString() == "Order Access")
                    ) {
                        val uid = i.child("uid").value.toString()
                        uidList.add(uid)
                        for (j in uidList) {
                            uidLists += "$j,"
                        }
                    }
                }
                uidLists += shopId
                val NOTIFICATION_TOPIC =
                    "/topics/PUSH_NOTIFICATIONS"//must be same as subscribed by user
                val NOTIFICATION_TITLE = "Cash On Delivery"
                val NOTIFICATION_MESSAGE =
                    "Amount ₹${totalCost.toString()} will be collected on delivery"
                val NOTIFICATION_TYPE = "New Order"
                //prepare json(what to send and where to send)
                val notificationJs = JSONObject()
                val notificationBodyJs = JSONObject()
                try {
                    //what to send
                    notificationBodyJs.put("notificationType", NOTIFICATION_TYPE)
                    notificationBodyJs.put("buyerId", uid.toString())
                    notificationBodyJs.put("sellerUid", shopId.toString())
                    notificationBodyJs.put("ListOfIds", uidLists)
                    notificationBodyJs.put("orderId", orderId)
                    notificationBodyJs.put("notificationTitle", NOTIFICATION_TITLE)
                    notificationBodyJs.put("notificationMessage", NOTIFICATION_MESSAGE)
                    //where to send
                    notificationJs.put("to", NOTIFICATION_TOPIC)//to all who subscribed this topic
                    notificationJs.put("data", notificationBodyJs)
                } catch (e: Exception) {
                    Toast.makeText(this@continue_payment, e.message, Toast.LENGTH_SHORT).show()
                }
                sendFcmNotification(notificationJs, orderId)
            }
        })
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
                finish()

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

    override fun onBackPressed() {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        finish()
    }
}