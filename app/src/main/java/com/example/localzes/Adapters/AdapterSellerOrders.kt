package com.example.localzes.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Modals.ModelOrderDetails
import com.example.localzes.OrdersDetailsSellerActivity
import com.example.localzes.R
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
        val totalItemsTv: TextView = view.findViewById(R.id.totalItemsTv)
        val paymentCOD: TextView = view.findViewById(R.id.txtCod1)
        val paymentPAID: TextView = view.findViewById(R.id.txtCod2)
        val orderedByName: TextView = view.findViewById(R.id.txtOrderedBy)
        val orderedByMobile: TextView = view.findViewById(R.id.txtMobileOrderedBy)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderSellerOrders {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_order_seller, parent, false)
        return HolderSellerOrders(
            view
        )
    }

    override fun getItemCount(): Int {
        return seller_orders.size
    }

    override fun onBindViewHolder(holder: HolderSellerOrders, position: Int) {
        val sellerOrders = seller_orders[position]
        holder.orderIdTv.text = "OD${sellerOrders.orderId}"
        holder.orderAmountTv.text = "Amount:- ₹.${sellerOrders.orderCost}"
        holder.orderStatusTv.text = sellerOrders.orderStatus
        holder.orderedByName.text = sellerOrders.orderByName
        holder.orderedByMobile.text = sellerOrders.orderByMobile
        if (sellerOrders.orderQuantity > 1.toString()) {

            holder.totalItemsTv.text = "${sellerOrders.orderQuantity} items"
        } else {
            holder.totalItemsTv.text = "${sellerOrders.orderQuantity} item"
        }
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
        val sdf = SimpleDateFormat("dd/MM/yyyy,hh:mm a")
        val date = Date(sellerOrders.orderTime.toLong())
        val formattedDate = sdf.format(date)
        holder.orderDateTv.text = formattedDate
        when (sellerOrders.paymentMode) {
            "Cash on Delivery" -> {
                holder.paymentPAID.visibility = View.GONE
                holder.paymentCOD.visibility = View.VISIBLE
            }
            "Paytm" -> {
                holder.paymentPAID.visibility = View.GONE
                holder.paymentCOD.visibility = View.VISIBLE
            }
            else -> {
                holder.paymentPAID.visibility = View.GONE
                holder.paymentCOD.visibility = View.GONE
            }
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(
                context,
                OrdersDetailsSellerActivity::class.java
            )
            intent.putExtra("orderIdTv", sellerOrders.orderId)
            intent.putExtra("orderByTv", sellerOrders.orderBy)
            context.startActivity(intent)
        }
    }
}