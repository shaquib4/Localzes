package com.localze.android.Modals

class ModalIncomeStatus(
    val orderCost: String,
    val transferId: String,
    val settlementId: String,
    val orderId: String,
    val payeeName: String,
    val payWith: String,
    val payeeMobNo: String
) {
    constructor() : this("", "", "", "", "", "", "")
}