package com.localzes.android

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.localzes.android.Adapters.AdapterStaffOf
import com.localzes.android.Modals.ModelStaffOf
import kotlinx.android.synthetic.main.activity_continueas.*
import util.ConnectionManager

class Continueas : AppCompatActivity() {
    var firebaseUser: FirebaseUser? = null
    private lateinit var userDatabase: DatabaseReference
    private lateinit var cuserDatabase: DatabaseReference
    private lateinit var suserDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var adapterStaffOf: AdapterStaffOf
    private lateinit var staffOf: List<ModelStaffOf>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_continueas)
        progress_continue.visibility = View.GONE
        staffOf = ArrayList<ModelStaffOf>()
        retryContinueAs.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)) {
                rl_continueAs.visibility = View.VISIBLE
                rl_retryContinueAs.visibility = View.GONE
                this.recreate()
            } else {
                rl_continueAs.visibility = View.GONE
                rl_retryContinueAs.visibility = View.VISIBLE
            }
        }



        btnCustomer.setOnClickListener {
            progress_continue.visibility = View.VISIBLE
            if (ConnectionManager().checkConnectivity(this)) {
                rl_continueAs.visibility = View.VISIBLE
                rl_retryContinueAs.visibility = View.GONE
                customer()
            } else {
                rl_continueAs.visibility = View.GONE
                rl_retryContinueAs.visibility = View.VISIBLE
            }
        }
        btnseller.setOnClickListener {
            progress_continue.visibility = View.VISIBLE
            if (ConnectionManager().checkConnectivity(this)) {
                rl_continueAs.visibility = View.VISIBLE
                rl_retryContinueAs.visibility = View.GONE
                seller()
            } else {
                rl_continueAs.visibility = View.GONE
                rl_retryContinueAs.visibility = View.VISIBLE
            }
        }


    }


    private fun seller() {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        userDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(firebaseUser!!.uid)
        userDatabase!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("staff").exists()) {
                        userDatabase.child("staff")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {

                                }

                                override fun onDataChange(snapshots: DataSnapshot) {
                                    (staffOf as ArrayList<ModelStaffOf>).clear()
                                    for (i in snapshots.children) {
                                        val obj = ModelStaffOf(
                                            i.child("shopOwnerName").value.toString(),
                                            i.child("shopName").value.toString(),
                                            i.child("shopMobileNumber").value.toString(),
                                            i.child("status").value.toString(),
                                            i.child("invitationUid").value.toString(),
                                            i.child("invitationStatus").value.toString()
                                        )
                                        if (i.child("invitationStatus").value.toString() == "Accepted") {
                                            (staffOf as ArrayList<ModelStaffOf>).add(obj)
                                        }

                                    }
                                    if (staffOf.isEmpty()) {
                                        progress_continue.visibility = View.GONE
                                        startActivity(
                                            Intent(
                                                this@Continueas,
                                                Home_seller::class.java
                                            )
                                        )
                                        finish()
                                    } else {
                                        val builder = AlertDialog.Builder(this@Continueas)
                                        val view = LayoutInflater.from(this@Continueas)
                                            .inflate(R.layout.main_seller, null, false)
                                        val rv: RecyclerView =
                                            view.findViewById(R.id.recycler_staff_of)
                                        val layoutManager = LinearLayoutManager(this@Continueas)
                                        rv.layoutManager = layoutManager
                                        adapterStaffOf = AdapterStaffOf(this@Continueas, staffOf)
                                        rv.adapter = adapterStaffOf
                                        builder.create().show()
                                    }
                                }
                            })
                    } else {
                        progress_continue.visibility = View.GONE
                        startActivity(Intent(this@Continueas, Home_seller::class.java))
                        finish()
                    }
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