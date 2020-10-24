package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_continueas.*

class Continueas : AppCompatActivity() {
    var firebaseUser: FirebaseUser?=null
    private lateinit var userDatabase: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_continueas)
        progress_continue.visibility= View.GONE
        btnCustomer.setOnClickListener {
            progress_continue.visibility= View.VISIBLE
            customer()
        }
    }

    private fun customer() {
        firebaseUser= FirebaseAuth.getInstance().currentUser
        userDatabase= FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)
        userDatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    progress_continue.visibility= View.GONE
                    startActivity(Intent(this@Continueas,Home::class.java))
                    finish()
                }else{
                    progress_continue.visibility= View.GONE
                    val phone=intent.getStringExtra("phone1")
                    val intent= Intent(this@Continueas,Registerdetails::class.java)
                    intent.putExtra("phone2",phone)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }



        })

    }
}