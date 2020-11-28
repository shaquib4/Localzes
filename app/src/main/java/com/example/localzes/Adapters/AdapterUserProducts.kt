package com.example.localzes.Adapters

import android.app.AlertDialog
import android.content.Context
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Modals.ModelAddProduct
import com.example.localzes.Modals.UserCartDetails
import com.example.localzes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

@Suppress("NAME_SHADOWING")
class AdapterUserProducts(val context: Context, private val products_user: List<ModelAddProduct>) :
    RecyclerView.Adapter<AdapterUserProducts.HolderUserProducts>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderUserProducts {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_single_row_customer, parent, false)
        return HolderUserProducts(view)
    }

    override fun getItemCount(): Int {
        return products_user.size
    }

    override fun onBindViewHolder(holder: HolderUserProducts, position: Int) {
        val userProducts = products_user[position]
        Picasso.get().load(userProducts.imageUrl).into(holder.productImage)
        holder.productTitle.text = userProducts.title.substring(0,1).toUpperCase()+userProducts.title.substring(1)
        holder.productPrice.text = "Rs. ${userProducts.offerPrice}"
        val mString = "Rs. ${userProducts.sellingPrice}"
        val spannableString = SpannableString(mString)
        val mStrikeThrough = StrikethroughSpan()
        val productId = userProducts.productId
        val orderTo = userProducts.shopId
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val orderBy = user!!.uid
        val priceEach = userProducts.offerPrice
        val productTitle = userProducts.title
        val sellingPrice = userProducts.sellingPrice
        val productUrl = userProducts.imageUrl
        var cost: Double = 0.0
        var finalCost: Double = 0.0
        var finalSellingPrice:Double=0.0
        var sellingP:Double=0.0
        var quantity: Int = 0
        var mauth:FirebaseAuth= FirebaseAuth.getInstance()
        var uid=mauth.currentUser!!.uid
        var shopID: String? =null
        val userDatabase= FirebaseDatabase.getInstance().reference.child("users").child(uid).child("Cart")
            .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                   for (i in snapshot.children){
                       val shopId=i.child("orderTo").value.toString()
                       shopID=shopId
                   }
                }
            })
        sellingP=userProducts.sellingPrice.toDouble()
        finalSellingPrice=userProducts.sellingPrice.toDouble()
        cost = userProducts.offerPrice.toDouble()
        finalCost = userProducts.offerPrice.toDouble()
        quantity = 1

        holder.productOfferPrice.text = spannableString
        holder.addItem.setOnClickListener {
            if (orderTo==shopID.toString()||shopID==null){
                addToCart(
                    orderBy,
                    productTitle,
                    priceEach,
                    userProducts.offerPrice,
                    "1",
                    orderTo,
                    productId,
                    productUrl,
                    sellingPrice,
                    holder.btnIncrease,
                    holder.btnDecrease,
                    holder.btnLinear,
                    cost,
                    finalCost,
                    quantity,
                    holder.rating,
                    holder.txtCounter,
                    holder.addItem,
                    sellingP,
                    finalSellingPrice,
                    userProducts.sellingPrice
                )
                holder.addItem.visibility = View.GONE
            }else if (orderTo!==shopID.toString()){
                val builder=AlertDialog.Builder(context)
                val view=LayoutInflater.from(context).inflate(R.layout.custom_layout,null)
                builder.setView(view)
                val show=builder.show()
                val confirm:Button=view.findViewById(R.id.btnConfirm)
                val cancel:Button=view.findViewById(R.id.btnCancel)
                confirm.setOnClickListener {
                    val userDatabase= FirebaseDatabase.getInstance().reference.child("users").child(uid).child("Cart")
                    userDatabase.removeValue()
                    addToCart(
                        orderBy,
                        productTitle,
                        priceEach,
                        userProducts.offerPrice,
                        "1",
                        orderTo,
                        productId,
                        productUrl,
                        sellingPrice,
                        holder.btnIncrease,
                        holder.btnDecrease,
                        holder.btnLinear,
                        cost,
                        finalCost,
                        quantity,
                        holder.rating,
                        holder.txtCounter,
                        holder.addItem,
                        sellingP,
                        finalSellingPrice,
                        userProducts.sellingPrice
                    )
                    holder.addItem.visibility = View.GONE
                    show.dismiss()
                }
                cancel.setOnClickListener {
                    show.dismiss()
                }
            }
        }
    }


    private fun addToCart(
        uid: String,
        title: String,
        priceEach: String,
        finalPr: String,
        finalQ: String,
        shopId: String,
        productId: String,
        productUrl: String,
        sellingPrice: String,
        btnIncrease: Button,
        btnDecrease: Button,
        btnLinear: LinearLayout,
        cost: Double,
        finalCost: Double,
        quantity: Int,
        rating: TextView,
        txtCounter: TextView,
        addItem: Button,
        sellingP:Double,
        finalSellingPrice:Double,
        finalSellingPr:String
    ) {
        val cart = UserCartDetails(
            productId,
            uid,
            title,
            priceEach,
            finalPr,
            finalQ,
            shopId,
            productUrl,
            sellingPrice,
            finalSellingPr
        )
        val cartDetails =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("Cart")
        cartDetails.child(productId).setValue(cart).addOnCompleteListener {
            if (it.isSuccessful) {
                btnLinear.visibility = View.VISIBLE
                var finalCost = finalCost
                var finalSellingPrice=finalSellingPrice
                var sellingP=sellingP
                var cost = cost
                var quantity = quantity
                 var sellingPrice=sellingPrice

                btnIncrease.setOnClickListener {
                    finalSellingPrice +=  sellingP
                    finalCost += cost
                    quantity++
                    txtCounter.text = quantity.toString()
                    val cart = UserCartDetails(
                        productId,
                        uid,
                        title,
                        priceEach,
                        finalCost.toString(),
                        quantity.toString(),
                        shopId,
                        productUrl,
                        sellingPrice,
                        finalSellingPrice.toString()

                    )
                    val cartDetails =
                        FirebaseDatabase.getInstance().reference.child("users").child(uid)
                            .child("Cart")
                    cartDetails.child(productId).setValue(cart)
                }
                btnDecrease.setOnClickListener {
                    var b = txtCounter.text
                    b = quantity.toString()
                    if (quantity > 1) {
                        finalSellingPrice -=  sellingP
                        finalCost -= cost
                        quantity--
                        txtCounter.text = quantity.toString()
                        val cart = UserCartDetails(
                            productId,
                            uid,
                            title,
                            priceEach,
                            finalCost.toString(),
                            quantity.toString(),
                            shopId,
                            productUrl,
                            sellingPrice,
                            finalSellingPrice.toString()
                        )
                        val cartDetails =
                            FirebaseDatabase.getInstance().reference.child("users").child(uid)
                                .child("Cart")
                        cartDetails.child(productId).setValue(cart)
                    }
                    if (b <= 1.toString()) {
                        btnLinear.visibility = View.GONE
                        addItem.visibility = View.VISIBLE
                        val cartDetails =
                            FirebaseDatabase.getInstance().reference.child("users").child(uid)
                                .child("Cart")
                        cartDetails.child(productId).removeValue().addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(context, "Item removed cart", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }
        }
        Toast.makeText(context, "Item added in the cart", Toast.LENGTH_SHORT).show()
    }

    inner class HolderUserProducts(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.imgProduct_customer)
        val productTitle: TextView = view.findViewById(R.id.txtProductTitle_customer)
        val productPrice: TextView = view.findViewById(R.id.txtProductPrice_customer)
        val productOfferPrice: TextView = view.findViewById(R.id.txtProductOfferPrice)
        val addItem: Button = view.findViewById(R.id.btnAddItem)
        val btnIncrease: Button = view.findViewById(R.id.btnIncrease_new)
        val btnDecrease: Button = view.findViewById(R.id.btnDecrease_new)
        val btnLinear: LinearLayout = view.findViewById(R.id.btnLinear)
        val rating: TextView = view.findViewById(R.id.txtRatingShop_customer)
        val txtCounter: TextView = view.findViewById(R.id.txtCounter)


    }
}