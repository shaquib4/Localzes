package com.example.localzes.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Modals.ModalSellerOrderList
import com.example.localzes.R
import com.example.localzes.UserListOrderDetails
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AdapterUserListOrderHistory(
    val context: Context,
    private val order_List_History: List<ModalSellerOrderList>
) :
    RecyclerView.Adapter<AdapterUserListOrderHistory.HolderUserListOrderHistory>() {
    class HolderUserListOrderHistory(view: View) : RecyclerView.ViewHolder(view) {
        val totalListItemsTv: TextView = view.findViewById(R.id.totalItemsCustomerTv)
        val totalOrderCostTv: TextView = view.findViewById(R.id.orderAmountCustomerTv)
        val orderListIdTv: TextView = view.findViewById(R.id.orderIdCustomerTv)
        val orderShopTv: TextView = view.findViewById(R.id.txtShopName)
        val orderByShopMobileTv: TextView = view.findViewById(R.id.txtShopMobile)
        val orderDateTv: TextView = view.findViewById(R.id.orderDateTv)
        val orderListStatusTv: TextView = view.findViewById(R.id.orderStatusCustomerTv)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderUserListOrderHistory {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_order_user, parent, false)
        return HolderUserListOrderHistory(view)
    }

    override fun getItemCount(): Int {
        return order_List_History.size
    }

    override fun onBindViewHolder(holder: HolderUserListOrderHistory, position: Int) {
        val userListOrderHistory = order_List_History[position]
        holder.totalListItemsTv.text = userListOrderHistory.totalItems
        if (userListOrderHistory.orderCost == "") {
            holder.totalOrderCostTv.text = "Amount will be updated by seller soon"
        } else {
            holder.totalOrderCostTv.text = userListOrderHistory.orderCost
        }
        holder.orderListIdTv.text = "OD${userListOrderHistory.orderId}"
        val sdf = SimpleDateFormat("dd/MM/yyyy,hh:mm a")
        val date = Date(userListOrderHistory.orderTime.toLong())
        val formattedDate = sdf.format(date)
        holder.orderDateTv.text = formattedDate
        if (userListOrderHistory.listStatus == "") {
            holder.orderListStatusTv.text = "In Progress"
        } else {
            holder.orderListStatusTv.text = userListOrderHistory.listStatus
        }
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("seller")
                .child(userListOrderHistory.orderTo)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                holder.orderShopTv.text = snapshot.child("shop_name").value.toString()
                holder.orderByShopMobileTv.text = snapshot.child("phone").value.toString()
            }

        })
        holder.itemView.setOnClickListener {
            val intent = Intent(context, UserListOrderDetails::class.java)
            intent.putExtra("orderId", userListOrderHistory.orderId)
            intent.putExtra("orderTo", userListOrderHistory.orderTo)
            context.startActivity(intent)
        }
    }
}