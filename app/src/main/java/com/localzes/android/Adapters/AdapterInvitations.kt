package com.localzes.android.Adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.localzes.android.Modals.ModelStaffOf

class AdapterInvitations(val context: Context, val invitationOf: List<ModelStaffOf>) :
    RecyclerView.Adapter<AdapterInvitations.HolderInvitationsOf>() {
    class HolderInvitationsOf(view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderInvitationsOf {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return invitationOf.size
    }

    override fun onBindViewHolder(holder: HolderInvitationsOf, position: Int) {
        val invitationStaff = invitationOf[position]
    }

}