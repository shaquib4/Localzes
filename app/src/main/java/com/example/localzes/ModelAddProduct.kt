package com.example.localzes

class ModelAddProduct(val productId:String,val imageUrl:String,val title:String,val description:String,val sellingPrice:String,val offerPrice:String,val quantity:String) {
    constructor():this("","","","","","","")
}