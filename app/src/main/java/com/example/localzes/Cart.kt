package com.example.localzes

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterCartItem
import com.example.localzes.Adapters.AdapterManageAddress
import com.example.localzes.Modals.ModelManageAddress
import com.example.localzes.Modals.ModelOrderDetails
import com.example.localzes.Modals.UserCartDetails
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_cart1.*
import util.ConnectionManager

class Cart : AppCompatActivity() {
    private lateinit var cartDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerCartProduct: RecyclerView
    private lateinit var userCartAdapter: AdapterCartItem
    private lateinit var cartProducts: List<UserCartDetails>
    private lateinit var totalItems: TextView
    private lateinit var txtTotalPrice: TextView
    private var totalCost: Double = 0.0
    private var totalOriginalPrice: Double = 0.0
    private var totalItem: Int = 0
    private lateinit var txtPrice: TextView
    private lateinit var txtDiscountPrice: TextView
    private lateinit var txtDeliveryCharge: TextView
    private lateinit var txtTotalAmount: TextView
    private lateinit var totalPayment: TextView
    private lateinit var btnContinue: Button
    var discountAmount: Double = 0.00
    private lateinit var shopId: String
    private lateinit var pId: String
    private lateinit var orderByuid: String
    private lateinit var orderDetails: ModelOrderDetails
    private lateinit var progressDialog: ProgressDialog
    private lateinit var deliveryUser: String
    private lateinit var deliveryUserMobileNo: String
    private lateinit var deliveryAddress: TextView
    private lateinit var addAddress: TextView
    private var boolean: Boolean = true
    private lateinit var relativeCartEmpty: RelativeLayout
    private lateinit var relativeCart: RelativeLayout
    private lateinit var orderByName: String
    private lateinit var orderByMobile: String
    private lateinit var addresses: List<ModelManageAddress>
    private lateinit var btnShopNow: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart1)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid

        recyclerCartProduct = findViewById(R.id.cart_recycler_view)
        txtTotalPrice = findViewById(R.id.txtTotalPrice)
        txtPrice = findViewById(R.id.txtPrice)
        txtDeliveryCharge = findViewById(R.id.txtDeliveryCharge)
        txtDiscountPrice = findViewById(R.id.txtDiscountPrice)
        txtTotalAmount = findViewById(R.id.txtTotalAmount)
        totalPayment = findViewById(R.id.TotalPayment)
        btnContinue = findViewById(R.id.btnContinue)
        deliveryAddress = findViewById(R.id.txtArea)
        addAddress = findViewById(R.id.txtAddAddress)
        btnShopNow = findViewById(R.id.btnShopNow)
        addresses = ArrayList<ModelManageAddress>()
        relativeCartEmpty = findViewById(R.id.rl_cart_empty)
        relativeCart = findViewById(R.id.rl_cart)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)
        totalCost = 0.0
        totalOriginalPrice = 0.0

        recyclerCartProduct.layoutManager = LinearLayoutManager(this)
        cartProducts = ArrayList<UserCartDetails>()
        totalItems = findViewById(R.id.txtTotalItems)
        cartDatabase =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("Cart")

        bottom_navCarts.selectedItemId = R.id.nav_cart
        bottom_navCarts.setOnNavigationItemSelectedListener { item ->


            when (item.itemId) {

                R.id.nav_home -> {
                    startActivity(Intent(this, Home::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_search -> {

                    startActivity(Intent(this, Search::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_cart -> {

                    return@setOnNavigationItemSelectedListener true

                }
                R.id.nav_account -> {

                    startActivity(Intent(this, Accounts::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
            }
            return@setOnNavigationItemSelectedListener true
        }

        retryCart.setOnClickListener {
            this.recreate()
        }
        if (ConnectionManager().checkConnectivity(this)) {
            rl_cart_connection.visibility=View.VISIBLE
            rl_retryCart.visibility=View.GONE
            cartDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var finalPriceList = arrayListOf<Double>()
                var finalQuantityList = arrayListOf<Int>()
                var sellingPriceList = arrayListOf<Double>()
                (cartProducts as ArrayList<UserCartDetails>).clear()
                totalOriginalPrice = 0.0
                totalCost = 0.0

                for (i in snapshot.children) {
                    finalPriceList.clear()
                    sellingPriceList.clear()

                    val productId = i.child("productId").value.toString()
                    val orderBy = i.child("orderBy").value.toString()
                    val productTitle = i.child("productTitle").value.toString()
                    val priceEach = i.child("priceEach").value.toString()
                    val finalPrice = i.child("finalPrice").value.toString()
                    val finalQuantity = i.child("finalQuantity").value.toString()
                    val orderTo = i.child("orderTo").value.toString()
                    val productImageUrl = i.child("productImageUrl").value.toString()
                    val sellingPrice = i.child("sellingPrice").value.toString()
                    val finalsellingPrice = i.child("finalsellingPrice").value.toString()
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
                    shopId = orderTo
                    pId = productId
                    orderByuid = orderBy
                    for (j in finalPriceList) {
                        totalCost += j
                    }
                    for (k in sellingPriceList) {
                        totalOriginalPrice += k
                    }

                    (cartProducts as ArrayList<UserCartDetails>).add(obj)

                }
                if (cartProducts.isEmpty()) {
                    relativeCart.visibility = View.GONE
                    btnShopNow.setOnClickListener {
                        val intent = Intent(this@Cart, Home::class.java)
                        startActivity(intent)
                        finish()
                    }

                } else {
                    relativeCartEmpty.visibility = View.GONE
                    relativeCart.visibility = View.VISIBLE
                    userCartAdapter = AdapterCartItem(
                        this@Cart,
                        cartProducts
                    )
                    recyclerCartProduct.adapter = userCartAdapter
                    totalItem = snapshot.childrenCount.toInt()
                    totalItems.text = "Total Item :- ${snapshot.childrenCount}"
                    txtPrice.text = "₹${totalOriginalPrice}"
                    discountAmount =
                        ((totalOriginalPrice.toString()).toDouble() - (totalCost.toString()
                            .toDouble()))
                    txtDiscountPrice.text = "₹${discountAmount}"
                    if (snapshot.childrenCount > 1) {
                        txtTotalPrice.text = "Price(${snapshot.childrenCount} items)"
                    } else {
                        txtTotalPrice.text = "Price(${snapshot.childrenCount} item)"
                    }

                    val amount = totalCost.toString()
                    txtTotalAmount.text = "₹${amount}"
                    totalPayment.text = "₹${amount}"
                    for (i in cartProducts) {
                        val proId = i.productId
                        val database =
                            FirebaseDatabase.getInstance().reference.child("seller").child(shopId)
                                .child("Products").child(proId)
                        database.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {


                                if (snapshot.child("stock").value.toString() == "OUT") {

                                    boolean = false
                                } /*else if (snapshot.child("stock").value.toString() == "IN"&&snapshot.child("stock").value.toString()
                                        !=="OUT") {
                                            val intent =
                                                Intent(this@Cart, continue_payment::class.java)
                                            intent.putExtra("shopId", shopId)
                                            intent.putExtra("totalCost", totalCost.toString())
                                            intent.putExtra("orderBy", orderByuid)
                                            intent.putExtra("totalItem", totalItem.toString())
                                            intent.putExtra("delivery", deliveryUser)
                                            intent.putExtra("orderByName", orderByName)
                                            intent.putExtra("orderByMobile", orderByMobile)
                                            startActivity(intent)
                                            finish()
                                        }*/


                            }
                        })
                    }
/*
                    val database=FirebaseDatabase.getInstance().reference.child("seller")
                        .child(shopId).child("Products").child(pId)
                    database.addValueEventListener(object :ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            btnContinue.setOnClickListener {
                                if (snapshot.child("stock").value.toString()=="OUT"){
                                    Toast.makeText(this@Cart,"Some Products are out of stock,Please remove",Toast.LENGTH_SHORT)
                                        .show()
                                }else if (snapshot.child("stock").value.toString()=="IN"){
                                    val intent = Intent(this@Cart, continue_payment::class.java)
                                    intent.putExtra("shopId", shopId)
                                    intent.putExtra("totalCost", totalCost.toString())
                                    intent.putExtra("orderBy", orderByuid)
                                    intent.putExtra("totalItem", totalItem.toString())
                                    intent.putExtra("delivery", deliveryUser)
                                    intent.putExtra("orderByName", orderByName)
                                    intent.putExtra("orderByMobile", orderByMobile)
                                    startActivity(intent)
                                    finish()
                                }}

                        }
                    })*/
                }


            }
        })}else{
            rl_cart_connection.visibility=View.GONE
            rl_retryCart.visibility=View.VISIBLE
        }


            btnContinue.setOnClickListener {
                if(ConnectionManager().checkConnectivity(this)){
                    rl_cart_connection.visibility=View.VISIBLE
                    rl_retryCart.visibility=View.GONE
            if (boolean) {
                val intent = Intent(this@Cart, continue_payment::class.java)
                intent.putExtra("shopId", shopId)
                intent.putExtra("totalCost", totalCost.toString())
                intent.putExtra("orderBy", orderByuid)
                intent.putExtra("totalItem", totalItem.toString())
                intent.putExtra("delivery", deliveryUser)
                intent.putExtra("orderByName", orderByName)
                intent.putExtra("orderByMobile", orderByMobile)
                startActivity(intent)
                finish()
            } else if (!boolean) {
                Toast.makeText(
                    this,
                    "Some Products are out of stock,Please remove",
                    Toast.LENGTH_SHORT
                ).show()
            }}else{
                    rl_cart_connection.visibility=View.GONE
                    rl_retryCart.visibility=View.VISIBLE
                }
        }
        if (ConnectionManager().checkConnectivity(this)) {val reference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(uid)
                .child("current_address")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                deliveryUser = snapshot.child("address").value.toString()
                deliveryUserMobileNo = snapshot.child("mobileNo").value.toString()
                deliveryAddress.text = deliveryUser
            }

        })
        val ref: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                orderByName = snapshot.child("name").value.toString()
                orderByMobile = snapshot.child("phone").value.toString()
            }

        })}else{
            rl_cart_connection.visibility=View.GONE
            rl_retryCart.visibility=View.VISIBLE
        }




            val mRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(uid)
        mRef.child("address").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (addresses as ArrayList<ModelManageAddress>).clear()
                for (i in snapshot.children) {

                    val obj = ModelManageAddress(
                        i.child("id").value.toString(),
                        i.child("address").value.toString(),
                        i.child("city").value.toString(),
                        i.child("pinCode").value.toString(),
                        i.child("country").value.toString(),
                        i.child("state").value.toString(),
                        i.child("mobileNo").value.toString()

                    )
                    (addresses as ArrayList<ModelManageAddress>).add(obj)
                }
                addAddress.setOnClickListener {
                    rl_cart_connection.visibility=View.VISIBLE
                    rl_retryCart.visibility=View.GONE
                    if (ConnectionManager().checkConnectivity(this@Cart)){
                    val dialog = BottomSheetDialog(this@Cart)
                    val view =
                        LayoutInflater.from(this@Cart).inflate(R.layout.address_layout, null, false)
                    val rv: RecyclerView = view.findViewById(R.id.recycler_Address_dialog)
                    val btnAddNewAddress: Button = view.findViewById(R.id.btnAddNewAddress)
                    val txtCurrAddress: TextView = view.findViewById(R.id.txtCurrAddress)
                    val txtCurrMobile: TextView = view.findViewById(R.id.txtCurrMobile)
                    txtCurrAddress.text = deliveryUser
                    txtCurrMobile.text = "Mobile No- ${deliveryUserMobileNo}"
                    val layoutManager = LinearLayoutManager(this@Cart)
                    rv.layoutManager = layoutManager
                    val adapter = AdapterManageAddress(this@Cart, addresses)
                    rv.adapter = adapter
                    btnAddNewAddress.setOnClickListener {
                        startActivity(Intent(this@Cart, MapsActivity_New::class.java))
                        finish()
                    }
                    dialog.setContentView(view)
                    dialog.show()
                    dialog.setCancelable(false)
                    dialog.setCanceledOnTouchOutside(true)
                }else{
                        rl_cart_connection.visibility=View.GONE
                        rl_retryCart.visibility=View.VISIBLE
                    }
            }}
        })


    }

    override fun onBackPressed() {
        val intent = Intent(applicationContext, Home::class.java)
        startActivity(intent)
        finish()
    }
}