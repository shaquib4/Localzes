package com.example.localzes

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
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class AdapterCartItem(val context: Context, private val cart_user: List<UserCartDetails>) :
    RecyclerView.Adapter<AdapterCartItem.HolderUserCart>() {
    class HolderUserCart(view: View) : RecyclerView.ViewHolder(view) {
        val productImageCart: ImageView = view.findViewById(R.id.imgProduct_customer)
        val productTitleCart: TextView = view.findViewById(R.id.txtProductTitle_customer)
        val productOriginalPriceCart: TextView = view.findViewById(R.id.txtProductPrice_customer)
        val productOfferPriceCart: TextView = view.findViewById(R.id.txtProductOfferPrice2)
        val btnDecreaseCart: Button = view.findViewById(R.id.btnDecrease)
        val quantityCart: TextView = view.findViewById(R.id.txtCounter)
        val btnIncreaseCart: Button = view.findViewById(R.id.btnIncrease)
        val removeItem: ImageView = view.findViewById(R.id.imgRemove)
        val productTotalPrice: TextView = view.findViewById(R.id.txtProductTotalPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderUserCart {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cart_single_row, parent, false)
        return HolderUserCart(view)
    }

    override fun getItemCount(): Int {
        return cart_user.size
    }

    private lateinit var mCartDatabase: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    override fun onBindViewHolder(holder: HolderUserCart, position: Int) {
        val cartDetails = cart_user[position]
        val quantity = cartDetails.finalQuantity
        val cost = cartDetails.finalPrice
        val costOne = cartDetails.priceEach
        val finalSelling=cartDetails.finalsellingPrice
        Picasso.get().load(cartDetails.productImageUrl).into(holder.productImageCart)
        holder.productTitleCart.text = cartDetails.productTitle
        holder.productOfferPriceCart.text = cartDetails.priceEach
        val mString = "Rs. ${cartDetails.sellingPrice}"
        val spannableString = SpannableString(mString)
        val mStrikeThrough = StrikethroughSpan()
        spannableString.setSpan(mStrikeThrough, 0, mString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.productOriginalPriceCart.text = spannableString
        holder.productTotalPrice.text = cartDetails.finalPrice
        holder.quantityCart.text = quantity
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        val uid = user!!.uid
        mCartDatabase =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("Cart")
        var items = quantity.toInt()
        var updatedCost = cost.toDouble()
        val oneCost = costOne.toInt()
        val finalSell=finalSelling.toDouble()
        holder.btnDecreaseCart.setOnClickListener {
            if (items > 1) {
                updatedCost -= oneCost
                items--
                holder.productTotalPrice.text = "Rs. ${updatedCost}"
                holder.quantityCart.text = items.toString()
                val headers=HashMap<String,Any>()
                headers["finalPrice"]=updatedCost.toString()
                headers["finalQuantity"]=items.toString()
                headers["finalSellingPrice"]
                mCartDatabase.child(cart_user[position].productId)
            }
        }
        holder.btnIncreaseCart.setOnClickListener {
            updatedCost += oneCost
            items++
            holder.productTotalPrice.text = "Rs. ${updatedCost}"
            holder.quantityCart.text = items.toString()
        }
        holder.removeItem.setOnClickListener {
            deleteItem(position)
        }
    }

    private fun deleteItem(position: Int) {
        val user = mAuth.currentUser
        val uid = user!!.uid
        val productId = cart_user[position].productId
        val itemTitle = cart_user[position].productTitle
        val dbReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("Cart")
        dbReference.orderByChild("productId").equalTo(productId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        i.ref.removeValue()
                    }
                    Toast.makeText(context, "Item $itemTitle removed from cart", Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }
}