package com.localze.android

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class HelpSectionActivity : AppCompatActivity() {
    private lateinit var imgBackCustomer: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_section)
        imgBackCustomer = findViewById(R.id.imgBackContactCustomer)
        imgBackCustomer.setOnClickListener {
            val intent = Intent(this, Accounts::class.java)
            startActivity(intent)
            finish()
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