package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_search.*

class Search : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        bottom_navSearch.selectedItemId = R.id.nav_search
        bottom_navSearch.setOnNavigationItemSelectedListener { item ->


            when(item.itemId){

                R.id.nav_home->{
                    startActivity(Intent(this,Home::class.java))
                    overridePendingTransition(0,0)
                    finish()

                }
                R.id.nav_search->{

                    return@setOnNavigationItemSelectedListener true

                }
                R.id.nav_cart->{


                    startActivity(Intent(this,Cart::class.java))
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.nav_account->{

                    startActivity(Intent(this,Accounts::class.java))
                    overridePendingTransition(0,0)
                    finish()

                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }
}