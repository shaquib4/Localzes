package com.localze.android.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.localze.android.Modals.Upload
import com.localze.android.R
import com.localze.android.UserProductsActivity
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class AdapterSearchItem(val context: Context, private val search: List<Upload>) :
    RecyclerView.Adapter<AdapterSearchItem.HolderSearchItem>() {
    class HolderSearchItem(view: View) : RecyclerView.ViewHolder(view) {
        val shopImage: ImageView = view.findViewById(R.id.productImage)
        val shopName: TextView = view.findViewById(R.id.productName)
        val shopAddress: TextView = view.findViewById(R.id.price)
        val locality2: TextView = view.findViewById(R.id.price1)

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
        val searchItem = search[position]
        holder.locality2.text = searchItem.locality2
        holder.shopName.text =
            searchItem.shop_name.substring(0, 1).toUpperCase() + searchItem.shop_name.substring(1)
        try {
            holder.shopAddress.text = searchItem.locality.substring(0, 1).toUpperCase() + searchItem.locality.substring(1)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val reference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(searchItem.shopId)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    if (snapshot.child("StoreStatus").value.toString() == "OPEN") {
                        Glide.with(context).load(searchItem.imageUrl).into(holder.shopImage)
                        holder.itemView.isClickable = true
                    } else if (snapshot.child("StoreStatus").value.toString() == "CLOSED") {
                        val colorMatrix = ColorMatrix()
                        colorMatrix.setSaturation(0.0f)
                        val filter = ColorMatrixColorFilter(colorMatrix)
                        holder.shopImage.colorFilter = filter
                        holder.itemView.isClickable = false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })

        try {
            Picasso.get().load(searchItem.imageUrl).into(holder.shopImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(
                context,
                UserProductsActivity::class.java
            )
            intent.putExtra("shopId", searchItem.shopId)
            context.startActivity(intent)

        }
    }

}