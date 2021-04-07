package com.localze.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class IncomeStatus : AppCompatActivity() {
    private lateinit var cashOnDelivery: Button
    private lateinit var paytm: Button
    private lateinit var razorpay: Button
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income_status)
        cashOnDelivery = findViewById(R.id.btnCOD)
        paytm = findViewById(R.id.btnPaytm)
        razorpay = findViewById(R.id.btnRazorpay)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        cashOnDelivery.setOnClickListener {
            showCODOrders(uid)
        }
        paytm.setOnClickListener {
            showPaytmOrders(uid)

        }
        razorpay.setOnClickListener {
            showRazorpayOrders(uid)

        }
    }

    private fun showRazorpayOrders(uid: String) {

    }

    private fun showPaytmOrders(uid: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        databaseReference.child("Orders").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    if (i.child("paymentMode").value.toString() == "Paid(Pay with Paytm)"){

                    }
                }
            }

        })
        databaseReference.child("OrdersLists").addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children){
                    if (i.child("paymentMode").value.toString()=="Paid(Pay with Paytm)"){

                    }
                }
            }

        })

    }

    private fun showCODOrders(uid: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        databaseReference.child("Orders").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    if (i.child("paymentMode").value.toString() == "Unpaid(Cash on Delivery)") {

                    }
                }
            }

        })
        databaseReference.child("OrdersLists").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    if (i.child("paymentMode").value.toString() == "Unpaid(Cash on Delivery)") {

                    }
                }
            }

        })
    }
}