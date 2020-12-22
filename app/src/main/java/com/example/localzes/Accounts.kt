package com.example.localzes

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.localzes.Adapters.ExpandableListViewAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_accounts.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.set

class Accounts : AppCompatActivity() {
    private lateinit var listViewAdapter: ExpandableListViewAdapter
    private lateinit var menu: List<String>
    private lateinit var item: HashMap<String, List<String>>
    private lateinit var userName: TextView
    private lateinit var userMobileNo: TextView
    private lateinit var userEmailAddress: TextView
    private lateinit var logOut: TextView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts)
        userName = findViewById(R.id.txtaccName)
        userMobileNo = findViewById(R.id.txtaccmobile)
        userEmailAddress = findViewById(R.id.txtaccEmail)
        logOut = findViewById(R.id.txtaccEdit)
        userAuth = FirebaseAuth.getInstance()
        val user = userAuth.currentUser
        val uid = user!!.uid
        databaseReference = FirebaseDatabase.getInstance().reference.child("users").child(uid)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val userN = snapshot.child("name").value.toString()
                val userM = snapshot.child("phone").value.toString()
                val userE = snapshot.child("email").value.toString()
                userName.text = userN
                userMobileNo.text = userM
                userEmailAddress.text = userE
            }

        })
        logOut.setOnClickListener {
            userAuth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        bottom_navAcc.selectedItemId = R.id.nav_account
        bottom_navAcc.setOnNavigationItemSelectedListener { item ->


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


                    startActivity(Intent(this, Cart::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.nav_account -> {

                    return@setOnNavigationItemSelectedListener true


                }
            }
            return@setOnNavigationItemSelectedListener true
        }
        showList()
        listViewAdapter =
            ExpandableListViewAdapter(
                this,
                menu,
                item
            )
        expand.setAdapter(listViewAdapter)
        expand.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            val select = listViewAdapter.getChild(groupPosition, childPosition) as String
            when (select) {
                "Current Order" -> {
                    val intent = Intent(this, CurrentOrdersActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                "Past Orders" -> {
                    val intent = Intent(this, PastOrdersActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                "Manage Address" -> {
                    val intent = Intent(this, ManageAddress::class.java)
                    startActivity(intent)
                    finish()
                }
                "Favourites" -> {
                    val intent = Intent(this, PagerActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            return@setOnChildClickListener true
        }
    }

    private fun showList() {

        menu = ArrayList()
        item = HashMap()

        (menu as ArrayList<String>).add("Account")
        (menu as ArrayList<String>).add("Refunds & Payments")
        (menu as ArrayList<String>).add("My Order")
        (menu as ArrayList<String>).add("Help")

        val item1: MutableList<String> = ArrayList()
        item1.add("Manage Address")
        item1.add("Favourites")
        item1.add("Offers")
        item1.add("Referrals")

        val item2: MutableList<String> = ArrayList()
        item2.add("Refund Status")
        item2.add("Payment Modes")
        item2.add("Transaction History")

        val item3: MutableList<String> = ArrayList()
        item3.add("Current Order")
        item3.add("Past Orders")
        val item4: MutableList<String> = ArrayList()

        item[menu[0]] = item1
        item[menu[1]] = item2
        item[menu[2]] = item3
        item[menu[3]] = item4
    }

    override fun onBackPressed() {
        val intent = Intent(applicationContext, Home::class.java)
        startActivity(intent)
        finish()
    }
}