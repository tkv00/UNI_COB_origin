package com.example.uni_cob.utility

data class User(
    val department:String?=null,
    val email: String?=null,
    val name: String?=null,
    val password: String?=null,
    val profileImageUrl:String?=null,
    val schoolName: String?=null,
    val selectedGrade:String?=null,
    val stNumber: String?=null,
    var uid:String?=null,
    val userPhoneNumber: String?=null
)
