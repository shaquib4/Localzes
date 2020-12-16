package com.example.localzes

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_continueas.*
import util.ConnectionManager
import java.util.HashMap

class Continueas : AppCompatActivity() {
    var firebaseUser: FirebaseUser? = null
    private lateinit var userDatabase: DatabaseReference
    private lateinit var cuserDatabase: DatabaseReference
    private lateinit var suserDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_continueas)

        if (ConnectionManager().checkConnectivity(this)) {
            progress_continue.visibility = View.GONE
            btnCustomer.setOnClickListener {
                customer()
            }
            btnseller.setOnClickListener {
                seller()
            }
        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")
            dialog.setPositiveButton("Open Setting") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
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

    override fun onBackPressed() {
        finishAffinity()
    }
}