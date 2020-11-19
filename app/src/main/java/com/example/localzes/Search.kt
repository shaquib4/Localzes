package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_search.*

class Search : AppCompatActivity() {
    private lateinit var userDatabase: DatabaseReference
    private lateinit var recyclerSearchItem: RecyclerView
    private lateinit var searchProductAdapter:AdapterSearchProductItem
    private lateinit var searchAdapter: AdapterSearchItem
    private lateinit var searchProductItem:  List<ModelAddProduct>
    private lateinit var searchItem: List<Upload>
    private lateinit var searchAct:EditText
    private lateinit var search:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        searchAct=findViewById(R.id.search_act)
        recyclerSearchItem=findViewById(R.id.recycler_search_item)
         search=findViewById(R.id.search)
        recyclerSearchItem.layoutManager=LinearLayoutManager(this)
        searchItem=ArrayList<Upload>()
        searchProductItem=ArrayList<ModelAddProduct>()

        bottom_navSearch.selectedItemId = R.id.nav_search
        bottom_navSearch.setOnNavigationItemSelectedListener { item ->


            when(item.itemId){

                R.id.nav_home->{
                    startActivity(Intent(this,Home::class.java))
                    overridePendingTransition(0,0)
                    finish()

                }
                R.id.nav_search->{

                    return@setOnNavigationItemSelectedListener true

                }
                R.id.nav_cart->{


                    startActivity(Intent(this,Cart::class.java))
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.nav_account->{

                    startActivity(Intent(this,Accounts::class.java))
                    overridePendingTransition(0,0)
                    finish()

                }
            }
            return@setOnNavigationItemSelectedListener true
        }
        userDatabase = FirebaseDatabase.getInstance().reference.child("seller")
        userDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (searchItem as ArrayList<Upload>).clear()
                for (i in snapshot.children) {

                    val obj = Upload(
                        i.child("shopId").value.toString(),
                        i.child("phone").value.toString(),
                        i.child("name").value.toString(),
                        i.child("email").value.toString(),
                        i.child("address").value.toString(),
                        i.child("shop_name").value.toString(),
                        i.child("imageUrl").value.toString(),
                        i.child("category1").value.toString(),
                        i.child("upi").value.toString(),
                        i.child("locality").value.toString(),
                        i.child("city").value.toString(),
                        i.child("pinCode").value.toString(),
                        i.child("state").value.toString(),
                        i.child("country").value.toString()
                    )
                    (searchItem as ArrayList<Upload>).add(obj)

                }
                 searchAdapter=AdapterSearchItem(this@Search,searchItem)


            }

        })

        btnLocality.setOnClickListener {
            searchAct.visibility=View.VISIBLE
            search.visibility=View.GONE
            btnLocality.setTextColor(this.resources.getColor(R.color.white))
            btnLocality.setBackgroundColor(this.resources.getColor(R.color.colorPrimary))
            btnShop.setTextColor(this.resources.getColor(R.color.black))
            btnShop.setBackgroundColor(this.resources.getColor(R.color.white))
            btnProduct.setTextColor(this.resources.getColor(R.color.black))
            btnProduct.setBackgroundColor(this.resources.getColor(R.color.white))
            searchAct.addTextChangedListener(object : TextWatcher{
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(cs: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    searchLocality(cs.toString().toLowerCase())
                }
            })
        }
        btnShop.setOnClickListener {
            searchAct.visibility=View.VISIBLE
            search.visibility=View.GONE
            btnShop.setTextColor(this.resources.getColor(R.color.white))
            btnShop.setBackgroundColor(this.resources.getColor(R.color.colorPrimary))
            btnLocality.setTextColor(this.resources.getColor(R.color.black))
            btnLocality.setBackgroundColor(this.resources.getColor(R.color.white))
            btnProduct.setTextColor(this.resources.getColor(R.color.black))
            btnProduct.setBackgroundColor(this.resources.getColor(R.color.white))
            searchAct.addTextChangedListener(object : TextWatcher{
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(cs: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    searchShops(cs.toString().toLowerCase())
                }
            })
        }

        btnProduct.setOnClickListener {

            search.visibility=View.VISIBLE
            btnProduct.setTextColor(this.resources.getColor(R.color.white))
            btnProduct.setBackgroundColor(this.resources.getColor(R.color.colorPrimary))
            btnShop.setTextColor(this.resources.getColor(R.color.black))
            btnShop.setBackgroundColor(this.resources.getColor(R.color.white))
            btnLocality.setTextColor(this.resources.getColor(R.color.black))
            btnLocality.setBackgroundColor(this.resources.getColor(R.color.white))
            search.addTextChangedListener(object : TextWatcher{
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(cs: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    searchProducts(cs.toString().toLowerCase())
                }
            })

        }

    }

    private fun searchProducts(string:String) {
        (searchProductItem as ArrayList<ModelAddProduct>).clear()
       val userDatabases = FirebaseDatabase.getInstance().reference.child("seller")
        userDatabases.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {


                for (i in snapshot.children) {
                    val shopId= i.child("shopId").value.toString()



                    val queryProductItem = FirebaseDatabase.getInstance().reference.child("seller").child(shopId).child("Products").orderByChild("title")
                        .startAt(string)
                        .endAt(string + "\uf8ff")
                    queryProductItem.addValueEventListener(object : ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(snapshot: DataSnapshot) {

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
                                    i.child("quantity").value.toString()
                                )
                                (searchProductItem as ArrayList<ModelAddProduct>).add(obj)

                            }
                            searchProductAdapter=AdapterSearchProductItem(this@Search,searchProductItem)
                            recyclerSearchItem.adapter=searchProductAdapter
                            //userProductAdapter = AdapterUserProducts(this@UserProductsActivity, mUserProducts)
                            //recyclerUserProduct.adapter = userProductAdapter
                        }
                    })
                }
            }
        })
    }

    private fun searchShops(str:String){
        val queryShop = FirebaseDatabase.getInstance().reference.child("seller").orderByChild("shop_name")
            .startAt(str)
            .endAt(str + "\uf8ff")

        queryShop.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (searchItem as ArrayList<Upload>).clear()
                for (i in snapshot.children) {

                    val obj = Upload(
                        i.child("shopId").value.toString(),
                        i.child("phone").value.toString(),
                        i.child("name").value.toString(),
                        i.child("email").value.toString(),
                        i.child("address").value.toString(),
                        i.child("shop_name").value.toString(),
                        i.child("imageUrl").value.toString(),
                        i.child("category1").value.toString(),
                        i.child("upi").value.toString(),
                        i.child("locality").value.toString(),
                        i.child("city").value.toString(),
                        i.child("pinCode").value.toString(),
                        i.child("state").value.toString(),
                        i.child("country").value.toString()
                    )
                    (searchItem as ArrayList<Upload>).add(obj)

                }
                searchAdapter=AdapterSearchItem(this@Search,searchItem)
                recyclerSearchItem.adapter=searchAdapter

            }
        })
    }
    private fun searchLocality(str:String){
        val queryShop = FirebaseDatabase.getInstance().reference.child("seller").orderByChild("locality")
            .startAt(str)
            .endAt(str + "\uf8ff")

        queryShop.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (searchItem as ArrayList<Upload>).clear()
                for (i in snapshot.children) {

                    val obj = Upload(
                        i.child("shopId").value.toString(),
                        i.child("phone").value.toString(),
                        i.child("name").value.toString(),
                        i.child("email").value.toString(),
                        i.child("address").value.toString(),
                        i.child("shop_name").value.toString(),
                        i.child("imageUrl").value.toString(),
                        i.child("category1").value.toString(),
                        i.child("upi").value.toString(),
                        i.child("locality").value.toString(),
                        i.child("city").value.toString(),
                        i.child("pinCode").value.toString(),
                        i.child("state").value.toString(),
                        i.child("country").value.toString()
                    )
                    (searchItem as ArrayList<Upload>).add(obj)

                }
                searchAdapter=AdapterSearchItem(this@Search,searchItem)
                recyclerSearchItem.adapter=searchAdapter

            }
        })
    }
}