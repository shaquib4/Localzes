package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home_seller.*

class Home_seller : AppCompatActivity() {
    private lateinit var ordersAccepted:TextView
    private lateinit var ordersOutForDelivery:TextView
    private lateinit var ordersCompleted:TextView
    private lateinit var ordersPending:TextView
    private lateinit var orderAuth: FirebaseAuth
    private lateinit var orderDatabaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_seller)
        ordersAccepted=findViewById(R.id.txtItemAccepted)
        ordersCompleted=findViewById(R.id.txtItemActive)
        ordersOutForDelivery=findViewById(R.id.txtItemOFD)
        ordersPending=findViewById(R.id.txtItemPending)
        orderAuth=FirebaseAuth.getInstance()
        val user=orderAuth.currentUser
        val uid=user!!.uid

        bottom_navHome_seller.selectedItemId = R.id.nav_home
        bottom_navHome_seller.setOnNavigationItemSelectedListener { item ->


            when(item.itemId){


                R.id.nav_product_seller->{

                    startActivity(Intent(this,Seller_Products::class.java))
                    overridePendingTransition(0,0)
                    finish()

                }
            }
            return@setOnNavigationItemSelectedListener false
        }
        orderDatabaseReference=FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        orderDatabaseReference.child("Orders").orderByChild("orderStatus").equalTo("Pending").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                ordersPending.text=snapshot.childrenCount.toString()
            }
        })
        orderDatabaseReference.child("Orders").orderByChild("orderStatus").equalTo("Accepted").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                ordersAccepted.text=snapshot.childrenCount.toString()
            }
        })
        orderDatabaseReference.child("Orders").orderByChild("orderStatus").equalTo("Out For Delivery").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                ordersOutForDelivery.text=snapshot.childrenCount.toString()
            }

        })
        orderDatabaseReference.child("Orders").orderByChild("orderStatus").equalTo("Completed").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
               ordersCompleted.text=snapshot.childrenCount.toString()
            }

        })
        ordersAccepted.setOnClickListener {
            orderDatabaseReference.child("Orders").orderByChild("orderStatus").equalTo("Accepted").addValueEventListener(object :ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {

                }

            })
        }
        ordersCompleted.setOnClickListener {

        }
        ordersPending.setOnClickListener {

        }
        ordersOutForDelivery.setOnClickListener {

        }
    }
}