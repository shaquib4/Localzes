package com.localzes.android.Modals

class ModelManageAddress(
    val id:String,
    val address: String,
    val city: String,
    val pinCode: String,
    val country: String,
    val state: String,
    val mobileNo: String
) {
    constructor() : this("", "", "", "", "", "","")
}