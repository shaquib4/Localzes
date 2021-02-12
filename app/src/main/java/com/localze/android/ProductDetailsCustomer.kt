package com.localze.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductDetailsCustomer : AppCompatActivity() {
    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var offerPrice: TextView
    private lateinit var originalPrice: TextView
    private lateinit var productStock: TextView
    private lateinit var productDescription: TextView
    private var productId: String? = "400"
    private var shopId: String? = "500"
    private lateinit var productAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details_customer)
        productImage = findViewById(R.id.imgP)
        productName = findViewById(R.id.txtItemName)
        offerPrice = findViewById(R.id.txtPrice)
        originalPrice = findViewById(R.id.txtMRP)
        productStock = findViewById(R.id.txtStock)
        productDescription = findViewById(R.id.txtDetails)
        productAuth = FirebaseAuth.getInstance()
        val user = productAuth.currentUser
        val uid = user!!.uid
        productId = intent.getStringExtra("productsId")
        shopId = intent.getStringExtra("shopsId")
        FirebaseDatabase.getInstance().reference.child("seller").child(shopId.toString())
            .child("Products").child(productId.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val imgUrl = snapshot.child("imageUrl").value.toString()
                    val pName = snapshot.child("title").value.toString()
                    val offPrice = snapshot.child("offerPrice").value.toString()
                    val orgPrice = snapshot.child("sellingPrice").value.toString()
                    val pStock = snapshot.child("stock").value.toString()
                    val pDesc =
                        snapshot.child("ProductDetails").child("description").value.toString()
                    productName.text = pName
                    offerPrice.text = offPrice
                    val spannableString = SpannableString(orgPrice)
                    val mStrikeThrough = StrikethroughSpan()
                    spannableString.setSpan(
                        mStrikeThrough,
                        0,
                        orgPrice.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    productStock.text = "STOCK:-${pStock}"
                    productDescription.text = pDesc
                    Glide.with(this@ProductDetailsCustomer).load(imgUrl).into(productImage)
                }

            })
    }

}