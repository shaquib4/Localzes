package com.example.localzes

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AdapterSellerProducts(val context:Context,private val products_seller:List<ModelAddProduct>) : RecyclerView.Adapter<AdapterSellerProducts.HolderProduct>() {
    class HolderProduct(view: View) : RecyclerView.ViewHolder(view){
        val imgProduct: ImageView =view.findViewById(R.id.imgProduct)
        val txtProductTitle: TextView =view.findViewById(R.id.txtProductTitle)
        val txtProductPrice:TextView=view.findViewById(R.id.txtProductPrice)
        val txtProductOfferPrice:TextView=view.findViewById(R.id.txtProductOfferPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProduct {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.product_single_row,parent,false)
        return HolderProduct(view)
    }

    override fun getItemCount(): Int {
        return products_seller.size
    }

    override fun onBindViewHolder(holder: HolderProduct, position: Int) {
        val products=products_seller[position]
        Picasso.get().load(products.imageUrl).into(holder.imgProduct)
        holder.txtProductTitle.text=products.title
        holder.txtProductPrice.text="Rs. ${products.offerPrice}"
        val mString="Rs. ${products.sellingPrice}"
        val spannableString=SpannableString(mString)
        val mStrikeThrough=StrikethroughSpan()
        spannableString.setSpan(mStrikeThrough,0,mString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.txtProductOfferPrice.text=spannableString
    }
}