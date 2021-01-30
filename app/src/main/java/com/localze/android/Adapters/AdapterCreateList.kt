package com.localze.android.Adapters

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
import com.localze.android.CreateList
import com.localze.android.Modals.ModelList
import com.localze.android.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdapterCreateList(val context: Context, val item_List: List<ModelList>) :
    RecyclerView.Adapter<AdapterCreateList.HolderCreateList>() {
    class HolderCreateList(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: EditText = view.findViewById(R.id.Item_name)
        val itemQuantity: EditText = view.findViewById(R.id.Item_Quan)
        val itemRemove: ImageView = view.findViewById(R.id.itemRemove)
        val itemSNo: TextView = view.findViewById(R.id.txtSNo)

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
        holder.itemName.setText(itemList.itemName)
        holder.itemQuantity.setText(itemList.itemQuantity)
        holder.itemSNo.text = (position + 1).toString() + "."
        holder.itemName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                saveToDatabase1(s.toString(), itemList.itemId)
            }

        })
        holder.itemQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                saveToDatabase2(s.toString(), itemList.itemId)

            }

        })
        holder.itemRemove.setOnClickListener {
            deleteListItem(position)
        }
    }

    private fun deleteListItem(position: Int) {
        val uAuth = FirebaseAuth.getInstance()
        val user = uAuth.currentUser
        val uid = user!!.uid
        val id = item_List[position].itemId
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("OrderList")
        databaseReference.orderByChild("itemId").equalTo(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        i.ref.removeValue()
                    }
                    (context as CreateList).recreate()
                }
            })
    }

    private fun saveToDatabase2(s: String, itemId: String) {
        val uAuth = FirebaseAuth.getInstance()
        val user = uAuth.currentUser
        val uid = user!!.uid
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("OrderList")
                .child(itemId)
        val headers = HashMap<String, Any>()
        headers["itemQuantity"] = s
        databaseReference.updateChildren(headers)
    }


    private fun saveToDatabase1(s: String, itemId: String) {
        val uAuth = FirebaseAuth.getInstance()
        val user = uAuth.currentUser
        val uid = user!!.uid
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("OrderList")
                .child(itemId)
        val headers = HashMap<String, Any>()
        headers["itemName"] = s
        databaseReference.updateChildren(headers)
    }

}