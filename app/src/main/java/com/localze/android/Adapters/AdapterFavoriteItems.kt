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
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.localze.android.Modals.ModelAddProduct
import com.localze.android.R
import com.localze.android.UserProductsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdapterFavoriteItems(val context: Context, val favList: List<ModelAddProduct>) :
    RecyclerView.Adapter<AdapterFavoriteItems.HolderFavoriteItems>() {
    class HolderFavoriteItems(view: View) : RecyclerView.ViewHolder(view) {
        val imgItem: ImageView = view.findViewById(R.id.imgItem)
        val itemName: TextView = view.findViewById(R.id.txtItemName)
        val itemOriginalCost: TextView = view.findViewById(R.id.txtItemMRP)
        val itemSellingCost: TextView = view.findViewById(R.id.txtItemPrice)
        val imgFavorites: ImageView = view.findViewById(R.id.imgFavourite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderFavoriteItems {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_favourite_item, parent, false)
        return HolderFavoriteItems(view)
    }

    override fun getItemCount(): Int {
        return favList.size
    }

    override fun onBindViewHolder(holder: HolderFavoriteItems, position: Int) {
        val favItems = favList[position]
        val databaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(favItems.shopId)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    if (snapshot.child("StoreStatus").value.toString() == "OPEN") {
                        databaseReference.child("Products").child(favItems.productId)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {

                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    try {
                                        if (snapshot.child("stock").value.toString() == "IN") {
                                            Glide.with(context).load(favItems.imageUrl)
                                                .into(holder.imgItem)
                                            holder.itemView.isClickable = true
                                        } else if (snapshot.child("stock").value.toString() == "OUT") {
                                            val colorMatrix = ColorMatrix()
                                            colorMatrix.setSaturation(0.0f)
                                            val filter = ColorMatrixColorFilter(colorMatrix)
                                            holder.imgItem.colorFilter = filter
                                            holder.itemView.isClickable = false
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                }

                            })

                    } else if (snapshot.child("StoreStatus").value.toString() == "CLOSED") {
                        val colorMatrix = ColorMatrix()
                        colorMatrix.setSaturation(0.0f)
                        val filter = ColorMatrixColorFilter(colorMatrix)
                        holder.imgItem.colorFilter = filter
                        holder.itemView.isClickable = false
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

        })



      try {

       Glide.with(context).load(favItems.imageUrl).into(holder.imgItem)}catch (e:Exception){
          e.printStackTrace()
      }
        holder.itemName.text = favItems.title
        holder.itemOriginalCost.text = "₹${favItems.sellingPrice}"
        holder.itemSellingCost.text = "₹${favItems.offerPrice}"
        holder.imgFavorites.setOnClickListener {
            deleteFavItem(position)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, UserProductsActivity::class.java)
            intent.putExtra("shopId", favItems.shopId)
            context.startActivity(intent)
        }
    }

    private fun deleteFavItem(position: Int) {
        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        val uid = user!!.uid
        val productId = favList[position].productId
        val dbReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(uid)
                .child("FavoriteItems")
        dbReference.orderByChild("productId").equalTo(productId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        i.ref.removeValue()
                    }
                    Toast.makeText(context, "Item Removed From Favorites", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }
}