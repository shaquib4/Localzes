package com.example.localzes

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AdapterManageAddress(val context: Context, private val address_manage:List<ModelManageAddress>) : RecyclerView.Adapter<AdapterManageAddress.HolderManageAddress>() {
    class HolderManageAddress(view: View) :RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderManageAddress {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: HolderManageAddress, position: Int) {
        TODO("Not yet implemented")
    }
}