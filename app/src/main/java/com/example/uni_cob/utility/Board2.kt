package com.example.uni_cob.utility

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Board2(
    var postId:String?=null,
    var userName:String?=null,//이름
    var profileImageURl:String?=null,//이미지
    var userId: String? = null, // 글을 쓴 사용자의 ID
    var title: String? = null,
    var content: String? = null,
    var categories: MutableList<String> = mutableListOf(),
    var date: Long = System.currentTimeMillis(),
    var time:String?=null,//만날시간지정
    var eventDate: String? = null, // Board2에만 해당
    var location: String? = null, // Board2에만 해당
    var numberOfPeople: Int? = null ,// Board2에만 해당
    var online:Boolean=true, //온라인 오프라인상태
    var currentNumberOfPeople: Int? = null, // 현재 신청한 사람의 수를
    var applications: MutableMap<String, ApplicationInfo>? = null // 신청자 정보

):Parcelable


@Parcelize
data class ApplicationInfo(
    var name: String? = null, // 신청자 이름
    var email: String? = null, // 신청자 이메일
    var phone: String? = null // 신청자 전화번호
) : Parcelable