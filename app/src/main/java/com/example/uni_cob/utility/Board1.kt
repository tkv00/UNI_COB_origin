package com.example.uni_cob.utility

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Board1(
    var userName:String?=null,//이름
    var postId:String?=null,//게시판 고유 id
    var profileImageURl:String?=null,//이미지
    var userId:String?=null,
    var title: String?=null,
    var content: String?=null,
    var categories: MutableList<String> = mutableListOf(),
    var date: Long = System.currentTimeMillis() // 작성 날짜는 현재 시간으로 설정
): Parcelable
