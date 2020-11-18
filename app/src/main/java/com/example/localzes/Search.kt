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
    private lateinit var searchAdapter: AdapterSearchItem
    private lateinit var searchItem: List<Upload>
    private lateinit var searchAct:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        searchAct=findViewById(R.id.search_act)
        recyclerSearchItem=findViewById(R.id.recycler_search_item)
        recyclerSearchItem.setHasFixedSize(true)
        recyclerSearchItem.layoutManager=LinearLayoutManager(this)
        searchItem=ArrayList<Upload>()

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
                        i.child("upi").value.toString()
                    )
                    (searchItem as ArrayList<Upload>).add(obj)

                }
                 searchAdapter=AdapterSearchItem(this@Search,searchItem)


            }

        })
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
                        i.child("upi").value.toString()
                    )
                    (searchItem as ArrayList<Upload>).add(obj)

                }
                searchAdapter=AdapterSearchItem(this@Search,searchItem)
                recyclerSearchItem.adapter=searchAdapter

            }
        })
    }
}