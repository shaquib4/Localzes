package com.example.localzes

class ModelOrderDetails(
    val orderId: String,
    val orderTime: String,
    val orderStatus: String,
    val orderCost: String,
    val orderBy: String,
    val orderTo: String,
    val orderQuantity:String,
    val deliveryAddress:String
) {
    constructor():this("","","","","","","","")
}