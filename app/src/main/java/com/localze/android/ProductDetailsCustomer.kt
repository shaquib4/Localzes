package com.localze.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
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
    private lateinit var refundType: TextView
    private lateinit var availableSize: TextView
    private lateinit var colorsVariant: TextView
    private lateinit var addToCart: Button
    private lateinit var btnLinear: LinearLayout
    private lateinit var btnDecrease: Button
    private lateinit var btnIncrease: Button
    private lateinit var txtCount: TextView
    private var productCost: Double = 0.0
    private var finalSellingPrice: Double = 0.0
    private var productOriginal: Double = 0.0
    private var finalProductOriginal: Double = 0.0
    private var quantity: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details_customer)
        productImage = findViewById(R.id.imgP)
        productName = findViewById(R.id.txtItemName)
        offerPrice = findViewById(R.id.txtPrice)
        originalPrice = findViewById(R.id.txtMRP)
        productStock = findViewById(R.id.txtStock)
        productDescription = findViewById(R.id.txtDetails)
        refundType = findViewById(R.id.txtRefund)
        availableSize = findViewById(R.id.txtSize)
        colorsVariant = findViewById(R.id.txtColor)
        addToCart = findViewById(R.id.btnCartAdd)
        btnLinear = findViewById(R.id.btnLinearDetail)
        btnDecrease = findViewById(R.id.btnDecrease_newDetail)
        btnIncrease = findViewById(R.id.btnIncrease_newDetail)
        txtCount = findViewById(R.id.txtCounterDetail)
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
                    productCost = offPrice.toDouble()
                    val orgPrice = snapshot.child("sellingPrice").value.toString()
                    productOriginal = orgPrice.toDouble()
                    val pStock = snapshot.child("stock").value.toString()
                    val pDesc =
                        snapshot.child("ProductDetails").child("description").value.toString()
                    val refundT =
                        snapshot.child("ProductDetails").child("refundableType").value.toString()
                    refundType.text = "This product is $refundT"
                    val colorT = snapshot.child("ProductDetails").child("colors").value.toString()
                    val sizes=snapshot.child("ProductDetails").child("sizes").value.toString()
                    availableSize.text=sizes
                    colorsVariant.text = colorT
                    productName.text = pName
                    offerPrice.text = "₹$offPrice"
                    val spannableString = SpannableString("₹$orgPrice")
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
        finalSellingPrice = productCost
        finalProductOriginal = productOriginal
        addToCart.setOnClickListener {

        }
        btnDecrease.setOnClickListener {
            if (quantity > 1) {
                finalSellingPrice -= productCost
                finalProductOriginal -= productOriginal
                quantity--
                txtCount.text = quantity.toString()
            }
        }
        btnIncrease.setOnClickListener {
            finalSellingPrice += productCost
            finalProductOriginal += productOriginal
            quantity++
            txtCount.text = quantity.toString()
        }
    }

}