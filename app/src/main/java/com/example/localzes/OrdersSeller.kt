package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import kotlinx.android.synthetic.main.activity_orders_seller.*

class OrdersSeller : AppCompatActivity() {
    private lateinit var card1:CardView
    private lateinit var card2:CardView
    private lateinit var card3:CardView
    private lateinit var card4:CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_seller)
        card1=findViewById(R.id.card1)
        card2=findViewById(R.id.card2)
        card3=findViewById(R.id.card3)
        card4=findViewById(R.id.card4)

        bottom_navOrders_seller.selectedItemId=R.id.nav_order_seller
        bottom_navOrders_seller.setOnNavigationItemSelectedListener {
            when (it.itemId) {


                R.id.nav_product_seller -> {

                    startActivity(Intent(this, Seller_Products::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_order_seller -> {

                   return@setOnNavigationItemSelectedListener true

                }
                R.id.nav_account_seller -> {

                    startActivity(Intent(this, AccountsSeller::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_store_seller -> {

                    startActivity(Intent(this, Home_seller::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_category_seller -> {

                    startActivity(Intent(this, Category::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
            }

            return@setOnNavigationItemSelectedListener false
        }
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
    }
}