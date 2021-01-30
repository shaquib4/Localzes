package com.localze.android.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.localze.android.Modals.ModelAddProduct
import com.localze.android.R

class AdapterCatalogue(val context: Context,private val menu_card:List<ModelAddProduct>):RecyclerView.Adapter<AdapterCatalogue.HolderProductCatalogue>() {
    class HolderProductCatalogue(view:View):RecyclerView.ViewHolder(view) {
        val itemName:TextView=view.findViewById(R.id.Itemname)
        val itemQuantity:TextView=view.findViewById(R.id.quan)
        val itemPrice:TextView=view.findViewById(R.id.priceMenu)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProductCatalogue {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.menu_single_row,parent,false)
        return HolderProductCatalogue(view)
    }

    override fun getItemCount(): Int {
        return menu_card.size
    }

    override fun onBindViewHolder(holder: HolderProductCatalogue, position: Int) {
        val menu_list=menu_card[position]
        holder.itemName.text=menu_list.title
        holder.itemQuantity.text="(${menu_list.quantity} ${menu_list.unit})"
        holder.itemPrice.text=menu_list.offerPrice

    }
}