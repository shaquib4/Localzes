package com.localze.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class ContactUsSeller : AppCompatActivity() {
    private lateinit var imgBackContact: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us_seller)
        imgBackContact = findViewById(R.id.imgBackContactSeller)
        imgBackContact.setOnClickListener {
            val intent = Intent(this, AccountsSeller::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, AccountsSeller::class.java)
        startActivity(intent)
        finish()
    }
}