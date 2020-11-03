package com.example.localzes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AdapterCartItem(val context: Context,private val user_cart:List<UserCartDetails> ):RecyclerView.Adapter<AdapterCartItem.HolderCartProducts>() {
    class HolderCartProducts(view:View):RecyclerView.ViewHolder(view) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCartProducts {
       
    }

    override fun getItemCount(): Int {
        return user_cart.size
    }

    override fun onBindViewHolder(holder: HolderCartProducts, position: Int) {

    }
}