package com.localze.android

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class ContactUsSeller : AppCompatActivity() {
    private lateinit var imgBackContact: ImageView
    private lateinit var fb_localze: ImageView
    private lateinit var twitter_localze: ImageView
    private lateinit var insta_localze: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us_seller)
        imgBackContact = findViewById(R.id.imgBackContactSeller)
        fb_localze = findViewById(R.id.ic_fb_seller)
        twitter_localze = findViewById(R.id.ic_twitter_seller)
        insta_localze = findViewById(R.id.ic_ig_seller)
        imgBackContact.setOnClickListener {
            val intent = Intent(this, AccountsSeller::class.java)
            startActivity(intent)
            finish()
        }
        fb_localze.setOnClickListener {
            try {
                packageManager.getPackageInfo("com.facebook.katana", 0)
                val uriFb: Uri = Uri.parse("fb://page/101131415336236")
                startActivity(Intent(Intent.ACTION_VIEW, uriFb))
            } catch (e: Exception) {
                val uriFb: Uri = Uri.parse("https://www.facebook.com/Localze-Team-101131415336236")
                startActivity(Intent(Intent.ACTION_VIEW, uriFb))
            }
        }
        twitter_localze.setOnClickListener {
            try {
                val twitterLink = "https://twitter.com/localze?s=20"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(twitterLink))
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        insta_localze.setOnClickListener {
            try {
                val instaLink = "https://www.instagram.com/localzeteam/"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instaLink))
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, AccountsSeller::class.java)
        startActivity(intent)
        finish()
    }
}