package com.localze.android.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.localze.android.Modals.ModalIncomeStatus
import com.localze.android.R

class AdapterIncomeStatus (val context: Context, val listIncome:List<ModalIncomeStatus>):RecyclerView.Adapter<AdapterIncomeStatus.HolderIncomeStatus>(){
    class HolderIncomeStatus(view:View):RecyclerView.ViewHolder(view) {
        val orderPrice:TextView=view.findViewById(R.id.PaidAmount)
        val payeeName:TextView=view.findViewById(R.id.PayeeNAme)
        val transferStatus:TextView=view.findViewById(R.id.Status)
        val transferId:TextView=view.findViewById(R.id.trnfID)
        val trnfTime:TextView=view.findViewById(R.id.Date)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderIncomeStatus {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.row_income_status,parent,false)
        return HolderIncomeStatus(view)
    }

    override fun getItemCount(): Int {
       return listIncome.size
    }

    override fun onBindViewHolder(holder: HolderIncomeStatus, position: Int) {
        val list_Income=listIncome[position]
        holder.orderPrice.text= "₹${list_Income.orderCost}"
        holder.payeeName.text=list_Income.payeeName
        holder.transferStatus.text="TransferInProgress"
        holder.transferId.text=list_Income.transferId
        when(list_Income.payWith){
            "Cash On Delivery"->{

            }
            "Paytm"->{

            }
            "Razorpay"->{

            }
        }


    }
}