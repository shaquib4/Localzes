package com.localze.android.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.localze.android.R

class ExpandableListViewAdapterSeller internal constructor(private val context: Context, private val menu: List<String>, private  val item:HashMap<String,List<String>>):
    BaseExpandableListAdapter() {
    override fun getGroup(groupPosition: Int): Any {
        return menu[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    @SuppressLint("SetTextI18n")
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView=convertView
        val menuTitle=getGroup(groupPosition) as String

        if (convertView==null){
            val inflater=context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.menu_account,null)
        }
        val menuTv=convertView!!.findViewById<TextView>(R.id.txtmenu)
        val menuitem=convertView!!.findViewById<TextView>(R.id.txtitemmenu)

        menuTv.text=menuTitle
/*            menuitem.text = "Offers, Referrals"

        }*/
        if (menuTitle=="Refunds & Payments"){
            menuitem.text = "Refund Initiated, Transaction history"
        }

        else if (menuTitle=="Help"){
            menuitem.text = "FAQs, Contact us"
        }else if(menuTitle=="Switch"){
            menuitem.text="To Main Seller, Staff"
        }
        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return this.item[this.menu[groupPosition]]!!.size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return this.item[this.menu[groupPosition]]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        var convertView=convertView
        var itemTitles=getChild(groupPosition,childPosition) as String
        var menuTitle=getGroup(groupPosition) as String


        if (convertView==null){
            val inflater=context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.items_menu,null)
        }
        var itemTv=convertView!!.findViewById<TextView>(R.id.txtitem)
        var img=convertView!!.findViewById<ImageView>(R.id.imgitem)

        itemTv.text = itemTitles
       /*  if (itemTitles=="Offers"&& menuTitle=="Account")
        {
            img.setImageResource(R.drawable.ic_offer_vc)
        }else if (itemTitles=="Referrals"&& menuTitle=="Account")
        {
            img.setImageResource(R.drawable.ic_referrals)
        }*/ if (itemTitles=="Refund Initiated"&& menuTitle=="Refunds & Payments")
        {
            img.setImageResource(R.drawable.ic_refundstatus)
        }else if (itemTitles=="Transaction History"&& menuTitle=="Refunds & Payments")
        {
            img.setImageResource(R.drawable.ic_refundstatus)
        }/*else if (itemTitles=="FAQs"&& menuTitle=="Help"){
             img.setImageResource(R.drawable.ic_offer_vc)
         }*/else if(itemTitles=="Contact Us"&& menuTitle=="Help"){
             img.setImageResource(R.drawable.ic_referrals)
         }else if (itemTitles=="To Main Seller"&&menuTitle=="Switch"){
            img.setImageResource(R.drawable.ic_referrals)
        }else if (itemTitles=="As Staff Of Shop"&&menuTitle=="Switch"){
            img.setImageResource(R.drawable.ic_referrals)
        }

        return convertView
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return menu.size
    }
}