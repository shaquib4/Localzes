package com.localzes.android.Modals

class ModelUserOrderDetails(
    val orderId: String,
    val orderTime: String,
    val orderStatus: String,
    val orderCost: String,
    val orderBy: String,
    val orderTo: String,
    val orderQuantity:String,
    val orderedItems:ArrayList<ModelOrderedItems>
) {
    constructor():this("","","","","","","",ArrayList())
}