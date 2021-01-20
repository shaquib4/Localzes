package com.localzes.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_registerdetails.*
import util.ConnectionManager

class Registerdetails : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    var firebaseUser: FirebaseUser?=null
    private lateinit var userDatabase: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registerdetails)
        auth= FirebaseAuth.getInstance()

        retryRegister.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)){
                rl_Register.visibility= View.VISIBLE
                rl_retryRegister.visibility=View.GONE
                this.recreate()
            }else{
                rl_Register.visibility= View.GONE
                rl_retryRegister.visibility=View.VISIBLE
            }
        }
        btnReg.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)){
                rl_Register.visibility= View.VISIBLE
                rl_retryRegister.visibility=View.GONE
            register()}else{
                rl_Register.visibility= View.GONE
                rl_retryRegister.visibility=View.VISIBLE
            }


        }
    }



    fun register(){


        val name=edtregName.text.trim().toString()
        val email=edtregemail.text.trim().toString()
        val phone=intent.getStringExtra("phone2")


        if(edtregName.text.trim().toString().isEmpty()){
            edtregName.error="please enter password"
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(edtregemail.text.trim().toString()).matches()){
            edtregemail.error="Please enter valid email"
            edtregemail.requestFocus()
            return
        }



        val  intent= Intent(this,MapsActivity::class.java)
        intent.putExtra("name",name)
        intent.putExtra("phone3",phone)
        intent.putExtra("email",email)
        startActivity(intent)
        finish()


    }

    override fun onBackPressed() {
        finishAffinity()
    }

}

