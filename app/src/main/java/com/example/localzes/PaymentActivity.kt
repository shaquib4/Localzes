package com.example.localzes

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
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*
import java.util.ArrayList

class PaymentActivity : AppCompatActivity() {
    var amount: TextView? = null
    var note: TextView? = null
    var name: TextView? = null
    var upivirtualid: TextView? = null
    var send: Button? = null
    var TAG = "main"
    private var shopId: String? = "100"
    private var totalCost: String? = "200"
    private var totalItem: String? = "300"
    private var uid: String? = "400"
    private lateinit var progressDialog: ProgressDialog
    private lateinit var orderDetails: ModelOrderDetails
    private lateinit var cartProducts: List<UserCartDetails>
    val UPI_PAYMENT = 0
    private lateinit var userDatabase: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)
        totalItem = intent.getStringExtra("totalItem")
        shopId = intent.getStringExtra("shopId")
        totalCost = intent.getStringExtra("totalCost")
        uid = intent.getStringExtra("orderBy")
        send = findViewById<View>(R.id.btnPayNow) as Button
        amount = findViewById<View>(R.id.txtPayAmount) as TextView
        note = findViewById<View>(R.id.txtReason) as TextView
        name = findViewById<View>(R.id.txtSellerName) as TextView
        upivirtualid = findViewById<View>(R.id.txtUPI) as TextView
        userDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(shopId.toString())

        userDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val names=snapshot.child("shop_name").value.toString()
                val upiId=snapshot.child("upi").value.toString()
                name!!.text=names
                upivirtualid!!.text=upiId
            }

        })



        send!!.setOnClickListener { //Getting the values from the Texts
            val sendf = intent.getStringExtra("send")
            amount!!.text = totalCost.toString()
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
                payUsingUpi(
                    name!!.text.toString(), upivirtualid!!.text.toString(),
                    note!!.text.toString(), amount!!.text.toString()
                )
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
            .appendQueryParameter("pn", name) //.appendQueryParameter("mc", "")
            //.appendQueryParameter("tid", "02125412")
            //.appendQueryParameter("tr", "25584584")
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
            if (status == "success") {
                //Code to handle successful transaction here.
                progressDialog.setMessage("Placing Your Order....")
                progressDialog.show()

                val timestamp = System.currentTimeMillis().toString()
                val orderId = timestamp
                val orderTime = timestamp
                val orderStatus = "Pending"
                val orderCost = totalCost.toString()
                val orderBy = uid
                val orderTo = shopId.toString()
                orderDetails =
                    ModelOrderDetails(
                        orderId,
                        orderTime,
                        orderStatus,
                        orderCost,
                        orderBy.toString(),
                        orderTo,
                        totalItem.toString()
                    )
                val reference: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("users")
                        .child(orderBy.toString())
                        .child("MyOrders")
                reference.child(orderId).setValue(orderDetails)
                val ref: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("seller").child(orderTo)
                        .child("Orders")
                ref.child(timestamp).setValue(orderDetails).addOnSuccessListener {
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
                        ref.child(timestamp).child("Items").child(productId).setValue(headers)
                        reference.child(orderId).child("orderedItems").child(productId)
                            .setValue(headers).addOnCompleteListener {
                            if (it.isSuccessful) {
                                progressDialog.dismiss()
                                //Toast.makeText(this, "Your order has been successfully placed", Toast.LENGTH_SHORT)
                                //.show()
                                startActivity(Intent(this@PaymentActivity, NewActivity::class.java))
                            }
                        }
                    }


                }

                Log.e("UPI", "payment successful: $approvalRefNo")
            } else if ("Payment cancelled by user." == paymentCancel) {
                Toast.makeText(
                    this@PaymentActivity,
                    "Payment cancelled by user.",
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.e("UPI", "Cancelled by user: $approvalRefNo")
            } else {
                Toast.makeText(
                    this@PaymentActivity,
                    "Transaction failed.Please try again",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("UPI", "failed payment: $approvalRefNo")
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
}