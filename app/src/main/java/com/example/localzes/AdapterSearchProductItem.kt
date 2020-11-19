package com.example.localzes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterSearchProductItem(val context: Context, private val searchProduct:List<ModelAddProduct>): RecyclerView.Adapter<AdapterSearchProductItem.HolderSearchProductItem>() {
    class HolderSearchProductItem(view: View) : RecyclerView.ViewHolder(view) {
        val productName:TextView=view.findViewById(R.id.productName)
        val productPrice:TextView=view.findViewById(R.id.price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderSearchProductItem {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_single_product_row, parent, false)
        return HolderSearchProductItem(view)
    }

    override fun getItemCount(): Int {
        return  searchProduct.size

    }

    override fun onBindViewHolder(holder: HolderSearchProductItem, position: Int) {
       val products=searchProduct[position]
        holder.productName.text=products.title
        holder.productPrice.text=products.offerPrice
    }
}