package com.example.localzes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Modals.ModelList
import com.example.localzes.R

class AdapterCreateList(val context: Context, val item_List: List<ModelList>) :
    RecyclerView.Adapter<AdapterCreateList.HolderCreateList>() {
    class HolderCreateList(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: EditText = view.findViewById(R.id.Itemname)
        val itemQuantity: EditText = view.findViewById(R.id.Item_Quan)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCreateList {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_single_row, parent, false)
        return HolderCreateList(view)
    }

    override fun getItemCount(): Int {
        return item_List.size
    }

    override fun onBindViewHolder(holder: HolderCreateList, position: Int) {
        val itemList = item_List[position]
        holder.itemName.setText(itemList.itemId)
        holder.itemQuantity.setText(itemList.itemQuantity)
    }

}