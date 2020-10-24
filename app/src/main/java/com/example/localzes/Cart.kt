package com.example.localzes

import android.accounts.Account
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_cart.*

class Cart : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        bottom_navCart.selectedItemId = R.id.nav_cart
        bottom_navCart.setOnNavigationItemSelectedListener { item ->


            when(item.itemId){

                R.id.nav_home->{
                    startActivity(Intent(this,Home::class.java))
                    overridePendingTransition(0,0)
                    finish()

                }
                R.id.nav_search->{

                    startActivity(Intent(this,Search::class.java))
                    overridePendingTransition(0,0)
                    finish()

                }
                R.id.nav_cart->{

                    return@setOnNavigationItemSelectedListener true

                }
                R.id.nav_account->{

                    startActivity(Intent(this, Accounts::class.java))
                    overridePendingTransition(0,0)
                    finish()

                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }
}