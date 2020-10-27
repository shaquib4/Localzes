package com.example.localzes

class ModelAddProduct(val productId:String,val imageUrl:String,val productCategory:String,val title:String,val description:String,val sellingPrice:String,val offerPrice:String,val unit:String,val quantity:String) {
    constructor():this("","","","","","","","","")
}