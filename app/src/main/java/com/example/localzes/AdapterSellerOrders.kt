package com.example.localzes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterSellerOrders(val context:Context,private val seller_orders:List<ModelOrderDetails>):RecyclerView.Adapter<AdapterSellerOrders.HolderSellerOrders>() {
    class HolderSellerOrders (view: View):RecyclerView.ViewHolder(view){
        val orderIdTv:TextView=view.findViewById(R.id.orderIdTv)
        val orderDateTv:TextView=view.findViewById(R.id.orderDateTv)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderSellerOrders {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.row_order_seller,parent,false)
        return HolderSellerOrders(view)
    }

    override fun getItemCount(): Int {
        return seller_orders.size
    }

    override fun onBindViewHolder(holder: HolderSellerOrders, position: Int) {

    }
}