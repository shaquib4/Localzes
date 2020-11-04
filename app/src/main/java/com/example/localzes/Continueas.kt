package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_continueas.*
import java.util.HashMap

class Continueas : AppCompatActivity() {
    var firebaseUser: FirebaseUser?=null
    private lateinit var userDatabase: DatabaseReference
    private lateinit var cuserDatabase: DatabaseReference
    private lateinit var suserDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_continueas)
        progress_continue.visibility= View.GONE
        btnCustomer.setOnClickListener {
            progress_continue.visibility= View.VISIBLE
            customer()
        }
        btnseller.setOnClickListener {
            progress_continue.visibility= View.VISIBLE
            seller()
        }
    }
    private fun seller() {
        firebaseUser= FirebaseAuth.getInstance().currentUser
        userDatabase= FirebaseDatabase.getInstance().reference.child("seller").child(firebaseUser!!.uid)
        userDatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    progress_continue.visibility=View.GONE
                    startActivity(Intent(this@Continueas,Home_seller::class.java))
                    finish()
                }else{
                    progress_continue.visibility=View.GONE
                    val phone=intent.getStringExtra("phone1")
                    val intent=Intent(this@Continueas,Registerdetails_seller::class.java)
                    startActivity(intent)
                    finish()



                }

            }

            override fun onCancelled(error: DatabaseError) {

            }



        })
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
                    startActivity(intent)
                    finish()


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }



        })

    }
}