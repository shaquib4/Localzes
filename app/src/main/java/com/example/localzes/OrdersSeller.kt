package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class OrdersSeller : AppCompatActivity() {
    private lateinit var card1:CardView
    private lateinit var card2:CardView
    private lateinit var card3:CardView
    private lateinit var card4:CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_seller)
        card1=findViewById(R.id.cardShare)
        card2=findViewById(R.id.cardQR)
        card3=findViewById(R.id.card3)
        card4=findViewById(R.id.cardBanner)


        card1.setOnClickListener {
            val intent=Intent(this,OrdersCompletedActivity::class.java)
            startActivity(intent)

        }
        card2.setOnClickListener {
            val intent=Intent(this,OrdersAcceptedActivity::class.java)
            startActivity(intent)

        }
        card3.setOnClickListener {
            val intent=Intent(this,OrderOutForDeliveryActivity::class.java)
            startActivity(intent)

        }
        card4.setOnClickListener {
            val intent=Intent(this,SellerOrdersActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val intent=Intent(applicationContext,Home_seller::class.java)
        startActivity(intent)
        finish()
    }
}