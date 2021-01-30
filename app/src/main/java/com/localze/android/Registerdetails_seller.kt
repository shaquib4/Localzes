package com.localze.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_registerdetails_seller.*
import util.ConnectionManager

class Registerdetails_seller : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var firebaseUser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registerdetails_seller)
        auth = FirebaseAuth.getInstance()
        retryRegisterSeller.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)){
                rl_retryRegisterSeller.visibility= View.VISIBLE
                rl_RegisterSeller.visibility=View.GONE
                this.recreate()
            }else{
                rl_retryRegisterSeller.visibility= View.GONE
                rl_RegisterSeller.visibility=View.VISIBLE
            }
        }
        btnRegSeller.setOnClickListener {

            if (ConnectionManager().checkConnectivity(this)){
                rl_retryRegisterSeller.visibility= View.VISIBLE
                rl_RegisterSeller.visibility=View.GONE
                register()
            }else{
                 rl_retryRegisterSeller.visibility= View.GONE
                rl_RegisterSeller.visibility=View.VISIBLE            }


        }
    }

    private fun register() {
        val name = edtregNameSeller.text.trim().toString()
        val email = edtregemailSeller.text.trim().toString()



        if (edtregNameSeller.text.trim().toString().isEmpty()) {
            edtregNameSeller.error = "please enter password"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(edtregemailSeller.text.trim().toString()).matches()) {
            edtregemailSeller.error = "Please enter valid email"
            edtregemailSeller.requestFocus()
            return
        }
        val intent = Intent(this, MapsActivity2::class.java)
        intent.putExtra("name", name)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()

    }

    override fun onBackPressed() {
        finishAffinity()
    }


}