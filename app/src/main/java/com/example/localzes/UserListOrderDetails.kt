package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class UserListOrderDetails : AppCompatActivity() {
    private lateinit var orderListIdUser:TextView
    private lateinit var orderDateListUser:TextView
    private lateinit var orderStatusListUser:TextView
    private lateinit var shopNameOrderList:TextView
    private lateinit var itemsOrderList:TextView
    private lateinit var amountOrderList:TextView
    private lateinit var deliveryAddressOrder:TextView
    private lateinit var shopAddressOrder:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list_order_details)
        orderListIdUser=findViewById(R.id.txtOrderListIdUser)
        orderDateListUser=findViewById(R.id.txtOrderListDateUser)
        orderStatusListUser=findViewById(R.id.txtOrderListStatusUser)
        shopNameOrderList=findViewById(R.id.txtShopNameOrderList)
        itemsOrderList=findViewById(R.id.txtItemsListUser)
        amountOrderList=findViewById(R.id.txtOrderListCostUser)
        deliveryAddressOrder=findViewById(R.id.txtOrderListDeliveryAddressUser)

    }
}