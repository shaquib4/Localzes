package com.example.localzes.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.BitmapDrawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.localzes.Modals.ModelAddProduct
import com.example.localzes.R
import com.example.localzes.Seller_Products
import com.example.localzes.UpdateProductDetailsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.lang.Exception

class AdapterSellerProducts(
    val context: Context,
    private val products_seller: List<ModelAddProduct>
) : RecyclerView.Adapter<AdapterSellerProducts.HolderProduct>() {
    class HolderProduct(view: View) : RecyclerView.ViewHolder(view) {
        val imgProduct: ImageView = view.findViewById(R.id.imgProduct_customer)
        val txtProductTitle: TextView = view.findViewById(R.id.txtProductTitle_customer)
        val txtProductPrice: TextView = view.findViewById(R.id.txtProductPrice_customer)
        val txtProductOfferPrice: TextView = view.findViewById(R.id.txtProductOfferPrice)
        val imgEditUpdate: ImageView = view.findViewById(R.id.imgEditUpdate)
        val switch: SwitchCompat = view.findViewById(R.id.switchStock)
        val stock: TextView = view.findViewById(R.id.txtStock)
        val imgRemove: ImageView = view.findViewById(R.id.imgRemoveProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProduct {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.product_single_row, parent, false)
        return HolderProduct(
            view
        )
    }

    override fun getItemCount(): Int {
        return products_seller.size
    }

    override fun onBindViewHolder(holder: HolderProduct, position: Int) {
        val products = products_seller[position]
       try{ Glide.with(context).load(products.imageUrl).into(holder.imgProduct)}
       catch (e:Exception){
           e.printStackTrace()
       }
        holder.txtProductTitle.text =
            products.title.substring(0, 1).toUpperCase() + products.title.substring(1)
        holder.txtProductPrice.text = "₹${products.offerPrice}"
        val mString = "₹${products.sellingPrice}"
        val spannableString = SpannableString(mString)
        val mStrikeThrough = StrikethroughSpan()
        spannableString.setSpan(mStrikeThrough, 0, mString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.txtProductOfferPrice.text = spannableString
        val ref = FirebaseDatabase.getInstance().reference.child("seller").child(products.shopId)
            .child("Products").child(products.productId)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {


                if (snapshot.child("stock").value.toString() == "IN"
                    && products.productId == snapshot.child("productId").value.toString()
                ) {
                    holder.switch.isChecked = true
                    holder.stock.text = "STOCK: IN"
                } else if (snapshot.child("stock").value.toString() == "OUT"
                    && products.productId == snapshot.child("productId").value.toString()
                ) {
                    holder.switch.isChecked = false
                    holder.stock.text = "STOCK: OUT"

                }
            }

        })
        /*    if (products.stock == "IN") {
                holder.switch.isChecked = true
                holder.stock.text = "STOCK: IN"
            } else if (products.stock == "OUT") {
                holder.switch.isChecked = false
                holder.stock.text = "STOCK: OUT"
            }*/
        holder.switch.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                val userMap = HashMap<String, Any>()
                userMap["stock"] = "IN"


                val reference =
                    FirebaseDatabase.getInstance().reference.child("seller").child(products.shopId)
                        .child("Products")
                        .child(products.productId)
                reference.updateChildren(userMap)
                holder.stock.text = "STOCK: IN"
            } else {
                val userMap = HashMap<String, Any>()
                userMap["stock"] = "OUT"


                val reference =
                    FirebaseDatabase.getInstance().reference.child("seller").child(products.shopId)
                        .child("Products")
                        .child(products.productId)
                reference.updateChildren(userMap)
                holder.stock.text = "STOCK: OUT"
            }

        }
        holder.imgEditUpdate.setOnClickListener {
            val intent = Intent(
                context,
                UpdateProductDetailsActivity::class.java
            )
            intent.putExtra("productId", products.productId)
            context.startActivity(intent)
        }
        holder.imgRemove.setOnClickListener {
            deleteProduct(position)
        }
    }

    private fun deleteProduct(position: Int) {
        val userAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val user = userAuth.currentUser
        val uid = user!!.uid
        val productId = products_seller[position].productId
        val database =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("Products")
        database.orderByChild("productId").equalTo(productId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        i.ref.removeValue()
                    }
                    Toast.makeText(context, "Product Removed successfully", Toast.LENGTH_SHORT)
                        .show()
                    (context as Seller_Products).recreate()
                }
            })
    }
}