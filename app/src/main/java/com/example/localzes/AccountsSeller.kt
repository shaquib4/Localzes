package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.localzes.Adapters.ExpandableListViewAdapterSeller
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_accounts.expand
import kotlinx.android.synthetic.main.activity_accounts_seller.*
import java.util.ArrayList

class AccountsSeller : AppCompatActivity() {
    private lateinit var listViewAdapter: ExpandableListViewAdapterSeller
    private lateinit var menu: List<String>
    private lateinit var item: HashMap<String, List<String>>
    private lateinit var userName: TextView
    private lateinit var userMobileNo: TextView
    private lateinit var userEmailAddress: TextView
    private lateinit var logOut: TextView
    private lateinit var userAuth:FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts_seller)
        userName=findViewById(R.id.txtaccName)
        userMobileNo=findViewById(R.id.txtaccmobile)
        userEmailAddress=findViewById(R.id.txtaccEmail)
        logOut=findViewById(R.id.txtaccEdit)
        userAuth= FirebaseAuth.getInstance()
        val user=userAuth.currentUser
        val uid=user!!.uid
        databaseReference= FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        databaseReference.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val userN=snapshot.child("name").value.toString()
                val userM=snapshot.child("phone").value.toString()
                val userE=snapshot.child("email").value.toString()
                userName.text=userN
                userMobileNo.text=userM
                userEmailAddress.text=userE
            }

        })
        logOut.setOnClickListener {
            userAuth.signOut()
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        showList()
        listViewAdapter =
            ExpandableListViewAdapterSeller(
                this,
                menu,
                item
            )
        expand.setAdapter(listViewAdapter)
        expand.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            val select=listViewAdapter.getChild(groupPosition, childPosition) as String
            when(select){
                "Offers"->{}
                "Referrals"->{}
                "Refunds Initiated"->{}
                "Transaction History"->{}
                "FAQs"->{}
                "Contact Us"->{}
            }
            return@setOnChildClickListener true
        }

        bottom_navAcc_seller.selectedItemId = R.id.nav_account_seller
        bottom_navAcc_seller.setOnNavigationItemSelectedListener { item ->


            when (item.itemId) {


                R.id.nav_product_seller -> {

                    startActivity(Intent(this, Seller_Products::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_order_seller -> {

                    startActivity(Intent(this, OrdersSeller::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_account_seller -> {

                   return@setOnNavigationItemSelectedListener true

                }
                R.id.nav_store_seller -> {

                    startActivity(Intent(this, Home_seller::class.java))
                    overridePendingTransition(0, 0)
                    finish()


                }
                R.id.nav_category_seller -> {

                    startActivity(Intent(this, Category::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
            }
            return@setOnNavigationItemSelectedListener false}
    }
    private fun showList() {

        menu = ArrayList()
        item = HashMap()

        (menu as ArrayList<String>).add("Account")
        (menu as ArrayList<String>).add("Refunds & Payments")
        (menu as ArrayList<String>).add("Help")

        val item1: MutableList<String> = ArrayList()
        item1.add("Offers")
        item1.add("Referrals")

        val item2: MutableList<String> = ArrayList()
        item2.add("Refunds Initiated")
        item2.add("Transaction History")

        val item3: MutableList<String> = ArrayList()
        item3.add("FAQs")
        item3.add("Contact Us")


        item[menu[0]] = item1
        item[menu[1]] = item2
        item[menu[2]] = item3

    }
}