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
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AdapterUserProducts(val context: Context, private val products_user: List<ModelAddProduct>) :
    RecyclerView.Adapter<AdapterUserProducts.HolderUserProducts>() {
    class HolderUserProducts(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.imgProduct_customer)
        val productTitle: TextView = view.findViewById(R.id.txtProductTitle_customer)
        val productPrice: TextView = view.findViewById(R.id.txtProductPrice_customer)
        val productOfferPrice: TextView = view.findViewById(R.id.txtProductOfferPrice)
        val addItem: Button = view.findViewById(R.id.btnAddItem)

    }

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
        holder.productOfferPrice.text=spannableString
        holder.addItem.setOnClickListener {

        }
    }
}