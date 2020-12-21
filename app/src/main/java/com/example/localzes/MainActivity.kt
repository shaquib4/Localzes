package com.example.localzes

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        if (restorePrefData()) {
            btnRegButton.setOnClickListener {
                register()
            }
        } else {
            btnRegButton.setOnClickListener {
                subscribeToTopic()
                savePrefData()
                register()
            }
        }
    }

    private fun register() {
        val phone = edtmobile.text.toString()
        if (edtmobile.text.toString().isEmpty()) {
            edtmobile.error = "please enter your phone number"
            return
        }
        val intent = Intent(this, Verify::class.java)
        intent.putExtra("phone", phone)
        startActivity(intent)
        finish()

    }

    private fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("PUSH_NOTIFICATIONS")
            .addOnCompleteListener { task ->
                var msg = "Notifications Are Enabled"
                if (!task.isSuccessful) {
                    msg = "Notifications are not enabled"
                }
            }
    }

    private fun savePrefData() {
        val pref: SharedPreferences = applicationContext.getSharedPreferences(
            "Enabled",
            Context.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putBoolean("isEnabled", true)
        editor.apply()
    }

    private fun restorePrefData(): Boolean {
        val pref: SharedPreferences =
            applicationContext.getSharedPreferences("Enabled", Context.MODE_PRIVATE)
        val isEnabledNotification = pref.getBoolean("isEnabled", false)
        return isEnabledNotification
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {

            startActivity(Intent(this, Continueas::class.java))
            finish()
        }
    }
}