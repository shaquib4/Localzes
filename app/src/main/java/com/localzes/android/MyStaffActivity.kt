package com.localzes.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.localzes.android.Adapters.AdapterMyStaff

class MyStaffActivity : AppCompatActivity() {
    private lateinit var addStaff: FloatingActionButton
    private lateinit var recyclerAddStaff: RecyclerView
    private lateinit var adapterStaffs:AdapterMyStaff
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_staff)
        addStaff = findViewById(R.id.addStaff)
        recyclerAddStaff = findViewById(R.id.recycler_add_staff)
    }
}