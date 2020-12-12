package com.example.localzes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterFavoriteItems
import com.example.localzes.Modals.ModelAddProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ITEM : Fragment() {
    lateinit var favRecyclerItem: RecyclerView
    lateinit var favItems: List<ModelAddProduct>
    lateinit var favoriteItemAdapter: AdapterFavoriteItems
    lateinit var databaseReference: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this   fragment
        val view = inflater.inflate(R.layout.fragment_two, container, false)
        favRecyclerItem = view.findViewById(R.id.recycler_favorite_item)
        favItems = ArrayList<ModelAddProduct>()
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        databaseReference = FirebaseDatabase.getInstance().reference.child("users").child(uid)
            .child("FavoriteItems")
        databaseReference.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
               for(i in snapshot.children){
                   val obj=ModelAddProduct()
               }
            }

        })
        return view
    }

}