package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterUserProducts
import com.example.localzes.Modals.ModelAddProduct
import com.example.localzes.Modals.UserCartDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserProductsActivity : AppCompatActivity() {
    private var shopId: String? = "100"
    private lateinit var mUserProductDatabase: DatabaseReference
    private lateinit var mUserProducts: List<ModelAddProduct>
    private lateinit var recyclerUserProduct: RecyclerView
    private lateinit var userProductAdapter: AdapterUserProducts
    private lateinit var cartRelativeLayout: RelativeLayout
    private lateinit var cartDatabaseReference: DatabaseReference
    private lateinit var quantityItem: TextView
    private lateinit var costTotal: TextView
    private lateinit var cartItems: List<UserCartDetails>
    private lateinit var auth: FirebaseAuth
    private lateinit var viewCart: TextView
    private lateinit var search:EditText
    var totalCost: Double = 0.00
    var totalOriginalPrice: Double = 0.00
    var totalItems:Int=0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_products)
        search=findViewById(R.id.searchShopProduct)
        shopId = intent.getStringExtra("shopId")
        mUserProducts = ArrayList<ModelAddProduct>()
        cartRelativeLayout = findViewById(R.id.rl_cart_details)
        cartRelativeLayout.visibility = View.GONE
        quantityItem = findViewById(R.id.quantity_item)
        costTotal = findViewById(R.id.total_cost_cart)
        cartItems = ArrayList<UserCartDetails>()
        viewCart = findViewById(R.id.txtViewCart)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        recyclerUserProduct = findViewById(R.id.recycler_shop_user_products)
        recyclerUserProduct.layoutManager = LinearLayoutManager(this)

        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(cs: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchUserProducts(cs.toString().toLowerCase())
            }
        })
        cartDatabaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("Cart")
        mUserProductDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(shopId.toString())
        mUserProductDatabase.child("Products").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (mUserProducts as ArrayList<ModelAddProduct>).clear()
                for (i in snapshot.children) {
                    val obj = ModelAddProduct(
                        i.child("shopId").value.toString(),
                        i.child("productId").value.toString(),
                        i.child("imageUrl").value.toString(),
                        i.child("productCategory").value.toString(),
                        i.child("title").value.toString(),
                        i.child("description").value.toString(),
                        i.child("sellingPrice").value.toString(),
                        i.child("offerPrice").value.toString(),
                        i.child("unit").value.toString(),
                        i.child("quantity").value.toString(),
                        i.child("stock").value.toString()
                    )
                    (mUserProducts as ArrayList<ModelAddProduct>).add(obj)

                }
                userProductAdapter =
                    AdapterUserProducts(
                        this@UserProductsActivity,
                        mUserProducts
                    )
                recyclerUserProduct.adapter = userProductAdapter
            }

        })
        cartDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                var finalPriceList= arrayListOf<Double>()
                var finalQuantityList= arrayListOf<Int>()
                var sellingPriceList= arrayListOf<Double>()
                totalOriginalPrice=0.0
                totalCost=0.0

                for (i in snapshot.children) {
                    finalPriceList.clear()
                    sellingPriceList.clear()
                    (cartItems as ArrayList<UserCartDetails>).clear()
                    val productId=i.child("productId").value.toString()
                    val orderBy = i.child("orderBy").value.toString()
                    val productTitle = i.child("productTitle").value.toString()
                    val priceEach = i.child("priceEach").value.toString()
                    val finalPrice = i.child("finalPrice").value.toString()
                    val finalQuantity = i.child("finalQuantity").value.toString()
                    val orderTo = i.child("orderTo").value.toString()
                    val productImageUrl = i.child("productImageUrl").value.toString()
                    val sellingPrice = i.child("sellingPrice").value.toString()
                    val finalsellingPrice=i.child("finalsellingPrice").value.toString()
                    val obj = UserCartDetails(
                        productId,
                        orderBy,
                        productTitle,
                        priceEach,
                        finalPrice,
                        finalQuantity,
                        orderTo,
                        productImageUrl,
                        sellingPrice,
                        finalsellingPrice
                    )
                    finalPriceList.add(finalPrice.toDouble())
                    sellingPriceList.add(finalsellingPrice.toDouble())
                    for (j in finalPriceList){
                        totalCost+=j
                    }
                    for (k in sellingPriceList){
                     totalOriginalPrice+=k
                    }

                    (cartItems as ArrayList<UserCartDetails>).add(obj)

                }
                if (cartItems.isNotEmpty()) {

                    cartRelativeLayout.visibility = View.VISIBLE
                    totalItems=snapshot.childrenCount.toInt()
                    quantityItem.text = totalItems.toString()
                    costTotal.text = (totalCost).toString()
                    viewCart.setOnClickListener {
                        val intent = Intent(applicationContext, Cart::class.java)
                        intent.putExtra("totalCost",totalCost.toString())
                        intent.putExtra("totalOriginalPrice",totalOriginalPrice.toString())
                        intent.putExtra("totalItems",totalItems.toString())
                        intent.putExtra("shopUid",shopId)
                        startActivity(intent)
                        finish()
                    }
                     if (cartItems.isEmpty()){
                        cartRelativeLayout.visibility=View.GONE
                    }
                }
            }
        })
    }

    private fun searchUserProducts(str:String){
      val  queryProduct =
            FirebaseDatabase.getInstance().reference.child("seller").child(shopId.toString()).child("Products").orderByChild("title")
                .startAt(str)
                .endAt(str + "\uf8ff")
        queryProduct.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (mUserProducts as ArrayList<ModelAddProduct>).clear()
                for (i in snapshot.children) {
                    val obj = ModelAddProduct(
                        i.child("shopId").value.toString(),
                        i.child("productId").value.toString(),
                        i.child("imageUrl").value.toString(),
                        i.child("productCategory").value.toString(),
                        i.child("title").value.toString(),
                        i.child("description").value.toString(),
                        i.child("sellingPrice").value.toString(),
                        i.child("offerPrice").value.toString(),
                        i.child("unit").value.toString(),
                        i.child("quantity").value.toString(),
                        i.child("stock").value.toString()
                    )
                    (mUserProducts as ArrayList<ModelAddProduct>).add(obj)

                }
                userProductAdapter =
                    AdapterUserProducts(
                        this@UserProductsActivity,
                        mUserProducts
                    )
                recyclerUserProduct.adapter = userProductAdapter
            }
        })
    }
}