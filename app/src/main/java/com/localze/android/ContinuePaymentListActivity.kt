package com.localze.android

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.*
import org.json.JSONObject

class ContinuePaymentListActivity : AppCompatActivity() {
    private var radioGroup: RadioGroup? = null
    lateinit var button: Button
    private lateinit var productCharges: TextView
    private lateinit var shippingCharges: TextView
    private lateinit var totalCharges: TextView
    private lateinit var totalChargesTv: TextView
    private var amoun = 0.0
    private var sellerAmount = 0.0
    private var sellerFinalAmount = 0.0
    private lateinit var progressDialog: ProgressDialog
    private lateinit var imgBackContinue: ImageView
    private var shopId: String? = "100"
    private var totalCost: String? = "200"
    private var orderId: String? = "300"
    private var orderBy: String? = "400"
    private var orderDeliveryFee: String? = "500"
    private var razorpayId: String = ""
    private var sellerRate: Double = 0.0
    private var userRate: Double = 0.0
    private var razorpayRate: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_continue_payment_list)
        radioGroup = findViewById(R.id.radioPaymentList)
        productCharges = findViewById(R.id.txtPCPriceList)
        shippingCharges = findViewById(R.id.txtSCPriceList)
        totalCharges = findViewById(R.id.txtTotalPayPriceList)
        totalChargesTv = findViewById(R.id.txtPriceList)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)
        imgBackContinue = findViewById(R.id.imgBackContinueList)
        shopId = intent.getStringExtra("shopId")
        totalCost = intent.getStringExtra("totalCost")
        orderId = intent.getStringExtra("orderId")
        orderBy = intent.getStringExtra("orderBy")
        orderDeliveryFee = intent.getStringExtra("deliveryFee")
        button = findViewById(R.id.btnPayContinueList)
        totalChargesTv.text = "₹" + totalCost.toString() + "/-"
        totalCharges.text = "₹" + totalCost.toString()
        val databaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(orderBy.toString())
                .child("MyOrderList").child(orderId.toString())
        val databaseRefer = FirebaseDatabase.getInstance().reference.child("RateSection")
        val databaseRef =
            FirebaseDatabase.getInstance().reference.child("seller").child(shopId.toString())
        databaseRefer.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                razorpayRate = snapshot.child("razorpayRate").value.toString().toDouble()
                userRate = snapshot.child("customerRate").value.toString().toDouble()
                sellerRate = snapshot.child("sellerRate").value.toString().toDouble()
            }

        })
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                razorpayId = snapshot.child("razorpayId").value.toString()
            }

        })
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val productCharge = totalCost.toString()
                        .toDouble() - (snapshot.child("deliveryFee").value.toString()).toDouble()
                    productCharges.text = "₹$productCharge"
                    shippingCharges.text = "₹${snapshot.child("deliveryFee").value.toString()}"
                } catch (
                    e: Exception
                ) {
                    e.printStackTrace()
                }
            }
        })
        button.setOnClickListener {
            val id = radioGroup!!.checkedRadioButtonId
            val radioButton = findViewById<RadioButton>(id)
            when (radioButton.text) {
                "Pay on Delivery" -> {
                    progressDialog.setMessage("Processing Your Request")
                    progressDialog.show()
                    val dataReference: DatabaseReference =
                        FirebaseDatabase.getInstance().reference.child("seller")
                            .child(shopId.toString())
                            .child("OrdersLists").child(orderId.toString())
                    val headers = HashMap<String, Any>()
                    headers["paymentMode"] = "Unpaid(Cash on Delivery)"
                    headers["transferId"] = ""
                    headers["settlementId"] = ""
                    dataReference.updateChildren(headers).addOnCompleteListener {
                        val userData: DatabaseReference =
                            FirebaseDatabase.getInstance().reference.child("users")
                                .child(orderBy.toString()).child("MyOrderList")
                                .child(orderId.toString())
                        userData.updateChildren(headers).addOnCompleteListener {
                            progressDialog.dismiss()
                            prepareNewNotificationMessage(orderId.toString())
                        }
                    }
                }
                "Pay with Paytm" -> {
                    val intent = Intent(this, PaymentActivity::class.java)
                    intent.putExtra("platform", "List")
                    intent.putExtra("shopId", shopId.toString())
                    intent.putExtra("totalCost", totalCost.toString())
                    intent.putExtra("orderId", orderId.toString())
                    intent.putExtra("orderBy", orderBy.toString())
                    intent.putExtra("deliveryFee", orderDeliveryFee.toString())
                    startActivity(intent)
                    finish()
                }
                "Pay with Razor Pay" -> {
                    val taxes=totalCost.toString().toDouble() * userRate * 1.18
                    amoun =
                        (totalCost.toString().toDouble() * userRate * 1.18) + (totalCost.toString()
                            .toDouble())
                    sellerAmount = (amoun) - (amoun * razorpayRate * 1.18)
                    sellerFinalAmount = (sellerAmount) - (sellerAmount * sellerRate * 1.18)
                    val intent = Intent(this, PaymentByRazorpay::class.java)
                    intent.putExtra("platform", "List")
                    intent.putExtra("shopId", shopId.toString())
                    intent.putExtra("totalCost", totalCost.toString())
                    intent.putExtra("orderId", orderId.toString())
                    intent.putExtra("orderBy", orderBy.toString())
                    intent.putExtra("razorpayId",razorpayId)
                    intent.putExtra("customerAmount", amoun.toString())
                    intent.putExtra("sellerAmount", sellerFinalAmount.toString())
                    intent.putExtra("taxes",taxes.toString())
                    startActivity(intent)
                    finish()
                }
            }
        }
        imgBackContinue.setOnClickListener {

        }
    }

    private fun prepareNewNotificationMessage(orderId: String) {
        val NOTIFICATION_TOPIC = "/topics/PUSH_NOTIFICATIONS"
        val NOTIFICATION_TITLE = "Cash On Delivery"
        val NOTIFICATION_MESSAGE =
            "Amount of ₹${totalCost.toString()} will be collected on delivery"
        val NOTIFICATION_TYPE = "PaymentMethod"
        val notificationJs = JSONObject()
        val notificationBodyJs = JSONObject()
        try {
            notificationBodyJs.put("notificationType", NOTIFICATION_TYPE)
            notificationBodyJs.put("buyerId", orderBy.toString())
            notificationBodyJs.put("sellerUid", shopId.toString())
            notificationBodyJs.put("orderId", orderId)
            notificationBodyJs.put("notificationTitle", NOTIFICATION_TITLE)
            notificationBodyJs.put("notificationMessage", NOTIFICATION_MESSAGE)
            notificationJs.put("to", NOTIFICATION_TOPIC)//to all who subscribed this topic
            notificationJs.put("data", notificationBodyJs)
        } catch (e: Exception) {
            e.printStackTrace()
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