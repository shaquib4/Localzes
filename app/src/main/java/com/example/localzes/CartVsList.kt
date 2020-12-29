package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class CartVsList : AppCompatActivity() {
    private lateinit var cardCarts: CardView
    private lateinit var cardListOrders: CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_vs_list)
        cardCarts = findViewById(R.id.cardCarts)
        cardListOrders = findViewById(R.id.cardListOrders)
        cardCarts.setOnClickListener {

        }
        cardListOrders.setOnClickListener {

        }
    }
}