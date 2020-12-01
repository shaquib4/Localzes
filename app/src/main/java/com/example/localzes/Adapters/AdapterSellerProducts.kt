package com.example.localzes.Adapters

import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Modals.ModelAddProduct
import com.example.localzes.R
import com.example.localzes.UpdateProductDetailsActivity
import com.squareup.picasso.Picasso

class AdapterSellerProducts(val context:Context,private val products_seller:List<ModelAddProduct>) : RecyclerView.Adapter<AdapterSellerProducts.HolderProduct>() {
    class HolderProduct(view: View) : RecyclerView.ViewHolder(view){
        val imgProduct: ImageView =view.findViewById(R.id.imgProduct_customer)
        val txtProductTitle: TextView =view.findViewById(R.id.txtProductTitle_customer)
        val txtProductPrice:TextView=view.findViewById(R.id.txtProductPrice_customer)
        val txtProductOfferPrice:TextView=view.findViewById(R.id.txtProductOfferPrice)
        val imgEditUpdate:ImageView=view.findViewById(R.id.imgEditUpdate)
        val switch: SwitchCompat =view.findViewById(R.id.switchStock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProduct {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.product_single_row,parent,false)
        return HolderProduct(
            view
        )
    }

    override fun getItemCount(): Int {
        return products_seller.size
    }

    override fun onBindViewHolder(holder: HolderProduct, position: Int) {
        val products=products_seller[position]
        Picasso.get().load(products.imageUrl).into(holder.imgProduct)
        holder.txtProductTitle.text=products.title
        holder.txtProductPrice.text="Rs. ${products.offerPrice}"
        val mString="Rs. ${products.sellingPrice}"
        val spannableString=SpannableString(mString)
        val mStrikeThrough=StrikethroughSpan()
        spannableString.setSpan(mStrikeThrough,0,mString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.txtProductOfferPrice.text=spannableString
        holder.switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                Toast.makeText(context,"Switch is On", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context,"Switch is Off", Toast.LENGTH_SHORT).show()
                holder.switch.setThumbResource(R.color.black)
            }
        }
        holder.imgEditUpdate.setOnClickListener {
            val intent= Intent(context,
                UpdateProductDetailsActivity::class.java)
            intent.putExtra("productId",products.productId)
            context.startActivity(intent)
        }
    }
}