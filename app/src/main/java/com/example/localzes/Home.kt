package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterUserShops
import com.example.localzes.Modals.Upload
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*

class Home : AppCompatActivity() {
    private lateinit var userDatabase: DatabaseReference
    var firebaseUser: FirebaseUser? = null
    private lateinit var shops:List<Upload>
    private lateinit var recyclerShopUser:RecyclerView
    private lateinit var userShopAdapter: AdapterUserShops
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        progress_home.visibility = View.VISIBLE
        recyclerShopUser=findViewById(R.id.recycler_shop_user)
        recyclerShopUser.layoutManager=LinearLayoutManager(this)



        firebaseUser = FirebaseAuth.getInstance().currentUser
        userDatabase = FirebaseDatabase.getInstance().reference.child("seller")
        shops=ArrayList<Upload>()


        bottom_navProducts.selectedItemId = R.id.nav_home
        bottom_navProducts.setOnNavigationItemSelectedListener { item ->


            when (item.itemId) {

                R.id.nav_home -> {

                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_search -> {

                    startActivity(Intent(this, Search::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_cart -> {

                    startActivity(Intent(this, Cart::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_account -> {

                    startActivity(Intent(this, Accounts::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
            }
            return@setOnNavigationItemSelectedListener false
        }
        userDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (shops as ArrayList<Upload>).clear()
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
                    (shops as ArrayList<Upload>).add(obj)
                    progress_home.visibility=View.GONE
                }
                userShopAdapter= AdapterUserShops(
                    this@Home,
                    shops
                )
                recyclerShopUser.adapter=userShopAdapter
            }

        })


    }

}