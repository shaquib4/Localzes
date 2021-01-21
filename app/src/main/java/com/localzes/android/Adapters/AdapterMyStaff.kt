package com.localzes.android.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.localzes.android.Modals.ModalAddStaff
import com.localzes.android.R

class AdapterMyStaff(val context: Context, val staffDetails: List<ModalAddStaff>) :
    RecyclerView.Adapter<AdapterMyStaff.HolderMyStaff>() {
    class HolderMyStaff(view: View) : RecyclerView.ViewHolder(view) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderMyStaff {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.staff_single_row, parent, false)
        return HolderMyStaff(view)

    }

    override fun getItemCount(): Int {
        return staffDetails.size
    }

    override fun onBindViewHolder(holder: HolderMyStaff, position: Int) {


    }
}