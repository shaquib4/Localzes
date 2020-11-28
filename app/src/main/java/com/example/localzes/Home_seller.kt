package com.example.localzes

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home_seller.*

class Home_seller : AppCompatActivity() {
    private lateinit var ordersAccepted: TextView
    private lateinit var ordersOutForDelivery: TextView
    private lateinit var ordersCompleted: TextView
    private lateinit var ordersPending: TextView
    private lateinit var orderAuth: FirebaseAuth
    private lateinit var orderDatabaseReference: DatabaseReference
    private lateinit var editShopDetails: ImageView
    private lateinit var shopName: TextView
    private lateinit var totalOrders:TextView
    private lateinit var totalIncome:TextView
    private lateinit var storeViews:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_seller)
        ordersAccepted = findViewById(R.id.txtItemAccepted)
        ordersCompleted = findViewById(R.id.txtItemActive)
        ordersOutForDelivery = findViewById(R.id.txtItemOFD)
        ordersPending = findViewById(R.id.txtItemPending)
        editShopDetails = findViewById(R.id.editShopDetails)
        shopName = findViewById(R.id.txtShopName)
        totalOrders=findViewById(R.id.txtTO)
        totalIncome=findViewById(R.id.txtTI)
        storeViews=findViewById(R.id.txtSV)
        orderAuth = FirebaseAuth.getInstance()
        val user = orderAuth.currentUser
        val uid = user!!.uid

        bottom_navHome_seller.selectedItemId = R.id.nav_store_seller
        bottom_navHome_seller.setOnNavigationItemSelectedListener { item ->


            when (item.itemId) {


                R.id.nav_product_seller -> {

                    startActivity(Intent(this, Seller_Products::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_order_seller -> {

                    startActivity(Intent(this, OrdersSeller::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_account_seller -> {

                    startActivity(Intent(this, AccountsSeller::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_store_seller -> {

                   return@setOnNavigationItemSelectedListener true

                }
                R.id.nav_category_seller -> {

                    startActivity(Intent(this, Category::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
            }
            return@setOnNavigationItemSelectedListener false
        }
        orderDatabaseReference = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        orderDatabaseReference.child("Orders").orderByChild("orderStatus").equalTo("Pending")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    ordersPending.text = snapshot.childrenCount.toString()
                }
            })
        orderDatabaseReference.child("Orders").orderByChild("orderStatus").equalTo("Accepted")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    ordersAccepted.text = snapshot.childrenCount.toString()
                }
            })
        orderDatabaseReference.child("Orders").orderByChild("orderStatus")
            .equalTo("Out For Delivery")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    ordersOutForDelivery.text = snapshot.childrenCount.toString()
                }

            })
        orderDatabaseReference.child("Orders").orderByChild("orderStatus").equalTo("Completed")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    ordersCompleted.text = snapshot.childrenCount.toString()
                }

            })
        OrderAcc.setOnClickListener {
            val intent = Intent(this, OrdersAcceptedActivity::class.java)
            startActivity(intent)
        }
        ordersCompleted.setOnClickListener {
            val intent = Intent(this, OrdersCompletedActivity::class.java)
            startActivity(intent)
        }
        ordersPending.setOnClickListener {
            val intent = Intent(this, SellerOrdersActivity::class.java)
            startActivity(intent)
        }
        ordersOutForDelivery.setOnClickListener {
            val intent = Intent(this, OrderOutForDeliveryActivity::class.java)
            startActivity(intent)
        }
        editShopDetails.setOnClickListener {
            val intent = Intent(this, UpdateShopDetailActivity::class.java)
            startActivity(intent)
        }
        orderDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val shop = snapshot.child("shop_name").value.toString()
                shopName.text = shop
            }
        })
        orderDatabaseReference.child("Orders").addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val orders=snapshot.childrenCount.toString()
                totalOrders.text=orders
                for(i in snapshot.children){
                    
                }
            }

        })
    }
}