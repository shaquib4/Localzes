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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.localzes.Modals.ModelAddProduct
import com.example.localzes.R
import com.example.localzes.UpdateProductDetailsActivity
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

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
        Glide.with(context).load(products.imageUrl).into(holder.imgProduct)
        holder.txtProductTitle.text = products.title.substring(0,1).toUpperCase()+products.title.substring(1)
        holder.txtProductPrice.text = "Rs. ${products.offerPrice}"
        val mString = "Rs. ${products.sellingPrice}"
        val spannableString = SpannableString(mString)
        val mStrikeThrough = StrikethroughSpan()
        spannableString.setSpan(mStrikeThrough, 0, mString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.txtProductOfferPrice.text = spannableString
        holder.switch.setOnCheckedChangeListener { buttonView, isChecked ->
            
            if (isChecked){
                val userMap=HashMap<String,Any>()
                userMap["stock"]="IN"


                val reference=FirebaseDatabase.getInstance().reference.child("seller").child(products.shopId).child("Products")
                    .child(products.productId)
                reference.updateChildren(userMap)
            }else{
                val userMap=HashMap<String,Any>()
                userMap["stock"]="OUT"


                val reference=FirebaseDatabase.getInstance().reference.child("seller").child(products.shopId).child("Products")
                    .child(products.productId)
                reference.updateChildren(userMap)
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