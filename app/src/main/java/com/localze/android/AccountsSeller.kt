package com.localze.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.localze.android.Adapters.ExpandableListViewAdapterSeller
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_accounts.expand
import kotlinx.android.synthetic.main.activity_accounts_seller.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.set

class AccountsSeller : AppCompatActivity() {
    private lateinit var listViewAdapter: ExpandableListViewAdapterSeller
    private lateinit var menu: List<String>
    private lateinit var item: HashMap<String, List<String>>
    private lateinit var userName: TextView
    private lateinit var userMobileNo: TextView
    private lateinit var userEmailAddress: TextView
    private lateinit var logOut: TextView
    private lateinit var userAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storeStatus: TextView
    private lateinit var switchStoreStatus: SwitchCompat
    private lateinit var privacySeller: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts_seller)
        userName = findViewById(R.id.txtaccName)
        userMobileNo = findViewById(R.id.txtaccmobile)
        userEmailAddress = findViewById(R.id.txtaccEmail)
        storeStatus = findViewById(R.id.storeOpen)

        switchStoreStatus = findViewById(R.id.switchOpen)
        privacySeller = findViewById(R.id.txtPrivacySeller)
        logOut = findViewById(R.id.txtaccEdit)
        userAuth = FirebaseAuth.getInstance()
        val user = userAuth.currentUser
        val uid = user!!.uid
        databaseReference = FirebaseDatabase.getInstance().reference.child("seller")
        databaseReference.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (!(snapshot.child("staffOfShop")
                        .exists()) || snapshot.child("staffOfShop").value.toString() == ""
                ) {
                    accountsMainSeller(uid)
                } else {
                    rl_Status.visibility = View.GONE
                    accountsStaff(uid)
                }
            }
        })


        databaseReference.child(uid).addValueEventListener(object : ValueEventListener {
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
        privacySeller.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://localze.flycricket.io/privacy.html")
            val chooser = Intent.createChooser(intent, "Open With")
            startActivity(chooser)
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

                    startActivity(Intent(this, cardBanners::class.java))
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
            return@setOnNavigationItemSelectedListener false
        }
    }

    private fun accountsStaff(uid: String) {
        showList()
        listViewAdapter =
            ExpandableListViewAdapterSeller(
                this,
                menu,
                item
            )
        expand.setAdapter(listViewAdapter)
        expand.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            val select = listViewAdapter.getChild(groupPosition, childPosition) as String
            when (select) {
                "Offers" -> {
                }
                "Referrals" -> {
                }
                "Account Details" -> {
                }
                "Transaction History" -> {
                }
                "Address" -> {
                    startActivity(Intent(this, AddressSellerEdit::class.java))
                    finish()
                }
                "Contact Us" -> {
                    val intent = Intent(this, HelpSectionActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                "To Main Seller" -> {
                    val intent = Intent(this, Home_seller::class.java)
                    val databaseReference =
                        FirebaseDatabase.getInstance().reference.child("seller").child(uid)
                    val headers = HashMap<String, Any>()
                    headers["staffOfShop"] = ""
                    headers["StoreStatus"] = "OPEN"
                    databaseReference.updateChildren(headers).addOnSuccessListener {
                        startActivity(intent)
                        finish()
                    }
                }
                "As Staff Of Shop" -> {
                    val intent = Intent(this, AsStaffOf::class.java)
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

        // (menu as ArrayList<String>).add("Account")
        (menu as ArrayList<String>).add("Payments")
        (menu as ArrayList<String>).add("Help")
        (menu as ArrayList<String>).add("Switch")

        //  val item1: MutableList<String> = ArrayList()
        //item1.add("Offers")
        //item1.add("Referrals")

        val item1: MutableList<String> = ArrayList()
        item1.add("Account Details")
        item1.add("Transaction History")

        val item2: MutableList<String> = ArrayList()
        //item2.add("FAQs")
        item2.add("Contact Us")
        item2.add("Address")

        val item3: MutableList<String> = ArrayList()
        item3.add("To Main Seller")
        item3.add("As Staff Of Shop")


        //item[menu[0]] = item1
        item[menu[0]] = item1
        item[menu[1]] = item2
        item[menu[2]] = item3

    }

    private fun accountsMainSeller(uid: String) {
        databaseReference.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("StoreStatus").value.toString() == "OPEN") {
                    switchStoreStatus.isChecked = true
                    storeStatus.text = snapshot.child("StoreStatus").value.toString()
                } else if (snapshot.child("StoreStatus").value.toString() == "CLOSED") {
                    switchStoreStatus.isChecked = false
                    storeStatus.text = snapshot.child("StoreStatus").value.toString()
                }
            }
        })
        switchStoreStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val headers = HashMap<String, Any>()
                headers["StoreStatus"] = "OPEN"
                val reference = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
                reference.updateChildren(headers)
                storeStatus.text = "OPEN"
            } else {
                val headers = HashMap<String, Any>()
                headers["StoreStatus"] = "CLOSED"
                val reference = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
                reference.updateChildren(headers)
                storeStatus.text = "CLOSED"
            }
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
            val select = listViewAdapter.getChild(groupPosition, childPosition) as String
            when (select) {
                "Offers" -> {
                }
                "Referrals" -> {
                }
                "Account Details" -> {
                    val intent = Intent(this, BankDetailsActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                "Transaction History" -> {
                    val intent=Intent(this, IncomeType::class.java)
                    startActivity(intent)
                    finish()
                }
                "FAQs" -> {
                }
                "Address" -> {
                    val intent = Intent(this, AddressSellerEdit::class.java)
                    startActivity(intent)
                    finish()
                }
                "Contact Us" -> {
                    val intent = Intent(this, ContactUsSeller::class.java)
                    startActivity(intent)
                    finish()
                }
                "To Main Seller" -> {
                    val intent = Intent(this, Home_seller::class.java)
                    val headers = HashMap<String, Any>()
                    headers["staffOfShop"] = ""
                    headers["StoreStatus"] = "OPEN"
                    databaseReference.child(uid).updateChildren(headers).addOnSuccessListener {
                        startActivity(intent)
                        finish()
                    }
                }
                "As Staff Of Shop" -> {
                    val intent = Intent(this, AsStaffOf::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            return@setOnChildClickListener true
        }
    }


    override fun onBackPressed() {
        val intent = Intent(applicationContext, Home_seller::class.java)
        startActivity(intent)
        finish()
    }
}