package com.example.uni_cob.utility

data class Board2(
    var userId: String? = null, // 글을 쓴 사용자의 ID
    var title: String? = null,
    var content: String? = null,
    var categories: MutableList<String> = mutableListOf(),
    var date: Long = System.currentTimeMillis(),
    var time:String?=null,//만날시간지정
    var eventDate: String? = null, // Board2에만 해당
    var location: String? = null, // Board2에만 해당
    var numberOfPeople: Int? = null // Board2에만 해당
)
