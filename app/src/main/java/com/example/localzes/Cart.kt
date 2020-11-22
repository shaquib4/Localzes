package com.example.localzes

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
    private var totalCost: Double = 0.0
    private var totalOriginalPrice: Double = 0.0
    private var totalItem: Int = 0
    private lateinit var txtPrice: TextView
    private lateinit var txtDiscountPrice: TextView
    private lateinit var txtDeliveryCharge: TextView
    private lateinit var txtTotalAmount: TextView
    private lateinit var totalPayment: TextView
    private lateinit var btnContinue: Button
    var discountAmount: Double = 0.00
    private lateinit var shopId:String
    private lateinit var orderByuid:String
    private lateinit var orderDetails: ModelOrderDetails
    private lateinit var progressDialog: ProgressDialog
    private lateinit var deliveryUser:String
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
        btnContinue = findViewById(R.id.btnContinue)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)
        totalCost = 0.0
        totalOriginalPrice = 0.0

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
                var finalPriceList= arrayListOf<Double>()
                var finalQuantityList= arrayListOf<Int>()
                var sellingPriceList= arrayListOf<Double>()
                (cartProducts as ArrayList<UserCartDetails>).clear()
                totalOriginalPrice=0.0
                totalCost=0.0

                for (i in snapshot.children) {
                    finalPriceList.clear()
                    sellingPriceList.clear()

                    val productId=i.child("productId").value.toString()
                    val orderBy = i.child("orderBy").value.toString()
                    val productTitle = i.child("productTitle").value.toString()
                    val priceEach = i.child("priceEach").value.toString()
                    val finalPrice = i.child("finalPrice").value.toString()
                    val finalQuantity = i.child("finalQuantity").value.toString()
                    val orderTo = i.child("orderTo").value.toString()
                    val productImageUrl = i.child("productImageUrl").value.toString()
                    val sellingPrice = i.child("sellingPrice").value.toString()
                    val finalsellingPrice=i.child("finalsellingPrice").value.toString()
                    val obj = UserCartDetails(
                        productId,
                        orderBy,
                        productTitle,
                        priceEach,
                        finalPrice,
                        finalQuantity,
                        orderTo,
                        productImageUrl,
                        sellingPrice,
                        finalsellingPrice
                    )
                    finalPriceList.add(finalPrice.toDouble())
                    sellingPriceList.add(finalsellingPrice.toDouble())
                    shopId= orderTo
                    orderByuid= orderBy
                    for (j in finalPriceList){
                        totalCost+=j
                    }
                    for (k in sellingPriceList){
                        totalOriginalPrice+=k
                    }

                    (cartProducts as ArrayList<UserCartDetails>).add(obj)

                }

                userCartAdapter = AdapterCartItem(this@Cart, cartProducts)
                recyclerCartProduct.adapter = userCartAdapter
                totalItem=snapshot.childrenCount.toInt()
                totalItems.text = "Total Item :- ${snapshot.childrenCount}"
                txtPrice.text = "Rs. ${totalOriginalPrice}"
                discountAmount =
                    ((totalOriginalPrice.toString()).toDouble() - (totalCost.toString().toDouble()))
                txtDiscountPrice.text = "Rs. ${discountAmount}"
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
        val reference:DatabaseReference=FirebaseDatabase.getInstance().reference.child("users").child(uid).child("current_address")
        reference.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                deliveryUser=snapshot.child("address").value.toString()
            }

        })
        btnContinue.setOnClickListener {
            val intent=Intent(this,PaymentActivity::class.java)
            intent.putExtra("shopId",shopId)
            intent.putExtra("totalCost",totalCost)
            intent.putExtra("orderBy",orderByuid)
            intent.putExtra("totalItem",totalItem.toString())
            intent.putExtra("delivery",deliveryUser)
            startActivity(intent)
            finish()
        }
    }
}