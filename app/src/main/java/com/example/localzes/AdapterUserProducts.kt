package com.example.localzes

import android.app.AlertDialog
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class AdapterUserProducts(val context: Context, private val products_user: List<ModelAddProduct>) :
    RecyclerView.Adapter<AdapterUserProducts.HolderUserProducts>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderUserProducts {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_single_row_customer, parent, false)
        return HolderUserProducts(view)
    }

    override fun getItemCount(): Int {
        return products_user.size
    }

    override fun onBindViewHolder(holder: HolderUserProducts, position: Int) {
        val userProducts = products_user[position]
        Picasso.get().load(userProducts.imageUrl).into(holder.productImage)
        holder.productTitle.text = userProducts.title
        holder.productPrice.text = "Rs. ${userProducts.sellingPrice}"
        val mString = "Rs. ${userProducts.offerPrice}"
        val spannableString = SpannableString(mString)
        val mStrikeThrough = StrikethroughSpan()
        spannableString.setSpan(mStrikeThrough, 0, mString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.productOfferPrice.text = spannableString
        holder.addItem.setOnClickListener {
            showQuantityDialog(userProducts)

        }
    }

    private var cost: Double = 0.0
    private var finalCost: Double = 0.0
    private var quantity: Int = 0

    private fun showQuantityDialog(userProducts: ModelAddProduct) {
        val view = LayoutInflater.from(context).inflate(R.layout.add_to_cart, null)
        val productName: TextView = view.findViewById(R.id.ProductName)
        val originalPriceEach: TextView = view.findViewById(R.id.ProductPrice)
        val originalOfferPriceEach: TextView = view.findViewById(R.id.ProductOfferPrice)
        val productImage: ImageView = view.findViewById(R.id.ProductImage)
        val finalPrice: TextView = view.findViewById(R.id.ProductFinalPrice)
        val btnIncrease: Button = view.findViewById(R.id.btnIncrease)
        val btnDecrease: Button = view.findViewById(R.id.btnDecrease)
        val finalQuantity: TextView = view.findViewById(R.id.txtCounter)
        val btnAddToCart: Button = view.findViewById(R.id.btncontinue)

        val productId = userProducts.productId
        val productTitle = userProducts.title
        val productUrl = userProducts.imageUrl

        quantity = 1
        cost = userProducts.offerPrice.toDouble()
        finalCost = userProducts.offerPrice.toDouble()
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        Picasso.get().load(productUrl).into(productImage)
        productName.text = productTitle
        originalPriceEach.text = "Rs. ${userProducts.sellingPrice}"
        originalOfferPriceEach.text = "Rs. ${userProducts.offerPrice}"
        finalPrice.text = "Rs. ${finalCost.toString()}"
        finalQuantity.text = quantity.toString()
        val dialog = builder.create()
        dialog.show()
        btnIncrease.setOnClickListener {
            finalCost += cost
            quantity++
            val amount = finalCost
            finalPrice.text = "Rs. ${amount}"
            finalQuantity.text = quantity.toString()
        }
        btnDecrease.setOnClickListener {
            if (quantity > 1) {
                finalCost -= cost
                quantity--
                val amount = finalCost
                finalPrice.text = "Rs. ${amount}"
                finalQuantity.text = quantity.toString()
            }

        }
        btnAddToCart.setOnClickListener {
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            val user = auth.currentUser
            val uid = user!!.uid
            val title = productName.text.toString().trim()
            val priceEach = originalOfferPriceEach.text.toString().replace("Rs. ","").trim()
            val finalPr = finalPrice.text.toString().replace("Rs. ", "").trim()
            val finalQ = finalQuantity.text.toString().trim()
            val sellingPrice=originalPriceEach.text.toString().replace("Rs. ","").trim()
            addToCart(uid, title, priceEach, finalPr, finalQ, userProducts.shopId,productId,productUrl,sellingPrice)
            dialog.dismiss()
        }
    }
    private lateinit var cart:UserCartDetails
    private lateinit var cartDetails:DatabaseReference
    private fun addToCart(
        uid: String,
        title: String,
        priceEach: String,
        finalPr: String,
        finalQ: String,
        shopId: String,
        productId: String,
        productUrl:String,
        sellingPrice:String
    ) {
        cart=UserCartDetails(productId,uid,title,priceEach,finalPr,finalQ,shopId,productUrl,sellingPrice)
        cartDetails= FirebaseDatabase.getInstance().reference.child("users").child(uid).child("Cart")
        cartDetails.child(productId).setValue(cart)
        Toast.makeText(context,"Item added in the cart",Toast.LENGTH_SHORT).show()
    }
    inner class HolderUserProducts(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.imgProduct_customer)
        val productTitle: TextView = view.findViewById(R.id.txtProductTitle_customer)
        val productPrice: TextView = view.findViewById(R.id.txtProductPrice_customer)
        val productOfferPrice: TextView = view.findViewById(R.id.txtProductOfferPrice)
        val addItem: Button = view.findViewById(R.id.btnAddItem)

    }
}