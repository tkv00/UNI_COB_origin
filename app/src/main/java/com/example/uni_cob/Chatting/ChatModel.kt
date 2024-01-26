package com.example.uni_cob.Chatting

import kotlin.collections.HashMap

data class ChatModel (val chatId: String = "",
                      val users: HashMap<String, Boolean> = HashMap(),
                 val comments : HashMap<String, Comment> = HashMap())
{
    class Comment(
        val uid: String? = null,
        val message: String? = null,
        val imageUrl:String?=null,
        val timestamp: Long = 0L,
        val messageType:String="text",
        val isRead: Boolean = false
    )
}