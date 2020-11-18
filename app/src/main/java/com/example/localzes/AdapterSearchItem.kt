package com.example.localzes

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AdapterSearchItem(val context: Context,private val search:List<Upload>):RecyclerView.Adapter<AdapterSearchItem.HolderSearchItem>() {
    class HolderSearchItem(view: View) : RecyclerView.ViewHolder(view) {
        val shopImage:ImageView=view.findViewById(R.id.shopImage)
        val shopName:TextView=view.findViewById(R.id.shopName)
        val shopAddress:TextView=view.findViewById(R.id.shopAddress)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderSearchItem {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_single_row, parent, false)
        return HolderSearchItem(view)
    }

    override fun getItemCount(): Int {
       return search.size
    }

    override fun onBindViewHolder(holder: HolderSearchItem, position: Int) {
        val searchItem=search[position]
        holder.shopName.text=searchItem.shop_name
        holder.shopAddress.text=searchItem.locality
        Picasso.get().load(searchItem.imageUrl).into(holder.shopImage)
        holder.itemView.setOnClickListener{
            val intent= Intent(context,UserProductsActivity::class.java)
            intent.putExtra("shopId",searchItem.shopId)
            context.startActivity(intent)

        }
    }

}