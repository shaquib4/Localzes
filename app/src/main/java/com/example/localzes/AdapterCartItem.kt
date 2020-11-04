package com.example.localzes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AdapterCartItem(val context:Context,private val cart_user:List<UserCartDetails>):RecyclerView.Adapter<AdapterCartItem.HolderUserCart>() {
    class HolderUserCart(view: View):RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderUserCart {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.cart_single_row,parent,false)
        return HolderUserCart(view)
    }

    override fun getItemCount(): Int {
        return cart_user.size
    }

    override fun onBindViewHolder(holder: HolderUserCart, position: Int) {

    }
}