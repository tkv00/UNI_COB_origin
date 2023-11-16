package com.example.uni_cob.utility

data class Board1(
    var userId:String?=null,
    var title: String?=null,
    var content: String?=null,
    var categories: MutableList<String> = mutableListOf(),
    var date: Long = System.currentTimeMillis() // 작성 날짜는 현재 시간으로 설정
)
