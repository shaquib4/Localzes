package com.localze.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONArray
import org.json.JSONObject

class PaymentRazorpay : AppCompatActivity(), PaymentResultWithDataListener {
    private var amo = ""
    private var amoun = 0.0
    private var sellerAmount = 0.0
    private var sellerFinalAmount = 0.0
    private var ali = ""
    private var orderId = ""
    private var email = ""
    private var phone = ""
    private var shopId = ""
    private var platform = ""
    private var userId = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var userDatabase: DatabaseReference
    private var razorpayID = ""
    private var sellerRate: Double = 0.0
    private var userRate: Double = 0.0
    private var cAmount=0.0
    private var sAmount=0.0
    private var razorpayRate: Double = 0.0
    private var customerAmount: String = ""
    private var sellersAmount: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_razorpay)
        val amount = intent.getStringExtra("totalCost").toString()
        shopId = intent.getStringExtra("shopId").toString()
        orderId = intent.getStringExtra("orderId").toString()
        platform = intent.getStringExtra("platform").toString()
        userId = intent.getStringExtra("orderBy").toString()
        customerAmount = intent.getStringExtra("customerAmount").toString()
        sellersAmount = intent.getStringExtra("sellerAmount").toString()
        razorpayID = intent.getStringExtra("razorpayId").toString()
        userDatabase = FirebaseDatabase.getInstance().reference.child("seller").child(shopId)
        userDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                email = snapshot.child("email").value.toString()
                phone = snapshot.child("phone").value.toString()
            }
        })

        amo = amount
        amoun = (amo.toDouble() * userRate * 1.18) + (amo.toDouble())
        sellerAmount = (amoun) - (amoun * razorpayRate * 1.18)
        sellerFinalAmount = (sellerAmount) - (sellerAmount * sellerRate * 1.18)
        Checkout.preload(applicationContext)
        /* auth=FirebaseAuth.getInstance()
         val user=auth.currentUser
         val uid =user!!.uid*/
        cAmount= kotlin.math.ceil(customerAmount.toDouble())
        sAmount= kotlin.math.floor(sellersAmount.toDouble())

        if (amount.isEmpty()) {

            Toast.makeText(this, "amount is missing", Toast.LENGTH_LONG).show()
        } else {

            val orderRequest = JSONObject()
            val transfer = JSONObject()
            val transferRequest = JSONArray()
            try {
                orderRequest.put(
                    "amount",
                    cAmount.toDouble() * 100
                ) // amount in the smallest currency unit
                orderRequest.put("currency", "INR")
                orderRequest.put("receipt", "order_rcptid_11")
                orderRequest.put("payment_capture", 1)
                transfer.put("account", razorpayID)
                transfer.put("amount", sAmount.toDouble() * 100)
                transfer.put("currency", "INR")
                transferRequest.put(transfer)
                orderRequest.put("transfers", transferRequest)
                // orderRequest.put("order_id",PaymentData().orderId)

                order(orderRequest)
            } catch (e: Exception) {
                // Handle Exception
                println(e.printStackTrace())
            }

        }

    }

    private fun order(orderRequest: JSONObject) {
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            "https://api.razorpay.com/v1/orders",
            orderRequest,
            Response.Listener {
                //after sending fcm start order details activity
                val orderId = it.get("id")
                startPayment(orderId.toString())


            },
            Response.ErrorListener {
                //if failed sending fcm,still start order details activity
                Toast.makeText(this, ali, Toast.LENGTH_SHORT).show()

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()


                headers["Content-Type"] = "application/json"
                headers["Authorization"] =
                    "Basic cnpwX2xpdmVfdTdtUURuMGh6aE9Ick06ZU15aDRScE1CSHpMcVZRRDMxbGE5MGdi"
                // headers["Connection"]="Keep-alive"

                // headers["Authorization"] =

                return headers
            }
        }
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun startPayment(orderId: String) {
        val activity: Activity = this
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name", "Localze")
            options.put("description", "Order Charges")
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("theme.color", "#ff4500")
            options.put("currency", "INR")
            options.put("order_id", orderId)
            options.put("amount", cAmount.toDouble() * 100)//pass amount in currency subunits

            val prefill = JSONObject()
            prefill.put("email", email)
            prefill.put("contact", phone)

            options.put("prefill", prefill)
            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }


    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        try {
            Toast.makeText(this, "Payment error please try again", Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            Log.e("OnPaymentError", "Exception in onPaymentError", e)
        }
    }

    override fun onPaymentSuccess(s: String?, p1: PaymentData?) {
        Log.e("Done", " payment successful " + s.toString())
        Toast.makeText(this, p1?.orderId, Toast.LENGTH_SHORT).show()
        val headers = HashMap<String, Any>()
        headers["paymentMode"] = "Paid Online"
        if (platform == "Cart") {
            FirebaseDatabase.getInstance().reference.child("seller").child(shopId).child("Orders")
                .child(orderId).updateChildren(headers).addOnSuccessListener {
                    FirebaseDatabase.getInstance().reference.child("users").child(userId)
                        .child("MyOrders").child(orderId).updateChildren(headers)
                        .addOnSuccessListener {
                            startActivity(Intent(this, NewActivity::class.java))
                            finish()
                        }
                }
        } else {
            FirebaseDatabase.getInstance().reference.child("seller").child(shopId)
                .child("OrdersLists")
                .child(orderId).updateChildren(headers).addOnSuccessListener {
                    FirebaseDatabase.getInstance().reference.child("users").child(userId)
                        .child("MyOrderList").child(orderId).updateChildren(headers)
                        .addOnSuccessListener {
                            startActivity(Intent(this, NewActivity::class.java))
                            finish()
                        }
                }
        }
    }
}