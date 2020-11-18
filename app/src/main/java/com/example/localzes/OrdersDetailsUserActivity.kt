package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class OrdersDetailsUserActivity : AppCompatActivity() {
    private lateinit var orderIdUser:TextView
    private lateinit var orderDateUser:TextView
    private lateinit var orderStatusUser:TextView
    private lateinit var shopNameUser:TextView
    private lateinit var totalItemsUser:TextView
    private lateinit var totalAmountUser:TextView
    private lateinit var deliveryAddress:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_details_user)

    }
}