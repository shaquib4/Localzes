package com.localze.android.Modals

class ModelOrderedItems(
    val pId: String,
    val name: String,
    val cost: String,
    val price: String,
    val quantity: String,
    val unit:String,
    val originalQuantity:String
) {
    constructor():this("","","","","","","")
}