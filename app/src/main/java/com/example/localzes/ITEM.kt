package com.example.localzes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterFavoriteItems
import com.example.localzes.Modals.ModelAddProduct

class ITEM : Fragment() {
    lateinit var favRecyclerItem: RecyclerView
    lateinit var favItems: List<ModelAddProduct>
    lateinit var favoriteItemAdapter: AdapterFavoriteItems


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this   fragment
        val view = inflater.inflate(R.layout.fragment_two, container, false)
        favRecyclerItem = view.findViewById(R.id.recycler_favorite_item)
        favItems = ArrayList<ModelAddProduct>()
        return view
    }

}