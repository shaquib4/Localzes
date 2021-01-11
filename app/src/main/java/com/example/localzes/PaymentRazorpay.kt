package com.example.localzes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject

class PaymentRazorpay : AppCompatActivity(),PaymentResultWithDataListener {
    private var amo=""
    private var ali=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_razorpay)
        val amount=intent.getStringExtra("totalCost").toString()
        amo=amount

        Checkout.preload(applicationContext)

        if (amount.isEmpty()){
            
            Toast.makeText(this,"amount is missing",Toast.LENGTH_LONG).show()
        }else{

            val orderRequest = JSONObject()
            try {
               
                orderRequest.put("amount", amo.toDouble()*100) // amount in the smallest currency unit
                orderRequest.put("currency", "INR")
                orderRequest.put("receipt", "order_rcptid_11")
                orderRequest.put("payment_capture", 1)
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
                startPayment()


            },
            Response.ErrorListener {
                //if failed sending fcm,still start order details activity
                Toast.makeText(this, ali.toString(), Toast.LENGTH_SHORT).show()

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()


                headers["Content-Type"] = "application/json"
                headers["Authorization"] ="Basic cnpwX3Rlc3RfWmdNSU5lSWVBQTZKOFg6V2tWZTczaXBvS0J2eUdtZUtqOFpMQVRL"
               // headers["Connection"]="Keep-alive"

               // headers["Authorization"] =

                return headers
            }
        }
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun startPayment() {
        val activity: Activity = this
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name","Localze")
            options.put("description","Order Charges")
            //You can omit the image option to fetch the image from dashboard
            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("theme.color", "#3399cc");
            options.put("currency","INR");
            options.put("order_id", PaymentData().orderId);
            options.put("amount",amo.toDouble()*100)//pass amount in currency subunits

            val prefill = JSONObject()
            prefill.put("email","mohammadshaquib4@gmail.com")
            prefill.put("contact","8528436997")

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
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
        Log.e("Done", " payment successful "+ s.toString());
        Toast.makeText(this, p1?.orderId, Toast.LENGTH_SHORT).show();
        startActivity(Intent(this,NewActivity::class.java))
        finish()
    }
}