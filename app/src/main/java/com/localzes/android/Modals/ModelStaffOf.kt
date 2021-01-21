package com.localzes.android.Modals

class ModelStaffOf(
    val shopOwnerName: String,
    val shopName: String,
    val shopMobileNumber: String,
    val status: String,
    val invitationUid: String
) {
    constructor() : this("", "", "", "", "")
}