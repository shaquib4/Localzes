package com.example.localzes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AdapterCartItem(val context:Context,private val cart_user:List<UserCartDetails>):RecyclerView.Adapter<AdapterCartItem.HolderUserCart>() {
    class HolderUserCart(view: View):RecyclerView.ViewHolder(view) {
        val productImageCart:ImageView=view.findViewById(R.id.imgProduct_customer)
        val productTitleCart:TextView=view.findViewById(R.id.txtProductTitle_customer)
        val productOriginalPriceCart:TextView=view.findViewById(R.id.txtProductPrice_customer)
        val productOfferPriceCart:TextView=view.findViewById(R.id.txtProductOfferPrice)
        val btnDecreaseCart:TextView=view.findViewById(R.id.btnDecrease)
        val quantityCart:TextView=view.findViewById(R.id.txtCounter)
        val btnIncreaseCart:TextView=view.findViewById(R.id.btnIncrease)
        val removeItem:ImageView=view.findViewById(R.id.imgRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderUserCart {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.cart_single_row,parent,false)
        return HolderUserCart(view)
    }

    override fun getItemCount(): Int {
        return cart_user.size
    }

    override fun onBindViewHolder(holder: HolderUserCart, position: Int) {
        val cartDetails=cart_user[position]
        Picasso.get().load(cartDetails.productImageUrl).into(holder.productImageCart)
        holder.productTitleCart.text=cartDetails.productTitle
        holder.productOfferPriceCart.text=cartDetails.priceEach
        holder.productOriginalPriceCart.text=cartDetails.sellingPrice
    }
}