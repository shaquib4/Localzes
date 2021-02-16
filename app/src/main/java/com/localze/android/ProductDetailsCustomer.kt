package com.localze.android

import android.app.AlertDialog
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
    private var shopID: String? = null
    private var imgUrls: String? = null
    private var productNam: String? = null
    private var unit: String? = null
    private var quan: String? = null
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
                    imgUrls = imgUrl
                    val pName = snapshot.child("title").value.toString()
                    productNam = pName
                    val offPrice = snapshot.child("offerPrice").value.toString()
                    productCost = offPrice.toDouble()
                    val uni = snapshot.child("unit").value.toString()
                    unit = uni.toString()
                    val quantity = snapshot.child("quantity").value.toString()
                    quan = quantity
                    val orgPrice = snapshot.child("sellingPrice").value.toString()
                    productOriginal = orgPrice.toDouble()
                    val pStock = snapshot.child("stock").value.toString()
                    if (snapshot.child("ProductDetails").exists()) {
                        val pDesc =
                            snapshot.child("ProductDetails").child("description").value.toString()
                        val refundT =
                            snapshot.child("ProductDetails")
                                .child("refundableType").value.toString()
                        refundType.text = "This product is $refundT"
                        val colorT =
                            snapshot.child("ProductDetails").child("colors").value.toString()
                        val sizes = snapshot.child("ProductDetails").child("sizes").value.toString()
                        availableSize.text = sizes
                        colorsVariant.text = colorT
                        productDescription.text = pDesc
                    } else {
                        refundType.text = ""
                        availableSize.text = ""
                        colorsVariant.text = ""
                        productDescription.text = ""
                    }
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
                    originalPrice.text = spannableString
                    productStock.text = "STOCK:-${pStock}"

                    Glide.with(this@ProductDetailsCustomer).load(imgUrl).into(productImage)
                }

            })
        finalSellingPrice = productCost
        finalProductOriginal = productOriginal
        val userDatabase =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("Cart")
        userDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    val shopId = i.child("orderTo").value.toString()

                    shopID = shopId
                }
            }
        })

        userDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userDatabase.child(productId.toString())
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    var finalQDB = snapshot.child("finalQuantity").value.toString()
                                    val finalPDB = snapshot.child("finalPrice").value.toString()
                                    val finalSPDB =
                                        snapshot.child("finalsellingPrice").value.toString()
                                    val sPDB = snapshot.child("sellingPrice").value.toString()
                                    val cPDB = snapshot.child("priceEach").value.toString()
                                    addToCart.visibility = View.GONE
                                    btnLinear.visibility = View.VISIBLE
                                    txtCount.text = finalQDB
                                    if (shopId.toString() == shopID.toString()) {
                                        addToCart1(
                                            productId.toString(),
                                            uid.toString(),
                                            productNam.toString(),
                                            cPDB.toDouble(),
                                            finalPDB.toDouble(),
                                            finalQDB.toInt(),
                                            shopId.toString(),
                                            imgUrls.toString(),
                                            sPDB.toDouble(),
                                            unit.toString(),
                                            quan.toString(),
                                            finalSPDB.toDouble()
                                        )
                                    }
                                } else if (!snapshot.exists()) {
                                    if (shopId.toString() == shopID.toString()) {
                                        addToCart.setOnClickListener {
                                            addToCarts(
                                                productId.toString(),
                                                uid.toString(),
                                                productNam.toString(),
                                                productCost,
                                                finalProductOriginal,
                                                1,
                                                shopId.toString(),
                                                imgUrls.toString(),
                                                productOriginal,
                                                unit.toString(),
                                                quan.toString(),
                                                finalSellingPrice
                                            )
                                        }
                                    } else if (shopId.toString() !== shopID.toString()) {
                                        val builder =
                                            AlertDialog.Builder(this@ProductDetailsCustomer)
                                        val view = LayoutInflater.from(this@ProductDetailsCustomer)
                                            .inflate(R.layout.custom_layout, null)
                                        builder.setView(view)
                                        val show = builder.show()
                                        val confirm: Button = view.findViewById(R.id.btnConfirm)
                                        val cancel: Button = view.findViewById(R.id.btnCancel)
                                        confirm.setOnClickListener {
                                            val userDatabases =
                                                FirebaseDatabase.getInstance().reference.child("users")
                                                    .child(uid).child("Cart")
                                            userDatabases.removeValue()
                                            addToCart.setOnClickListener {
                                                addToCarts(
                                                    productId.toString(),
                                                    uid.toString(),
                                                    productNam.toString(),
                                                    productCost,
                                                    finalProductOriginal,
                                                    1,
                                                    shopId.toString(),
                                                    imgUrls.toString(),
                                                    productOriginal,
                                                    unit.toString(),
                                                    quan.toString(),
                                                    finalSellingPrice
                                                )
                                            }
                                            show.dismiss()
                                        }
                                        cancel.setOnClickListener {
                                            show.dismiss()
                                        }
                                    }
                                }
                            }
                        })
                } else if (!snapshot.exists()) {
                    addToCart.setOnClickListener {
                        addToCarts(
                            productId.toString(),
                            uid.toString(),
                            productNam.toString(),
                            productCost,
                            finalProductOriginal,
                            1,
                            shopId.toString(),
                            imgUrls.toString(),
                            productOriginal,
                            unit.toString(),
                            quan.toString(),
                            finalSellingPrice
                        )
                    }
                }
            }
        })


    }

    private fun addToCarts(
        productId: String,
        uid: String,
        title: String,
        priceEach: Double,
        finalCost: Double,
        quantity: Int,
        shopId: String,
        productUrl: String,
        sellingPrice: Double,
        unit: String
        ,
        quantity1: String,
        finalSellingPrice: Double
    ) {

        val cartMap = HashMap<String, Any>()
        cartMap["productId"] = productId
        cartMap["orderBy"] = uid
        cartMap["productTitle"] = title
        cartMap["priceEach"] = priceEach.toString()
        cartMap["finalPrice"] = productCost.toString()
        cartMap["finalQuantity"] = quantity.toString()
        cartMap["orderTo"] = shopId
        cartMap["productImageUrl"] = productUrl
        cartMap["sellingPrice"] = sellingPrice.toString()
        cartMap["finalsellingPrice"] = productOriginal.toString()
        cartMap["unit"] = unit
        cartMap["originalQuantity"] = quantity1
        val cartDetails =
            FirebaseDatabase.getInstance().reference.child("users").child(uid)
                .child("Cart")
        cartDetails.child(productId).updateChildren(cartMap).addOnCompleteListener {
            if (it.isSuccessful) {
                addToCart.visibility = View.GONE
                btnLinear.visibility = View.VISIBLE
            }
        }

    }

    private fun addToCart1(
        productId: String,
        uid: String,
        title: String,
        priceEach: Double,
        finalCost: Double,
        quantity: Int,
        shopId: String,
        productUrl: String,
        sellingPrice: Double,
        unit: String,
        quantity1: String,
        finalSellingPrice: Double
    ) {
        var finalSellingPriceP = finalSellingPrice
        var finalCosts = finalCost
        var quan = quantity

        btnIncrease.setOnClickListener {
            finalSellingPriceP += sellingPrice
            finalCosts += priceEach
            quan++
            txtCount.text = quan.toString()
            val cartMap = HashMap<String, Any>()
            cartMap["productId"] = productId
            cartMap["orderBy"] = uid
            cartMap["productTitle"] = title
            cartMap["priceEach"] = priceEach.toString()
            cartMap["finalPrice"] = finalCosts.toString()
            cartMap["finalQuantity"] = quan.toString()
            cartMap["orderTo"] = shopId
            cartMap["productImageUrl"] = productUrl
            cartMap["sellingPrice"] = sellingPrice.toString()
            cartMap["finalsellingPrice"] = finalSellingPriceP.toString()
            cartMap["unit"] = unit
            cartMap["originalQuantity"] = quantity1
            val cartDetails =
                FirebaseDatabase.getInstance().reference.child("users").child(uid)
                    .child("Cart")
            cartDetails.child(productId).updateChildren(cartMap)
        }
        btnDecrease.setOnClickListener {
            var q = quan.toString()
            if (quan > 1) {
                finalSellingPriceP -= sellingPrice
                finalCosts -= priceEach
                quan--
                txtCount.text = quan.toString()
                val cartMap = HashMap<String, Any>()
                cartMap["productId"] = productId
                cartMap["orderBy"] = uid
                cartMap["productTitle"] = title
                cartMap["priceEach"] = priceEach.toString()
                cartMap["finalPrice"] = finalCosts.toString()
                cartMap["finalQuantity"] = quan.toString()
                cartMap["orderTo"] = shopId
                cartMap["productImageUrl"] = productUrl
                cartMap["sellingPrice"] = sellingPrice.toString()
                cartMap["finalsellingPrice"] = finalSellingPriceP.toString()
                cartMap["unit"] = unit
                cartMap["originalQuantity"] = quantity1
                val cartDetails =
                    FirebaseDatabase.getInstance().reference.child("users").child(uid)
                        .child("Cart")
                cartDetails.child(productId).updateChildren(cartMap)
            } else if (q <= 1.toString()) {
                val cartDetails =
                    FirebaseDatabase.getInstance().reference.child("users").child(uid)
                        .child("Cart")
                cartDetails.child(productId).removeValue().addOnCompleteListener {
                    if (it.isSuccessful) {
                        btnLinear.visibility = View.GONE
                        addToCart.visibility = View.VISIBLE

                    }
                }
            }
        }

    }


}