package com.example.uni_cob.utility

data class Board3(

    var userId: String? = null, // 글을 쓴 사용자의 ID
    var title: String? = null,
    var content: String? = null,
    var categories: List<String> = mutableListOf(),
    var date: Long = System.currentTimeMillis(),
    var time:String?=null,//만날시간지정
    var eventDate: String? = null, // Board2에만 해당
    var location: String? = null, // Board2에만 해당
    var numberOfPeople: Int = 0,// Board2에만 해당
    var online:Boolean=true,//온라인 오프라인상태
    var money:Int=0, //원데이클래스 비용 설정

)

