package com.localzes.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_help_section.*

class HelpSectionActivity : AppCompatActivity() {
    private lateinit var emailId: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_section)
        emailId = findViewById(R.id.txtEmail)
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
            intent.data = Uri.parse("mailto:")
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, "localzeteam@gmail.com")
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
        }
    }

    private fun gotoUrl(s: String) {
        val uri: Uri = Uri.parse(s)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

}