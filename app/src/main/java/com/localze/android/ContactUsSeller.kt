package com.localze.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ContactUsSeller : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us_seller)
    }

    override fun onBackPressed() {
        val intent = Intent(this, AccountsSeller::class.java)
        startActivity(intent)
        finish()
    }
}