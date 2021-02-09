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
import com.localze.android.Modals.ModelAddProduct
import com.localze.android.R
import com.localze.android.UserProductsActivity
import com.squareup.picasso.Picasso
import java.lang.Exception

class AdapterSearchProductItem(
    val context: Context,
    private val searchProduct: List<ModelAddProduct>
) : RecyclerView.Adapter<AdapterSearchProductItem.HolderSearchProductItem>() {
    class HolderSearchProductItem(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.productName)
        val productPrice: TextView = view.findViewById(R.id.price)
        val productImage: ImageView = view.findViewById(R.id.productImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderSearchProductItem {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.search_single_product_row, parent, false)
        return HolderSearchProductItem(
            view
        )
    }

    override fun getItemCount(): Int {
        return searchProduct.size

    }

    override fun onBindViewHolder(holder: HolderSearchProductItem, position: Int) {
        val products = searchProduct[position]
    try {
        holder.productName.text =
            products.title.substring(0, 1).toUpperCase() + products.title.substring(1)
        holder.productPrice.text = "₹" + products.offerPrice+"/ per ${products.quantity}${products.unit}"
    } catch (e:Exception){
        e.printStackTrace()
    }
        try {
            when (products.stock) {
                "IN" -> {
                    Glide.with(context).load(products.imageUrl).into(holder.productImage)
                    holder.itemView.setOnClickListener {
                        val intent = Intent(
                            context,
                            UserProductsActivity::class.java
                        )
                        intent.putExtra("shopId", products.shopId)
                        context.startActivity(intent)

                    }

                }
                "OUT" -> {
                    val colorMatrix = ColorMatrix()
                    colorMatrix.setSaturation(0.0f)
                    val filter = ColorMatrixColorFilter(colorMatrix)
                    holder.productImage.colorFilter = filter
                    holder.itemView.isClickable = false

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            Picasso.get().load(products.imageUrl).into(holder.productImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }
}