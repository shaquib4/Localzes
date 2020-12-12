package com.example.localzes.Adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.localzes.Modals.ModelAddProduct
import com.example.localzes.Modals.UserCartDetails
import com.example.localzes.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.lang.Exception

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
        val uAuth = FirebaseAuth.getInstance()
        val mUser = uAuth.currentUser
        val mUid = mUser!!.uid

        Glide.with(context).load(userProducts.imageUrl).into(holder.productImage)
        holder.productTitle.text =
            userProducts.title.substring(0, 1).toUpperCase() + userProducts.title.substring(1)
        holder.productPrice.text = "Rs. ${userProducts.offerPrice}"
        /*holder.stock.text = "STOCK :- " + userProducts.stock*/
        /* val dRef:DatabaseReference=FirebaseDatabase.getInstance().reference.child("seller").child(userProducts.shopId).child("Products").child(userProducts.productId)
         dRef.addValueEventListener(object :ValueEventListener{
             override fun onCancelled(error: DatabaseError) {

             }

             override fun onDataChange(snapshot: DataSnapshot) {
                 if(snapshot.child("stock").value.toString()==)
             }

         })*/
       when (userProducts.stock) {
            "IN" -> {
                Glide.with(context).load(userProducts.imageUrl).into(holder.productImage)
                holder.stock.text == "STOCK: IN"

            }
            "OUT" -> {
                val colorMatrix = ColorMatrix()
                colorMatrix.setSaturation(0.0f)
                val filter = ColorMatrixColorFilter(colorMatrix)
                holder.productImage.colorFilter = filter
                holder.stock.text = "STOCK: OUT"


            }
        }

      /*val ref =
            FirebaseDatabase.getInstance().reference.child("seller").child(userProducts.shopId)
                .child("Products").child(userProducts.productId)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
               if( snapshot.child("productId").value.toString()==userProducts.productId){
                   try {
                       val stock=snapshot.child("stock").value.toString()
                       if (stock=="IN") {
                           holder.addItem.visibility=View.VISIBLE
                           holder.card.isClickable=false
                           holder.stock.text == "STOCK: IN"
                           Glide.with(context).load(userProducts.imageUrl).into(holder.productImage)


                       } else {
                           holder.addItem.visibility=View.GONE
                           holder.card.isClickable=false
                           holder.stock.text = "STOCK: OUT"
                           val colorMatrix = ColorMatrix()
                           colorMatrix.setSaturation(0.0f)
                           val filter = ColorMatrixColorFilter(colorMatrix)
                           holder.productImage.colorFilter = filter

                       }
                   }catch (e: Exception) {
                       e.printStackTrace()
                   }

               }


            }

        })*/

        holder.unfavorite.setOnClickListener {
            val view = it
            holder.unfavorite.visibility = View.GONE
            holder.favorite.visibility = View.VISIBLE
            val obj = ModelAddProduct(
                userProducts.shopId,
                userProducts.productId,
                userProducts.imageUrl,
                userProducts.productCategory,
                userProducts.title,
                userProducts.description,
                userProducts.sellingPrice,
                userProducts.offerPrice,
                userProducts.unit,
                userProducts.quantity,
                userProducts.stock
            )
            val oDatabaseReference =
                FirebaseDatabase.getInstance().reference.child("users").child(mUid)
                    .child("FavoritesItems")
            oDatabaseReference.child(userProducts.shopId).setValue(obj).addOnSuccessListener {
                val snackbar = Snackbar.make(view, "Added to favorites", Snackbar.LENGTH_LONG)
                snackbar.show()
            }
        }
        holder.favorite.setOnClickListener {
            val view = it

            val favShopReference: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child("users").child(mUid)
                    .child("FavoriteItems")
            favShopReference.child(userProducts.shopId).removeValue().addOnSuccessListener {
                holder.favorite.visibility = View.GONE
                holder.unfavorite.visibility = View.VISIBLE
                val snackbar = Snackbar.make(view, "Removed from Favorites", Snackbar.LENGTH_LONG)
                snackbar.show()
            }
        }
        val mString = "Rs. ${userProducts.sellingPrice}"
        val spannableString = SpannableString(mString)
        val mStrikeThrough = StrikethroughSpan()
        var pid: String? = null
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
        var finalSellingPrice: Double = 0.0

        var sellingP: Double = 0.0
        var quantity: Int = 0
        var mauth: FirebaseAuth = FirebaseAuth.getInstance()
        var uid = mauth.currentUser!!.uid
        var shopID: String? = null
        sellingP = userProducts.sellingPrice.toDouble()
        finalSellingPrice = userProducts.sellingPrice.toDouble()
        cost = userProducts.offerPrice.toDouble()
        finalCost = userProducts.offerPrice.toDouble()
        quantity = 1
        val userDatabase =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("Cart")
        userDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    val shopId = i.child("orderTo").value.toString()

                    shopID = shopId
                }
            }
        })
        userDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userDatabase.child(productId)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                var finalQDB = snapshot.child("finalQuantity").value.toString()
                                val finalPDB = snapshot.child("finalPrice").value.toString()
                                val finalSPDB = snapshot.child("finalsellingPrice").value.toString()
                                val sPDB = snapshot.child("sellingPrice").value.toString()
                                val cPDB = snapshot.child("priceEach").value.toString()
                                if (snapshot.child("productId").value.toString() == productId && finalQDB.isNotEmpty()&&userProducts.stock=="IN") {
                                    holder.addItem.visibility = View.GONE
                                    holder.btnLinear.visibility = View.VISIBLE
                                    holder.txtCounter.text = finalQDB
                                    if (orderTo == shopID.toString() || shopID == null) {
                                        addToCart1(
                                            orderBy,
                                            productTitle,
                                            priceEach,
                                            finalPDB,
                                            finalQDB,
                                            orderTo,
                                            productId,
                                            productUrl,
                                            sellingPrice,
                                            holder.btnIncrease,
                                            holder.btnDecrease,
                                            holder.btnLinear,
                                            cPDB.toDouble(),
                                            finalPDB.toDouble(),
                                            finalQDB.toInt(),
                                            holder.rating,
                                            holder.txtCounter,
                                            holder.addItem,
                                            sPDB.toDouble(),
                                            finalSPDB.toDouble(),
                                            finalSPDB
                                        )

                                    }

                                } else if (snapshot.child("productId").value.toString() !== productId&&userProducts.stock=="IN") {
                                    holder.addItem.visibility = View.VISIBLE
                                    holder.addItem.setOnClickListener {
                                        if (orderTo == shopID.toString() || shopID == null) {
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

                                        } else if (orderTo !== shopID.toString()) {
                                            val builder = AlertDialog.Builder(context)
                                            val view = LayoutInflater.from(context)
                                                .inflate(R.layout.custom_layout, null)
                                            builder.setView(view)
                                            val show = builder.show()
                                            val confirm: Button = view.findViewById(R.id.btnConfirm)
                                            val cancel: Button = view.findViewById(R.id.btnCancel)
                                            confirm.setOnClickListener {
                                                val userDatabase =
                                                    FirebaseDatabase.getInstance().reference.child("users")
                                                        .child(uid).child("Cart")
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
                            }
                        })


                } else if(!snapshot.exists()&&userProducts.stock=="IN") {
                    holder.addItem.setOnClickListener {
                        if (orderTo == shopID.toString() || shopID == null) {
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

                        } else if (orderTo !== shopID.toString()) {
                            val builder = AlertDialog.Builder(context)
                            val view =
                                LayoutInflater.from(context).inflate(R.layout.custom_layout, null)
                            builder.setView(view)
                            val show = builder.show()
                            val confirm: Button = view.findViewById(R.id.btnConfirm)
                            val cancel: Button = view.findViewById(R.id.btnCancel)
                            confirm.setOnClickListener {
                                val userDatabase =
                                    FirebaseDatabase.getInstance().reference.child("users")
                                        .child(uid).child("Cart")
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
                }else if(!snapshot.exists()&&userProducts.stock=="OUT"){
                    holder.addItem.isClickable=false
                }
            }
        })






        holder.productOfferPrice.text = spannableString


    }

    private fun addToCart1(
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
        sellingP: Double,
        finalSellingPrice: Double,
        finalSellingPr: String
    ) {
        var finalCost = finalCost
        var finalSellingPrice = finalSellingPrice
        var sellingP = sellingP
        var cost = cost
        var quantity = quantity
        var sellingPrice = sellingPrice

        btnIncrease.setOnClickListener {
            finalSellingPrice += sellingP
            finalCost += cost
            quantity++
            txtCounter.text = quantity.toString()
            val cartMap = HashMap<String, Any>()
            cartMap["productId"] = productId
            cartMap["orderBy"] = uid
            cartMap["productTitle"] = title
            cartMap["priceEach"] = priceEach
            cartMap["finalPrice"] = finalCost.toString()
            cartMap["finalQuantity"] = quantity.toString()
            cartMap["orderTo"] = shopId
            cartMap["productImageUrl"] = productUrl
            cartMap["sellingPrice"] = sellingPrice
            cartMap["finalsellingPrice"] = finalSellingPrice.toString()
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
            cartDetails.child(productId).updateChildren(cartMap)
        }
        btnDecrease.setOnClickListener {
            var b = txtCounter.text
            b = quantity.toString()
            if (quantity > 1) {
                finalSellingPrice -= sellingP
                finalCost -= cost
                quantity--
                txtCounter.text = quantity.toString()
                val cartMap = HashMap<String, Any>()
                cartMap["productId"] = productId
                cartMap["orderBy"] = uid
                cartMap["productTitle"] = title
                cartMap["priceEach"] = priceEach
                cartMap["finalPrice"] = finalCost.toString()
                cartMap["finalQuantity"] = quantity.toString()
                cartMap["orderTo"] = shopId
                cartMap["productImageUrl"] = productUrl
                cartMap["sellingPrice"] = sellingPrice
                cartMap["finalsellingPrice"] = finalSellingPrice.toString()
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
                cartDetails.child(productId).updateChildren(cartMap)
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
        sellingP: Double,
        finalSellingPrice: Double,
        finalSellingPr: String
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
        val cartMap = HashMap<String, Any>()
        cartMap["productId"] = productId
        cartMap["orderBy"] = uid
        cartMap["productTitle"] = title
        cartMap["priceEach"] = priceEach
        cartMap["finalPrice"] = finalPr
        cartMap["finalQuantity"] = finalQ
        cartMap["orderTo"] = shopId
        cartMap["productImageUrl"] = productUrl
        cartMap["sellingPrice"] = sellingPrice
        cartMap["finalsellingPrice"] = finalSellingPr

        val cartDetails =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("Cart")
        cartDetails.child(productId).setValue(cartMap).addOnCompleteListener {
            if (it.isSuccessful) {
                btnLinear.visibility = View.VISIBLE

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
        val stock: TextView = view.findViewById(R.id.txtStock_customer)
        val unfavorite: FloatingActionButton = view.findViewById(R.id.btnFavoriteItem)
        val favorite: FloatingActionButton = view.findViewById(R.id.btnFavoriteItem1)
        val card:CardView=view.findViewById(R.id.card1_customer)

    }
}