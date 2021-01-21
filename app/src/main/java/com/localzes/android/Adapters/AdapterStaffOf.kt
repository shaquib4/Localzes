package com.localzes.android.Adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.localzes.android.Modals.ModelStaffOf
import com.localzes.android.Modals.Upload

class AdapterStaffOf(val context:Context, val staffOf:List<ModelStaffOf>):
    RecyclerView.Adapter<AdapterStaffOf.HolderStaffOf>() {
    class HolderStaffOf(view:View):RecyclerView.ViewHolder(view){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderStaffOf {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return staffOf.size
    }

    override fun onBindViewHolder(holder: HolderStaffOf, position: Int) {
        TODO("Not yet implemented")
    }
}