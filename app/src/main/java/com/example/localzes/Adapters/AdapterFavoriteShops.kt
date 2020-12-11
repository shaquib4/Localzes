package com.example.localzes.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.localzes.Modals.Upload
import com.example.localzes.R
import com.example.localzes.UserProductsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

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
        holder.imgFavorite.setOnClickListener {
            deleteFavShop(position)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, UserProductsActivity::class.java)
            intent.putExtra("shopId", fav_shops.shopId)
            context.startActivity(intent)
        }
    }

    private fun deleteFavShop(position: Int) {
        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        val uid = user!!.uid
        val shopId = favList[position].shopId
        val dbReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("Favorites")
        dbReference.orderByChild("shopId").equalTo(shopId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        i.ref.removeValue()
                    }
                    Toast.makeText(context, "Shop Removed From Favorites", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }
}