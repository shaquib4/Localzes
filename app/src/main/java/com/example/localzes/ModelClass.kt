package com.example.localzes

class ModelClass {
    private var uid:String=""
    private var name:String=""
    private var address:String=""
    private var email:String=""
    private var phone:String=""

    constructor()
    constructor(uid: String, name: String, address: String, email: String, phone: String) {
        this.uid = uid
        this.name = name
        this.address = address
        this.email = email
        this.phone = phone
    }
    fun getUid():String?{
        return uid
    }
    fun setUid(uid: String){
        this.uid=uid
    }
    fun getName():String?{
        return name
    }
    fun setName(name: String){
        this.name=name
    }
    fun getAddress():String?{
        return address
    }
    fun setAddress(address: String){
        this.address=address
    }
    fun getEmail():String?{
        return email
    }
    fun setEmail(email: String){
        this.email=email
    }

    fun getPhone():String?{
        return phone
    }
    fun setPhone(phone: String){
        this.phone=phone
    }
}