package com.localzes.android.Modals

class ModalAddStaff(
    val name: String,
    val phone: String,
    val address: String,
    val status: String,
    val access: String,
    val uid: String
) {
    constructor() : this("", "", "", "", "", "")
}