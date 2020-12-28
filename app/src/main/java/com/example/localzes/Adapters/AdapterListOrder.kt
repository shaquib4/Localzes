package com.example.localzes.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.ListOrderDetailSeller
import com.example.localzes.Modals.ModalSellerOrderList
import com.example.localzes.OrdersDetailsSellerActivity
import com.example.localzes.R
import java.text.SimpleDateFormat
import java.util.*

class AdapterListOrder(val context: Context,val modelSellerList:List<ModalSellerOrderList>)
    :RecyclerView.Adapter<AdapterListOrder.HolderListOrder>() {
    class HolderListOrder(view:View):RecyclerView.ViewHolder(view) {
        val totalItem:TextView=view.findViewById(R.id.txt_totalItems)
        val totalCost:TextView=view.findViewById(R.id.txt_orderAmountTv)
        val oderBy:TextView=view.findViewById(R.id.txt_OrderedBy)
        val cuMobileNum:TextView=view.findViewById(R.id.txt_MobileOrderedBy)
        val orderId:TextView=view.findViewById(R.id.txt_orderIdTv)
        val cod:TextView=view.findViewById(R.id.txt_Cod1)
        val paid:TextView=view.findViewById(R.id.txt_Cod2)
        val orderStatus:TextView=view.findViewById(R.id.txt_orderStatusTv)
        val orderTime:TextView=view.findViewById(R.id.txt_orderDateTv)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderListOrder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_list_order, parent, false)
           return AdapterListOrder.HolderListOrder(view)
    }

    override fun getItemCount(): Int {
      return  modelSellerList.size
    }

    override fun onBindViewHolder(holder: HolderListOrder, position: Int) {
       val sellerOrder=modelSellerList[position]
        holder.totalItem.text=sellerOrder.totalItems
        holder.totalCost.text=sellerOrder.orderCost
        holder.oderBy.text=sellerOrder.orderBy
        holder.orderId.text=sellerOrder.orderId
        holder.orderStatus.text=sellerOrder.orderStatus
        val sdf = SimpleDateFormat("dd/MM/yyyy,hh:mm a")
        val date = Date(sellerOrder.orderTime.toLong())
        val formattedDate = sdf.format(date)
        holder.orderTime.text=formattedDate

        holder.itemView.setOnClickListener {
            val intent = Intent(
                context,
                ListOrderDetailSeller::class.java
            )
            intent.putExtra("orderId",sellerOrder.orderId)
            intent.putExtra("orderBy",sellerOrder.orderBy)
            context.startActivity(intent)
        }
    }
}