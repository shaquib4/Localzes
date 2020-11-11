package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ExpandableListView
import kotlinx.android.synthetic.main.activity_accounts.*
import java.util.ArrayList

class Accounts : AppCompatActivity() {
    private lateinit var listViewAdapter: ExpandableListViewAdapter
    private lateinit var menu: List<String>
    private lateinit var item: HashMap<String, List<String>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts)
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
        listViewAdapter = ExpandableListViewAdapter(this, menu, item)
        expand.setAdapter(listViewAdapter)
        expand.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            val select = listViewAdapter.getChild(groupPosition, childPosition) as String
            if (select == "Current Order") {
                val intent = Intent(this, CurrentOrdersActivity::class.java)
                startActivity(intent)
            } else if (select == "Past Orders") {
                val intent=Intent(this,PastOrdersActivity::class.java)
                startActivity(intent)
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
}