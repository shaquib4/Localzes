package com.localze.android

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PaymentByRazorpay : AppCompatActivity() {
    private lateinit var shopName:TextView
    private lateinit var priceDetails:TextView
    private lateinit var deliveryCharge:TextView
    private lateinit var taxes:TextView
    private lateinit var totalAmount:TextView
    private lateinit var progressDialog: ProgressDialog
    private lateinit var payNow:Button
    private var shopId: String? = "100"
    private var totalCost: String? = ""
    private var taxesCharges:String?="450"
    private var razorpayId:String?="400"
    private var sellerAmount:String?="600"
    private var uid: String? = "400"
    private var orderId: String? = "800"
    private var mode: String? = "900"
    private var custAm:String?="500"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_by_razorpay)
        shopName=findViewById(R.id.txtSellerName)
        priceDetails=findViewById(R.id.AmountPrice)
        deliveryCharge=findViewById(R.id.txtPayAmountDelivery)
        taxes=findViewById(R.id.charge)
        totalAmount=findViewById(R.id.TxtTotalPriceAmount)
        payNow=findViewById(R.id.paymentRazorpay)
        shopId=intent.getStringExtra("shopId")
        totalCost=intent.getStringExtra("totalCost")
        custAm=intent.getStringExtra("customerAmount")
        uid=intent.getStringExtra("orderBy")
        taxesCharges=intent.getStringExtra("taxes")
        orderId=intent.getStringExtra("orderId")
        mode=intent.getStringExtra("platform")
        razorpayId=intent.getStringExtra("razorpayId")
        sellerAmount=intent.getStringExtra("sellerAmount")

        val userDatabase=FirebaseDatabase.getInstance().reference.child("users").child(uid.toString())
        val databaseRef=FirebaseDatabase.getInstance().reference.child("seller").child(shopId.toString())
        databaseRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                shopName.text=snapshot.child("shop_name").value.toString().capitalize()
            }
        })
        userDatabase.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(mode.toString()=="Cart"){
                    userDatabase.child("MyOrders").child(orderId.toString()).addValueEventListener(object :ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(snapshot: DataSnapshot) {

                            taxes.text="₹"+kotlin.math.ceil(taxesCharges.toString().toDouble()).toString()
                            priceDetails.text="₹"+snapshot.child("orderCost").value.toString()
                            deliveryCharge.text="₹${snapshot.child("deliveryFee").value.toString()}"
                            totalAmount.text="₹"+kotlin.math.ceil(custAm.toString().toDouble()).toString()
                        }

                    })
                }else{
                    userDatabase.child("MyOrderList").child(orderId.toString()).addValueEventListener(object :ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            taxes.text="₹"+kotlin.math.ceil(taxesCharges.toString().toDouble()).toString()
                            totalAmount.text="₹"+kotlin.math.ceil(custAm.toString().toDouble()).toString()
                            priceDetails.text="₹"+snapshot.child("orderCost").value.toString()
                            deliveryCharge.text="₹${snapshot.child("deliveryFee").value.toString()}"

                        }

                    })
                }
            }

        })
        payNow.setOnClickListener {
            val intent= Intent(this,PaymentRazorpay::class.java)
            intent.putExtra("platform", mode.toString())
            intent.putExtra("shopId", shopId.toString())
            intent.putExtra("totalCost", totalCost.toString())
            intent.putExtra("orderBy", uid.toString())
            intent.putExtra("orderId", orderId.toString())
            intent.putExtra("razorpayId", razorpayId)
            intent.putExtra("customerAmount", custAm.toString())
            intent.putExtra("sellerAmount", sellerAmount.toString())
            startActivity(intent)
            finish()
        }

    }
}