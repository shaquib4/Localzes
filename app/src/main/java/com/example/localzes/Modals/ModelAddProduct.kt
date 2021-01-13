package com.example.localzes.Modals

class ModelAddProduct(
    val shopId: String,
    val productId: String,
    val imageUrl: String,
    val productCategory: String,
    val title: String,
    val description: String,
    val sellingPrice: String,
    val offerPrice: String,
    val unit: String,
    val quantity: String,
    val stock: String
) {
    constructor() : this("", "", "", "", "", "", "", "", "", "", "")
}