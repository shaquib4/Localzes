package com.example.localzes.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Modals.Upload
import com.example.localzes.R
import com.example.localzes.UserProductsActivity
import com.squareup.picasso.Picasso

class AdapterSearchItem(val context: Context,private val search:List<Upload>):RecyclerView.Adapter<AdapterSearchItem.HolderSearchItem>() {
    class HolderSearchItem(view: View) : RecyclerView.ViewHolder(view) {
        val shopImage:ImageView=view.findViewById(R.id.productImage)
        val shopName:TextView=view.findViewById(R.id.productName)
        val shopAddress:TextView=view.findViewById(R.id.price)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderSearchItem {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_single_row, parent, false)
        return HolderSearchItem(
            view
        )
    }

    override fun getItemCount(): Int {
       return search.size
    }

    override fun onBindViewHolder(holder: HolderSearchItem, position: Int) {
        val searchItem=search[position]
        holder.shopName.text=searchItem.shop_name.substring(0,1).toUpperCase()+searchItem.shop_name.substring(1)
        try{holder.shopAddress.text=searchItem.locality.substring(0,1).toUpperCase()+
                searchItem.locality.substring(1)}catch (e:Exception){
            e.printStackTrace()
        }

        Picasso.get().load(searchItem.imageUrl).into(holder.shopImage)
        holder.itemView.setOnClickListener{
            val intent= Intent(context,
                UserProductsActivity::class.java)
            intent.putExtra("shopId",searchItem.shopId)
            context.startActivity(intent)

        }
    }

}