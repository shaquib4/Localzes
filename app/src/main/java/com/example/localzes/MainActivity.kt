package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        btnRegButton.setOnClickListener {
            register()
        }

    }
    private fun register() {
        val phone=edtmobile.text.toString()
        if (edtmobile.text.toString().isEmpty()) {
            edtmobile.error = "please enter your phone number"
            return


        }
        val intent= Intent(this,Verify::class.java)
        intent.putExtra("phone",phone)
        startActivity(intent)
        finish()

    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    private fun updateUI(currentUser : FirebaseUser?){
        if (currentUser != null){

            startActivity(Intent(this,Continueas::class.java))
            finish()
        }
    }
}