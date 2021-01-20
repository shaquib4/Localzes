package com.localzes.android.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.localzes.android.Modals.ModelOrderedItems
import com.localzes.android.R

class AdapterOrderedItems(
    val context: Context,
    private val ordered_items: List<ModelOrderedItems>
) : RecyclerView.Adapter<AdapterOrderedItems.HolderOrderedItems>() {
    class HolderOrderedItems(view: View) : RecyclerView.ViewHolder(view) {
        val itemTitle: TextView = view.findViewById(R.id.itemTitle)
        val itemPrice: TextView = view.findViewById(R.id.itemPrice)
        val totalQuantity: TextView = view.findViewById(R.id.totalQuantity)
        val totalPrice: TextView = view.findViewById(R.id.totalPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderOrderedItems {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_ordered_items, parent, false)
        return HolderOrderedItems(
            view
        )
    }

    override fun getItemCount(): Int {
        return ordered_items.size
    }

    override fun onBindViewHolder(holder: HolderOrderedItems, position: Int) {
        val orderedItems = ordered_items[position]
        holder.itemTitle.text = orderedItems.name
        holder.itemPrice.text = "₹${orderedItems.price}"
        holder.totalPrice.text = "₹${orderedItems.cost}"
        if (orderedItems.quantity > 1.toString()) {
            holder.totalQuantity.text = orderedItems.quantity + "items"
        } else {
            holder.totalQuantity.text = orderedItems.quantity + "item"
        }

    }
}