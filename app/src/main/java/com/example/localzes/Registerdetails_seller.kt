package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_registerdetails_seller.*

class Registerdetails_seller : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var firebaseUser: FirebaseUser?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registerdetails_seller)
        auth= FirebaseAuth.getInstance()
        btnRegSeller.setOnClickListener {
            register()


        }
    }

    private fun register() {
        val name=edtregNameSeller.text.toString()
        val email=edtregemailSeller.text.toString()



        if(edtregNameSeller.text.toString().isEmpty()){
            edtregNameSeller.error="please enter password"
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(edtregemailSeller.text.toString()).matches()){
            edtregemailSeller.error="Please enter valid email"
            edtregemailSeller.requestFocus()
            return
        }
        val  intent= Intent(this,MapsActivity2::class.java)
        intent.putExtra("name",name)
        intent.putExtra("email",email)
        startActivity(intent)
        finish()

    }



}