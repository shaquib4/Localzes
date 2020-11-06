package com.example.localzes

import android.accounts.Account
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_cart.*

class Cart : AppCompatActivity() {
    private lateinit var cartDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerCartProduct: RecyclerView
    private lateinit var userCartAdapter: AdapterCartItem
    private lateinit var cartProducts: List<UserCartDetails>
    private lateinit var totalItems: TextView
    private lateinit var txtTotalPrice: TextView
    private var totalCost: String? = "100"
    private var totalOriginalPrice: String? = "200"
    private var totalItem: String? = "300"
    private lateinit var txtPrice: TextView
    private lateinit var txtDiscountPrice: TextView
    private lateinit var txtDeliveryCharge: TextView
    private lateinit var txtTotalAmount: TextView
    private lateinit var totalPayment: TextView
    var discountAmount: Double = 0.00
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart1)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        recyclerCartProduct = findViewById(R.id.cart_recycler_view)
        txtTotalPrice = findViewById(R.id.txtTotalPrice)
        txtPrice = findViewById(R.id.txtPrice)
        txtDeliveryCharge = findViewById(R.id.txtDeliveryCharge)
        txtDiscountPrice = findViewById(R.id.txtDiscountPrice)
        txtTotalAmount = findViewById(R.id.txtTotalAmount)
        totalPayment = findViewById(R.id.TotalPayment)
        totalCost = intent.getStringExtra("totalCost")
        totalOriginalPrice = intent.getStringExtra("totalOriginalPrice")
        totalItem = intent.getStringExtra("totalItems")
        recyclerCartProduct.layoutManager = LinearLayoutManager(this)
        cartProducts = ArrayList<UserCartDetails>()
        totalItems = findViewById(R.id.txtTotalItems)
        cartDatabase =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("Cart")

        bottom_navCart.selectedItemId = R.id.nav_cart
        bottom_navCart.setOnNavigationItemSelectedListener { item ->


            when (item.itemId) {

                R.id.nav_home -> {
                    startActivity(Intent(this, Home::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_search -> {

                    startActivity(Intent(this, Search::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_cart -> {

                    return@setOnNavigationItemSelectedListener true

                }
                R.id.nav_account -> {

                    startActivity(Intent(this, Accounts::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
            }
            return@setOnNavigationItemSelectedListener true
        }
        cartDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    val obj = UserCartDetails(
                        i.child("productId").value.toString(),
                        i.child("orderBy").value.toString(),
                        i.child("productTitle").value.toString(),
                        i.child("priceEach").value.toString(),
                        i.child("finalPrice").value.toString(),
                        i.child("finalQuantity").value.toString(),
                        i.child("orderTo").value.toString(),
                        i.child("productImageUrl").value.toString(),
                        i.child("sellingPrice").value.toString()
                    )
                    (cartProducts as ArrayList<UserCartDetails>).add(obj)
                }
                userCartAdapter = AdapterCartItem(this@Cart, cartProducts)
                recyclerCartProduct.adapter = userCartAdapter
                totalItems.text = "Total Item :- ${snapshot.childrenCount}"
                txtPrice.text = "Rs. ${totalCost}"
                discountAmount =
                    ((totalOriginalPrice.toString()).toDouble() - (totalCost.toString().toDouble()))
                txtDiscountPrice.text = "-Rs. ${discountAmount}"
                if (snapshot.childrenCount > 1) {
                    txtTotalPrice.text = "Price(${snapshot.childrenCount} items)"
                } else {
                    txtTotalPrice.text = "Price(${snapshot.childrenCount} item)"
                }
                val amount = totalCost.toString()
                txtTotalAmount.text = "Rs. ${amount}"
                totalPayment.text = "Rs. ${amount}"
            }
        })
    }
}