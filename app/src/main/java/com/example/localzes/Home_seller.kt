package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home_seller.*

class Home_seller : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_seller)

        bottom_navHome_seller.selectedItemId = R.id.nav_home
        bottom_navHome_seller.setOnNavigationItemSelectedListener { item ->


            when(item.itemId){


                R.id.nav_product_seller->{

                    startActivity(Intent(this,Seller_Products::class.java))
                    overridePendingTransition(0,0)
                    finish()

                }
            }
            return@setOnNavigationItemSelectedListener false
        }
    }
}