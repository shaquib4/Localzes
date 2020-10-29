package com.example.localzes

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AdapterUserShops(val context:Context,private val shops_user:List<Upload>):RecyclerView.Adapter<AdapterUserShops.HolderUserShops>() {
    class HolderUserShops(view: View):RecyclerView.ViewHolder (view){
        val imgShop:ImageView=view.findViewById(R.id.imgShop)
        val shopName:TextView=view.findViewById(R.id.txtShop_name)
        val shopCategory:TextView=view.findViewById(R.id.txtCategory)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderUserShops {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.shop_single_customer,parent,false)
        return HolderUserShops(view)
    }

    override fun getItemCount(): Int {
       return shops_user.size
    }

    override fun onBindViewHolder(holder: HolderUserShops, position: Int) {
        val shops_user=shops_user[position]
        Picasso.get().load(shops_user.imageUrl).into(holder.imgShop)
        holder.shopName.text=shops_user.shop_name
        holder.shopCategory.text=shops_user.category1

        holder.itemView.setOnClickListener{
            val intent=Intent(context,UserProductsActivity::class.java)
            intent.putExtra("ShopId",shops_user.shopId)
            context.startActivity(intent)

        }
    }
}