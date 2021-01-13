package com.example.localzes.Modals

class ModalSellerOrderList(
    val orderId: String,
    val orderTime: String,
    val orderStatus: String,
    val orderCost: String,
    val orderBy: String,
    val orderTo: String,
    val deliveryAddress: String,
    val totalItems: String,
    val listStatus: String,
    val orderByName: String,
    val orderByMobile: String,
    val paymentMode: String
) {
    constructor() : this("", "", "", "", "", "", "", "", "", "", "", "")
}