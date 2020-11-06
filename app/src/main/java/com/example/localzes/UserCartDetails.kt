package com.example.localzes

class UserCartDetails(val productId:String,val orderBy:String,val productTitle:String,val priceEach:String,val finalPrice:String,val finalQuantity:String,val orderTo:String,val productImageUrl:String,val sellingPrice:String) {
    constructor():this("","","","","","","","","")
}