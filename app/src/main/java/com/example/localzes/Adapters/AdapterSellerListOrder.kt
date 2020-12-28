package com.example.localzes.Adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Modals.ModelList
import com.example.localzes.R

class AdapterSellerListOrder(
    val context: Context,
    val seller_order_list: List<ModelList>
) :
    RecyclerView.Adapter<AdapterSellerListOrder.HolderSellerListOrder>() {
    class HolderSellerListOrder(view: View) : RecyclerView.ViewHolder(view) {
        val txtSNo: TextView = view.findViewById(R.id.txtNo)
        val txtItem_Name: TextView = view.findViewById(R.id.txtItem_Name)
        val txtQuan: TextView = view.findViewById(R.id.txtQuan)
        val edtPrice: EditText = view.findViewById(R.id.edtPrice)
        val itemRemove: ImageView = view.findViewById(R.id.itemRemove)
        val itemRevive: ImageView = view.findViewById(R.id.itemRevive)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderSellerListOrder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_single_row_seller_order, parent, false)
        return HolderSellerListOrder(view)
    }

    override fun getItemCount(): Int {
        return seller_order_list.size

    }

    override fun onBindViewHolder(holder: HolderSellerListOrder, position: Int) {
        val sellerOrderList = seller_order_list[position]
        holder.txtSNo.text = (position + 1).toString() + "."
        holder.txtItem_Name.text=sellerOrderList.itemName
        holder.txtQuan.text=sellerOrderList.itemQuantity
        holder.edtPrice.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        holder.itemRemove.setOnClickListener {

        }
        holder.itemRevive.setOnClickListener {

        }

    }
}