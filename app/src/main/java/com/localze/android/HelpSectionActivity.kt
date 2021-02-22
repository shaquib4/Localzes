package com.localze.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HelpSectionActivity : AppCompatActivity() {
    private lateinit var imgBackCustomer: ImageView
    private lateinit var fbLocalze: ImageView
    private lateinit var twitterLocalze: ImageView
    private lateinit var instaLocalze: ImageView
    private lateinit var website: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_section)
        imgBackCustomer = findViewById(R.id.imgBackContactCustomer)
        fbLocalze = findViewById(R.id.ic_fb)
        twitterLocalze = findViewById(R.id.ic_twitter)
        instaLocalze = findViewById(R.id.ic_ig)
        website = findViewById(R.id.txtLink)
        website.setOnClickListener {
            val webLink = "https://www.localze.com"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webLink))
            val chooser = Intent.createChooser(intent, "Open With")
            startActivity(chooser)
        }
        imgBackCustomer.setOnClickListener {
            val intent = Intent(this, Accounts::class.java)
            startActivity(intent)
            finish()
        }
        fbLocalze.setOnClickListener {
            try {
                packageManager.getPackageInfo("com.facebook.katana", 0)
                val uriFb: Uri = Uri.parse("fb://page/101131415336236")
                startActivity(Intent(Intent.ACTION_VIEW, uriFb))
            } catch (e: Exception) {
                val uriFb: Uri = Uri.parse("https://www.facebook.com/Localze-Team-101131415336236")
                startActivity(Intent(Intent.ACTION_VIEW, uriFb))
            }
        }
        twitterLocalze.setOnClickListener {
            try {
                val twitterLink = "https://twitter.com/localze?s=20"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(twitterLink))
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        instaLocalze.setOnClickListener {
            try {
                val instaLink = "https://www.instagram.com/localzeteam/"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instaLink))
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
/*        emailId = findViewById(R.id.txtEmail)
        ic_fb.setOnClickListener {
            try {
                packageManager.getPackageInfo("com.facebook.katana", 0)
                val uriFb: Uri = Uri.parse("fb://page/101131415336236")
                startActivity(Intent(Intent.ACTION_VIEW, uriFb))
            } catch (e: Exception) {
                val uriFb: Uri = Uri.parse("https://www.facebook.com/Localze-Team-101131415336236")
                startActivity(Intent(Intent.ACTION_VIEW, uriFb))
            }
        }
        ic_gmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.data = Uri.parse("mailto:localzeteam@gmail.com")
            intent.type = "text/plain"
            try {
                startActivity(Intent.createChooser(intent, "Choose an Email Client"))
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        ic_twitter.setOnClickListener {

        }
        ic_ig.setOnClickListener {
            val string = "https://www.instagram.com/localzeteam/"
            gotoUrl(string)
        }*/
    }

    override fun onBackPressed() {
        val intent = Intent(this, Accounts::class.java)
        startActivity(intent)
        finish()
    }
}