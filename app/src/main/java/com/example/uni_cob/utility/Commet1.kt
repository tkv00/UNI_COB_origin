package com.example.uni_cob.utility

data class Comment1(
    val userId: String = "",
    val userName: String="",
    val content: String="",
    val timestamp: Long=0L,
    val department:String="",
    val profileImageUrl: String="",
    val postId:String="",
    val isRead: Boolean = false // 읽음 상태 필드 추가
)
