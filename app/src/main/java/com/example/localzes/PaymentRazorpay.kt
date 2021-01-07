package com.example.localzes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class PaymentRazorpay : AppCompatActivity(),PaymentResultListener {
    private var amo=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_razorpay)
        val amount=intent.getStringExtra("totalCost").toString()
        amo=amount

        Checkout.preload(applicationContext)

        if (amount.isEmpty()){
            Toast.makeText(this,"amount is missing",Toast.LENGTH_LONG).show()
        }else{
          startPayment()
        }

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
           // options.put("order_id", "order_DBJOWzybf0sJbb");
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

    override fun onPaymentError(p0: Int, p1: String?) {
        try {
            Toast.makeText(this, "Payment error please try again", Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            Log.e("OnPaymentError", "Exception in onPaymentError", e)
        }
    }

    override fun onPaymentSuccess(s: String?) {
        Log.e("Done", " payment successful "+ s.toString());
        Toast.makeText(this, "Payment successfully done! $s", Toast.LENGTH_SHORT).show();
        startActivity(Intent(this,NewActivity::class.java))
        finish()
    }
}