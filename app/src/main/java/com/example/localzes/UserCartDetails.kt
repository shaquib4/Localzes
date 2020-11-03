package com.example.localzes

class UserCartDetails(val orderBy:String,val productTitle:String,val priceEach:String,val finalPrice:String,val finalQuantity:String,val orderTo:String) {
    constructor():this("","","","","","")
}