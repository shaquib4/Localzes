package com.example.localzes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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
        favRecyclerItem.layoutManager = LinearLayoutManager(activity)
        favItems = ArrayList<ModelAddProduct>()
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        databaseReference = FirebaseDatabase.getInstance().reference.child("users").child(uid)
            .child("FavoriteItems")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (favItems as ArrayList<ModelAddProduct>).clear()
                for (i in snapshot.children) {
                    val obj = ModelAddProduct(
                        i.child("shopId").value.toString(),
                        i.child("productId").value.toString(),
                        i.child("imageUrl").value.toString(),
                        i.child("productCategory").value.toString(),
                        i.child("title").value.toString(),
                        i.child("description").value.toString(),
                        i.child("sellingPrice").value.toString(),
                        i.child("offerPrice").value.toString(),
                        i.child("unit").value.toString(),
                        i.child("quantity").value.toString(),
                        i.child("stock").value.toString()
                    )
                    (favItems as ArrayList<ModelAddProduct>).add(obj)
                }
                try {
                    favoriteItemAdapter = AdapterFavoriteItems(activity as Context, favItems)
                    favRecyclerItem.adapter = favoriteItemAdapter
                } catch (e: Exception) {

                }
            }
        })
        return view
    }

}