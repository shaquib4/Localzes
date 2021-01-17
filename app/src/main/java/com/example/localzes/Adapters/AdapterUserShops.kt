package com.example.localzes.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.localzes.Home
import com.example.localzes.Modals.Upload
import com.example.localzes.R
import com.example.localzes.UserProductsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.lang.Exception

class AdapterUserShops(val context: Context, private val shopsUser: List<Upload>) :
    RecyclerView.Adapter<AdapterUserShops.HolderUserShops>() {
    class HolderUserShops(view: View) : RecyclerView.ViewHolder(view) {
        val imgShop: ImageView = view.findViewById(R.id.imgShop)
        val shopName: TextView = view.findViewById(R.id.txtShop_name)
        val shopCategory: TextView = view.findViewById(R.id.txtCategory)
        val cardShop: CardView = view.findViewById(R.id.card_shop)
        val timing:TextView=view.findViewById(R.id.timing)
        val status:TextView=view.findViewById(R.id.Open)
        val unFavorite: FloatingActionButton = view.findViewById(R.id.btnFavorites)
        val favorite: FloatingActionButton = view.findViewById(R.id.btnFavorites1)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderUserShops {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shop_single_customer, parent, false)
        return HolderUserShops(
            view
        )
    }

    override fun getItemCount(): Int {
        return shopsUser.size
    }

    override fun onBindViewHolder(holder: HolderUserShops, position: Int) {
        val shops_user = shopsUser[position]
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user!!.uid
     try{   Glide.with(context).load(shops_user.imageUrl).into(holder.imgShop)}catch (e:Exception){
         e.printStackTrace()
     }

        holder.shopName.text =
            shops_user.shop_name.substring(0, 1).toUpperCase() + shops_user.shop_name.substring(1)
        holder.shopCategory.text = shops_user.category1
        val shopOpenTime=shops_user.openingTime
        val shopClosingTime=shops_user.closingTime
        holder.timing.text="$shopOpenTime to $shopClosingTime"
        val favShopReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(uid)
                .child("Favorites")
        favShopReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    favShopReference.child(shops_user.shopId)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    holder.unFavorite.visibility = View.GONE
                                    holder.favorite.visibility = View.VISIBLE
                                    holder.favorite.setOnClickListener {
                                        addToFavorites(
                                            it,
                                            shops_user,
                                            uid,
                                            holder.unFavorite,
                                            holder.favorite
                                        )
                                    }

                                } else {
                                    holder.favorite.visibility = View.GONE
                                    holder.unFavorite.visibility = View.VISIBLE
                                    holder.unFavorite.setOnClickListener {
                                        addToFavorites1(
                                            holder.favorite,
                                            holder.unFavorite,
                                            it,
                                            shops_user,
                                            uid
                                        )
                                    }

                                }
                            }

                        })
                } else {
                    holder.unFavorite.setOnClickListener {
                        val view = it
                        holder.unFavorite.visibility = View.GONE
                        holder.favorite.visibility = View.VISIBLE
                        val obj = Upload(
                            shops_user.shopId,
                            shops_user.phone,
                            shops_user.name,
                            shops_user.email,
                            shops_user.address,
                            shops_user.shop_name,
                            shops_user.imageUrl,
                            shops_user.category1,
                            shops_user.upi,
                            shops_user.locality,
                            shops_user.city,
                            shops_user.pinCode,
                            shops_user.state,
                            shops_user.country,
                            shops_user.openingTime,
                            shops_user.closingTime,
                            shops_user.closingDay,
                            shops_user.locality2

                        )
                        favShopReference.child(shops_user.shopId).setValue(obj)
                            .addOnSuccessListener {
                                val snackbar =
                                    Snackbar.make(view, "Added to Favorites", Snackbar.LENGTH_LONG)
                                snackbar.show()
                            }
                    }
                }
            }

        })
        val reference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(shops_user.shopId)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    if (snapshot.child("StoreStatus").value.toString() == "OPEN") {
                        Glide.with(context).load(shops_user.imageUrl).into(holder.imgShop)
                        holder.status.text="Open"
                        holder.itemView.isClickable = true
                    } else if (snapshot.child("StoreStatus").value.toString() == "CLOSED") {
                        val colorMatrix = ColorMatrix()
                        colorMatrix.setSaturation(0.0f)
                        val filter = ColorMatrixColorFilter(colorMatrix)
                        holder.imgShop.colorFilter = filter
                        holder.status.text="Closed"
                        holder.itemView.isClickable = false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })

        holder.itemView.setOnClickListener {
            val intent = Intent(
                context,
                UserProductsActivity::class.java
            )
            /*val databaseReference =
                FirebaseDatabase.getInstance().reference.child("seller").child(shops_user.shopId)
                    .child("shopViews")
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val views = snapshot.child("ViewCount").value.toString()
                    val shopViews = views.toInt()
                    val headers = HashMap<String, String>()
                    headers["viewCount"] = (shopViews + 1).toString()
                    databaseReference.setValue(headers)
                }
            })*/
            intent.putExtra("shopId", shops_user.shopId)
            context.startActivity(intent)
            (context as Home).finish()
        }
    }

    private fun addToFavorites1(
        favorite: FloatingActionButton,
        unFavorite: FloatingActionButton,
        it: View,
        shop_user: Upload,
        uid: String
    ) {
        val view = it
        val obj = Upload(
            shop_user.shopId,
            shop_user.phone,
            shop_user.name,
            shop_user.email,
            shop_user.address,
            shop_user.shop_name,
            shop_user.imageUrl,
            shop_user.category1,
            shop_user.upi,
            shop_user.locality,
            shop_user.city,
            shop_user.pinCode,
            shop_user.state,
            shop_user.country,
            shop_user.openingTime,
            shop_user.closingTime,
            shop_user.closingDay,
            shop_user.locality2

        )
        val favShopReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(uid)
                .child("Favorites")
        favShopReference.child(shop_user.shopId).setValue(obj).addOnSuccessListener {
            unFavorite.visibility = View.GONE
            favorite.visibility = View.VISIBLE
            val snackbar = Snackbar.make(view, "Added to Favorites", Snackbar.LENGTH_LONG)
            snackbar.show()

        }


    }

    private fun addToFavorites(
        it: View,
        shop_user: Upload,
        uid: String,
        unFavorite: FloatingActionButton,
        favorite1: FloatingActionButton
    ) {
        val view = it
        val obj = Upload(
            shop_user.shopId,
            shop_user.phone,
            shop_user.name,
            shop_user.email,
            shop_user.address,
            shop_user.shop_name,
            shop_user.imageUrl,
            shop_user.category1,
            shop_user.upi,
            shop_user.locality,
            shop_user.city,
            shop_user.pinCode,
            shop_user.state,
            shop_user.country,
            shop_user.openingTime,
            shop_user.closingTime,
            shop_user.closingDay,
            shop_user.locality2

        )
        val favShopReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(uid)
                .child("Favorites")
        favShopReference.child(shop_user.shopId).removeValue().addOnSuccessListener {
            favorite1.visibility = View.GONE
            unFavorite.visibility = View.VISIBLE
            val snackbar = Snackbar.make(view, "Removed from Favorites", Snackbar.LENGTH_LONG)
            snackbar.show()
        }

    }
}