package com.example.localzes.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.localzes.R

class ExpandableListViewAdapter internal constructor(private val context: Context, private val menu: List<String>,private  val item:HashMap<String,List<String>>):
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
        if (menuTitle=="Account"){
            menuitem.text = "Addresses, Favourites"

        }else if (menuTitle=="Refunds & Payments"){
            menuitem.text = "Refund status, Payment modes"
        }
        else if (menuTitle=="My Order"){
            menuitem.text = "Current orders,Past orders"
        }
        else if (menuTitle=="Help"){
            menuitem.text = "Contact us"
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
  if (itemTitles=="Manage Address"&& menuTitle=="Account"){
      img.setImageResource(R.drawable.ic_location_vc)
  }else if (itemTitles=="Favourites"&& menuTitle=="Account")
  {
      img.setImageResource(R.drawable.ic_favorite_black_24dp)
  }/*else if (itemTitles=="Offers"&& menuTitle=="Account")
  {
      img.setImageResource(R.drawable.ic_offer_vc)
  }else if (itemTitles=="Referrals"&& menuTitle=="Account")
  {
      img.setImageResource(R.drawable.ic_referrals)
  }*/else if (itemTitles=="Refund Status"&& menuTitle=="Refunds & Payments")
  {
      img.setImageResource(R.drawable.ic_refundstatus)
  }else if (itemTitles=="Payment Modes"&& menuTitle=="Refunds & Payments")
  {
      img.setImageResource(R.drawable.ic_paymentmode)
  }/*else if (itemTitles=="Transaction History"&& menuTitle=="Refunds & Payments")
  {
      img.setImageResource(R.drawable.ic_refundstatus)
  }*/else if (itemTitles=="Current Order"&& menuTitle=="My Order")
  {
      img.setImageResource(R.drawable.ic_shopping_cart_black_24dp)
  }else if (itemTitles=="Past Orders"&& menuTitle=="My Order")
  {
      img.setImageResource(R.drawable.ic_pastorder)
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