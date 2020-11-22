package com.example.localzes

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_continueas.*
import java.util.HashMap

class Continueas : AppCompatActivity() {
    var firebaseUser: FirebaseUser? = null
    private lateinit var userDatabase: DatabaseReference
    private lateinit var cuserDatabase: DatabaseReference
    private lateinit var suserDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var spEditor: SharedPreferences.Editor
    private var isChecked:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_continueas)
        sharedPreferences=getSharedPreferences("SETTINGS_SP", Context.MODE_PRIVATE)
        isChecked=sharedPreferences.getBoolean("FCM_ENABLED",false)
        progress_continue.visibility = View.GONE
        btnCustomer.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Request Permission")
            builder.setMessage("Click Allow to get notifications about your orders or Deny")
            builder.setPositiveButton("ALLOW") { text, listener ->
                subscribeToTopic()
            }
            builder.setNegativeButton("DENY") { text, listener ->

            }
            builder.create().show()
            progress_continue.visibility = View.VISIBLE
            customer()
        }
        btnseller.setOnClickListener {
            val builder=AlertDialog.Builder(this)
            builder.setTitle("Request Permission")
            builder.setMessage("Click Allow to get information about your orders")
            builder.setPositiveButton("ALLOW"){text,listener->
                subscribeToTopic()
            }
            builder.setNegativeButton("DENY"){text,listener->

            }
            builder.create().show()
            progress_continue.visibility = View.VISIBLE
            seller()
        }
    }

    private fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("PUSH_NOTIFICATIONS")
            .addOnCompleteListener {task->
                var msg="Notifications Are Enabled"
                if (!task.isSuccessful) {
                    msg  ="Notifications are not enabled"
                }
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
    }

    private fun seller() {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        userDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(firebaseUser!!.uid)
        userDatabase!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    progress_continue.visibility = View.GONE
                    startActivity(Intent(this@Continueas, Home_seller::class.java))
                    finish()
                } else {
                    progress_continue.visibility = View.GONE
                    val phone = intent.getStringExtra("phone1")
                    val intent = Intent(this@Continueas, Registerdetails_seller::class.java)
                    startActivity(intent)
                    finish()


                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }

    private fun customer() {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        userDatabase =
            FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)
        userDatabase!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    progress_continue.visibility = View.GONE
                    startActivity(Intent(this@Continueas, Home::class.java))
                    finish()
                } else {
                    progress_continue.visibility = View.GONE
                    val phone = intent.getStringExtra("phone1")
                    val intent = Intent(this@Continueas, Registerdetails::class.java)
                    startActivity(intent)
                    finish()


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

    }
}