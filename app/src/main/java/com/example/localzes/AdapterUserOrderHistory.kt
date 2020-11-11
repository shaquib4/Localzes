package com.example.localzes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterUserOrderHistory(
    val context: Context,
    private val order_history: List<ModelUserOrderDetails>
) : RecyclerView.Adapter<AdapterUserOrderHistory.HolderUserOrderHistory>() {
    class HolderUserOrderHistory(view: View) : RecyclerView.ViewHolder(view) {
        val totalItems: TextView = view.findViewById(R.id.totalItemsCustomerTv)
        val orderAmount: TextView = view.findViewById(R.id.orderAmountCustomerTv)
        val orderId: TextView = view.findViewById(R.id.orderIdCustomerTv)
        val orderDate: TextView = view.findViewById(R.id.orderDateTv)
        val orderStatus: TextView = view.findViewById(R.id.orderStatusCustomerTv)
        val orderShop: TextView = view.findViewById(R.id.txtShopName)
        val recyclerOrderHistoryItems: RecyclerView = view.findViewById(R.id.recyclerOrderHistory)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderUserOrderHistory {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_order_user, parent, false)
        return HolderUserOrderHistory(view)
    }

    override fun getItemCount(): Int {
        return order_history.size
    }

    override fun onBindViewHolder(holder: HolderUserOrderHistory, position: Int) {
        val orderHistory = order_history[position]
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(orderHistory.orderTo)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val address = snapshot.child("address")
                holder.orderShop.text = address.value.toString()
            }

        })
        holder.orderId.text = "OD${orderHistory.orderId}"
        if (orderHistory.orderQuantity.toInt() > 1) {
            holder.totalItems.text = "${orderHistory.orderQuantity} items"
        } else {
            holder.totalItems.text = "${orderHistory.orderQuantity} item"
        }
        holder.orderAmount.text = "Rs ${orderHistory.orderCost}"

        val sdf = SimpleDateFormat("dd/MM/yyyy,hh:mm a")
        val date = Date(orderHistory.orderTime.toLong())
        val formattedDate = sdf.format(date)
        holder.orderDate.text = formattedDate
        holder.orderStatus.text = orderHistory.orderStatus
        setUpRecycler(holder.recyclerOrderHistoryItems, orderHistory)

    }

    private fun setUpRecycler(
        recyclerOrderHistoryItem: RecyclerView,
        orderHistoryItems: ModelUserOrderDetails
    ) {
        val orderedItemList = ArrayList<ModelOrderedItems>()
        for (i in orderHistoryItems.orderedItems) {
            val obj = ModelOrderedItems(i.pId, i.name, i.cost, i.price, i.quantity)
            orderedItemList.add(obj)
        }
        val orderedItemAdapter = AdapterOrderedItems(context, orderedItemList)
        val layoutManager = LinearLayoutManager(context)
        recyclerOrderHistoryItem.layoutManager = layoutManager
        recyclerOrderHistoryItem.adapter = orderedItemAdapter
    }
}