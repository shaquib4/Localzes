package com.example.localzes.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Modals.Upload
import com.example.localzes.R
import com.example.localzes.UserProductsActivity
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class AdapterUserShops(val context: Context, private val shopsUser: List<Upload>) :
    RecyclerView.Adapter<AdapterUserShops.HolderUserShops>() {
    class HolderUserShops(view: View) : RecyclerView.ViewHolder(view) {
        val imgShop: ImageView = view.findViewById(R.id.imgShop)
        val shopName: TextView = view.findViewById(R.id.txtShop_name)
        val shopCategory: TextView = view.findViewById(R.id.txtCategory)


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
        Picasso.get().load(shops_user.imageUrl).into(holder.imgShop)

        holder.shopName.text =
            shops_user.shop_name.substring(0, 1).toUpperCase() + shops_user.shop_name.substring(1)
        holder.shopCategory.text = shops_user.category1
        val reference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(shops_user.shopId)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("StoreStatus").value.toString() == "OPEN") {
                    Picasso.get().load(shops_user.imageUrl).into(holder.imgShop)
                }
                else if(snapshot.child("StoreStatus").value.toString() == "CLOSED"){
                    val draw=holder.imgShop.drawable
                    val bitmap=(draw as BitmapDrawable).bitmap
                    val newBitmap=convertImage(bitmap)
                    holder.imgShop.setImageBitmap(newBitmap)
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
        }
    }

    private fun convertImage(original: Bitmap): Bitmap {
        val finalImage = Bitmap.createBitmap(original.width, original.height, original.config)
        var A: Int
        var R: Int
        var G: Int
        var B: Int
        var colorPixel: Int
        val width = original.width
        val height = original.height
        for (x in 0 until width) {
            for (y in 0 until height) {
                colorPixel = original.getPixel(x, y)
                A = Color.alpha(colorPixel)
                R = Color.red(colorPixel)
                G = Color.green(colorPixel)
                B = Color.blue(colorPixel)
                R = (R + G + B) / 3
                G = R
                B = R
                finalImage.setPixel(x, y, Color.argb(A, R, G, B))
            }
        }
        return finalImage
    }
}