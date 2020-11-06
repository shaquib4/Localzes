package com.example.localzes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterManageAddress(val context: Context, private val address_manage:List<ModelManageAddress>) : RecyclerView.Adapter<AdapterManageAddress.HolderManageAddress>() {
    class HolderManageAddress(view: View) :RecyclerView.ViewHolder(view) {
        val city:TextView=view.findViewById(R.id.txtHome)
        val address:TextView=view.findViewById(R.id.txtHome)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderManageAddress {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.address_single_row,parent,false)
        return HolderManageAddress(view)
    }

    override fun getItemCount(): Int {
        return address_manage.size
    }

    override fun onBindViewHolder(holder: HolderManageAddress, position: Int) {
     val address_user=address_manage[position]
      holder.city.text=address_user.city
      holder.address.text=address_user.address
    }
}