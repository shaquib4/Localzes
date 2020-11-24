package com.example.localzes.Modals

class Upload(val shopId:String,val phone: String,val name: String,val email: String,val address: String,val shop_name: String,val imageUrl: String, val category1: String, val upi:String,val locality:String,val city:String,val pinCode:String,val state:String, val country:String) {

    constructor() : this("","","", "", "","","","","","","","","","")
}