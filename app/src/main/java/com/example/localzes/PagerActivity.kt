package com.example.localzes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.view_pager.*

class PagerActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pager)

        val adapter = MyViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(SHOP() , "SHOP")
        adapter.addFragment(ITEM() , "ITEM")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)
    }

    class MyViewPagerAdapter (manager: FragmentManager) : FragmentPagerAdapter(manager) {

        private val fragmentList : MutableList<Fragment> = ArrayList()
        private val tittleList : MutableList<String> = ArrayList()

        override fun getItem(position: Int): Fragment {
         return fragmentList[position]
        }
        override fun getCount(): Int {
            return fragmentList.size
        }

        fun addFragment(fragment: Fragment, tittle:String){
             fragmentList.add(fragment)
            tittleList.add(tittle)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return tittleList[position]
        }

    }

    override fun onBackPressed() {
        val intent= Intent(this,AccountsSeller::class.java)
        startActivity(intent)
        finish()
    }

}