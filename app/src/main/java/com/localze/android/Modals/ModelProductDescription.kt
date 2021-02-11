package com.localze.android.Modals

class ModelProductDescription(
    val productDescription: String,
    val colorName: String,
    val availableSizes: String,
    val refundType: String
) {
    constructor() : this("", "", "", "")
}