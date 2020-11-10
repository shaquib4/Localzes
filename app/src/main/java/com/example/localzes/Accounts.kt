package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_accounts.*
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.ArrayList

class Accounts : AppCompatActivity() {
    private lateinit var listViewAdapter: ExpandableListViewAdapter
    private lateinit var menu:List<String>
    private lateinit var auth: FirebaseAuth
    private lateinit var userDatabase: DatabaseReference
    private lateinit var item:HashMap<String,List<String>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts)
        bottom_navAcc.selectedItemId = R.id.nav_account
       auth= FirebaseAuth.getInstance()
        val user=auth.currentUser
        val uid=user!!.uid
        userDatabase= FirebaseDatabase.getInstance().reference.child("users").child(uid)
        userDatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    val user:ModelClass?=snapshot.getValue(ModelClass::class.java)
                    val phone:String?=user!!.getPhone()
                    val name:String?=user!!.getName()
                    val email:String?=user!!.getEmail()
                        txtaccmobile.text=phone.toString()
                        txtaccName.text=name.toString()
                        txtaccEmail.text=email.toString()


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }



        })
        bottom_navAcc.setOnNavigationItemSelectedListener { item ->


            when(item.itemId){

                R.id.nav_home->{
                    startActivity(Intent(this,Home::class.java))
                    overridePendingTransition(0,0)
                    finish()

                }
                R.id.nav_search->{
                    startActivity(Intent(this,Search::class.java))
                    overridePendingTransition(0,0)
                    finish()

                }
                R.id.nav_cart->{


                    startActivity(Intent(this,Cart::class.java))
                    overridePendingTransition(0,0)
                    finish()
                }
                R.id.nav_account->{

                    return@setOnNavigationItemSelectedListener true


                }
            }
            return@setOnNavigationItemSelectedListener true
        }
        showList()
        listViewAdapter= ExpandableListViewAdapter(this,menu,item)
        expand.setAdapter(listViewAdapter)
    }
    private fun showList(){

        menu= ArrayList()
        item= HashMap()

        (menu as ArrayList<String>).add("Account")
        (menu as ArrayList<String>).add("Refunds & Payments")
        (menu as ArrayList<String>).add("My Order")
        (menu as ArrayList<String>).add("Help")

        val item1:MutableList<String> = ArrayList()
        item1.add("Manage Address")
        item1.add("Favourites")
        item1.add("Offers")
        item1.add("Referrals")

        val item2:MutableList<String> = ArrayList()
        item2.add("Refund Status")
        item2.add("Payment Modes")
        item2.add("Transaction History")

        val item3:MutableList<String> = ArrayList()
        item3.add("Current Order")
        item3.add("Past Orders")
        val item4:MutableList<String> = ArrayList()

        item[menu[0]]=item1
        item[menu[1]]=item2
        item[menu[2]]=item3
        item[menu[3]]=item4
    }
}