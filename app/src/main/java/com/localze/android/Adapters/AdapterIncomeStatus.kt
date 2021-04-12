package com.localze.android.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.localze.android.Modals.ModalIncomeStatus
import com.localze.android.R
import java.text.SimpleDateFormat
import java.util.*

class AdapterIncomeStatus(val context: Context, val listIncome: List<ModalIncomeStatus>) :
    RecyclerView.Adapter<AdapterIncomeStatus.HolderIncomeStatus>() {
    class HolderIncomeStatus(view: View) : RecyclerView.ViewHolder(view) {
        val orderPrice: TextView = view.findViewById(R.id.PaidAmount)
        val payeeName: TextView = view.findViewById(R.id.PayeeNAme)
        val transferStatus: TextView = view.findViewById(R.id.Status)
        val transferId: TextView = view.findViewById(R.id.trnfID)
        val trnfTime: TextView = view.findViewById(R.id.Date)
        val settlementDate: TextView = view.findViewById(R.id.txtSettleDate)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderIncomeStatus {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_income_status, parent, false)
        return HolderIncomeStatus(view)
    }

    override fun getItemCount(): Int {
        return listIncome.size
    }

    override fun onBindViewHolder(holder: HolderIncomeStatus, position: Int) {
        val list_Income = listIncome[position]
        holder.orderPrice.text = "₹${list_Income.orderCost}"
        holder.payeeName.text = list_Income.payeeName
        val sdf = SimpleDateFormat("dd/MM/yyyy,hh:mm a")
        val date = Date(list_Income.orderTime.toLong())
        val formattedDate = sdf.format(date)
        holder.trnfTime.text = formattedDate
        when (list_Income.payWith) {
            "Cash On Delivery" -> {
                holder.transferStatus.text = "Amount collected by COD"
                holder.transferId.text = "------"
                holder.settlementDate.text = formattedDate
            }
            "Paytm" -> {
                holder.transferStatus.text = "TransferSuccessful"
                holder.transferId.text = "------"
                holder.settlementDate.text = formattedDate
            }
            "Razorpay" -> {
                if (list_Income.settlementId == "" || list_Income.settlementId == null) {
                    holder.transferStatus.text = "TransferInProgress"
                    holder.transferId.text = list_Income.transferId
                    holder.settlementDate.text = "Amount to be settled in two business days"
                } else {
                    holder.transferStatus.text = "TransferSuccessful"
                    holder.transferId.text = list_Income.transferId
                    holder.settlementDate.text = "-------"
                }
            }
        }
    }
}