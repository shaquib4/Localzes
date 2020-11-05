package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserProductsActivity : AppCompatActivity() {
    private var shopId: String? = "100"
    private lateinit var mUserProductDatabase: DatabaseReference
    private lateinit var mUserProducts: List<ModelAddProduct>
    private lateinit var recyclerUserProduct: RecyclerView
    private lateinit var userProductAdapter: AdapterUserProducts
    private lateinit var cartRelativeLayout: RelativeLayout
    private lateinit var cartDatabaseReference: DatabaseReference
    private lateinit var quantityItem: TextView
    private lateinit var costTotal: TextView
    private lateinit var cartItems: List<UserCartDetails>
    private lateinit var auth: FirebaseAuth
    private lateinit var viewCart: TextView
    var totalCost: Double = 0.00
    var totalOriginalPrice: Double = 0.00
    var totalItems:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_products)
        shopId = intent.getStringExtra("shopId")
        mUserProducts = ArrayList<ModelAddProduct>()
        cartRelativeLayout = findViewById(R.id.rl_cart_details)
        cartRelativeLayout.visibility = View.GONE
        quantityItem = findViewById(R.id.quantity_item)
        costTotal = findViewById(R.id.total_cost_cart)
        cartItems = ArrayList<UserCartDetails>()
        viewCart = findViewById(R.id.txtViewCart)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        recyclerUserProduct = findViewById(R.id.recycler_shop_user_products)
        recyclerUserProduct.layoutManager = LinearLayoutManager(this)
        cartDatabaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("Cart")
        mUserProductDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(shopId.toString())
        mUserProductDatabase.child("Products").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (mUserProducts as ArrayList<ModelAddProduct>).clear()
                for (i in snapshot.children) {
                    val obj = ModelAddProduct(
                        i.child("shopId").value.toString(),
                        i.child("productId").value.toString(),
                        i.child("imageUrl").value.toString(),
                        i.child("productCategory").value.toString(),
                        i.child("title").value.toString(),
                        i.child("description").value.toString(),
                        i.child("sellingPrice").value.toString(),
                        i.child("offerPrice").value.toString(),
                        i.child("unit").value.toString(),
                        i.child("quantity").value.toString()
                    )
                    (mUserProducts as ArrayList<ModelAddProduct>).add(obj)
                }
                userProductAdapter = AdapterUserProducts(this@UserProductsActivity, mUserProducts)
                recyclerUserProduct.adapter = userProductAdapter
            }

        })
        cartDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    val orderBy = i.child("orderBy").value.toString()
                    val productTitle = i.child("productTitle").value.toString()
                    val priceEach = i.child("priceEach").value.toString()
                    val finalPrice = i.child("finalPrice").value.toString()
                    val finalQuantity = i.child("finalQuantity").value.toString()
                    val orderTo = i.child("orderTo").value.toString()
                    val productImageUrl = i.child("productImageUrl").value.toString()
                    val sellingPrice = i.child("sellingPrice").value.toString()
                    val obj = UserCartDetails(
                        orderBy,
                        productTitle,
                        priceEach,
                        finalPrice,
                        finalQuantity,
                        orderTo,
                        productImageUrl,
                        sellingPrice
                    )
                    (cartItems as ArrayList<UserCartDetails>).add(obj)
                    totalCost += priceEach.toDouble()
                    totalOriginalPrice += sellingPrice.toDouble()
                }
                if (cartItems.isNotEmpty()) {
                    cartRelativeLayout.visibility = View.VISIBLE
                    totalItems=snapshot.childrenCount.toInt()
                    quantityItem.text = totalItems.toString()
                    costTotal.text = totalCost.toString()
                }
            }
        })
        viewCart.setOnClickListener {
            val intent = Intent(this, Cart::class.java)
            intent.putExtra("totalCost",totalCost)
            intent.putExtra("totalOriginalPrice",totalOriginalPrice)
            intent.putExtra("totalItems",totalItems)
            startActivity(intent)
        }
    }
}