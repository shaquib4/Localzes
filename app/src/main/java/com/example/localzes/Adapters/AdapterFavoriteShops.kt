package com.example.localzes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.localzes.Modals.Upload
import com.example.localzes.R

class AdapterFavoriteShops(val context: Context, val favList: List<Upload>) :
    RecyclerView.Adapter<AdapterFavoriteShops.HolderFavoriteShops>() {
    class HolderFavoriteShops(view: View) : RecyclerView.ViewHolder(view) {
        val imgItem: ImageView = view.findViewById(R.id.imgItem)
        val txtShopName: TextView = view.findViewById(R.id.txtShopName)
        val txtShopAddress: TextView = view.findViewById(R.id.txtShopAddress)
        val imgFavorite: ImageView = view.findViewById(R.id.imgFavourite)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderFavoriteShops {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_favourite_shop, parent, false)
        return HolderFavoriteShops(view)
    }

    override fun getItemCount(): Int {
        return favList.size
    }

    override fun onBindViewHolder(holder: HolderFavoriteShops, position: Int) {
        val fav_shops = favList[position]
        holder.txtShopName.text = fav_shops.shop_name
        holder.txtShopAddress.text = fav_shops.address
        Glide.with(context).load(fav_shops.imageUrl).into(holder.imgItem)
    }
}