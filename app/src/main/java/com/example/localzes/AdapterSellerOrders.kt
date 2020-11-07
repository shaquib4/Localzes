package com.example.localzes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class AdapterSellerOrders(
    val context: Context,
    private val seller_orders: List<ModelOrderDetails>
) : RecyclerView.Adapter<AdapterSellerOrders.HolderSellerOrders>() {
    class HolderSellerOrders(view: View) : RecyclerView.ViewHolder(view) {
        val orderIdTv: TextView = view.findViewById(R.id.orderIdTv)
        val orderDateTv: TextView = view.findViewById(R.id.orderDateTv)
        val orderAmountTv: TextView = view.findViewById(R.id.orderAmountTv)
        val orderStatusTv: TextView = view.findViewById(R.id.orderStatusTv)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderSellerOrders {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_order_seller, parent, false)
        return HolderSellerOrders(view)
    }

    override fun getItemCount(): Int {
        return seller_orders.size
    }

    override fun onBindViewHolder(holder: HolderSellerOrders, position: Int) {
        val sellerOrders = seller_orders[position]
        holder.orderIdTv.text = "OD${sellerOrders.orderId}"
        holder.orderAmountTv.text = "Amount:- Rs.${sellerOrders.orderCost}"
        holder.orderStatusTv.text = sellerOrders.orderStatus
        when (sellerOrders.orderStatus) {
            "In Progress" -> {
                holder.orderStatusTv.setTextColor(context.resources.getColor(R.color.green))
            }
            "Confirmed" -> {
                holder.orderStatusTv.setTextColor(context.resources.getColor(R.color.acidGreen))
            }
            "Cancelled" -> {
                holder.orderStatusTv.setTextColor(context.resources.getColor(R.color.red))
            }
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a")
        val date= Date(sellerOrders.orderTime.toLong())
        val formattedDate=sdf.format(date)
        holder.orderDateTv.text=formattedDate
        holder.itemView.setOnClickListener {
            
        }
    }
}