package com.localzes.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_card_banners.*

class cardBanners : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_banners)

        bottom_navOrders_seller.selectedItemId = R.id.nav_order_seller
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
        cardQR.setOnClickListener {
            startActivity(Intent(this, generateQRcode::class.java))
            finish()
        }
        cardBanner.setOnClickListener {
            startActivity(Intent(this, ShopBanner::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, Home_seller::class.java)
        startActivity(intent)
        finish()
    }
}