package com.example.uni_cob.Chatting

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uni_cob.Chatting.ChatModel
import com.example.uni_cob.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatMain : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatListAdapter: ChatListAdapter
    private val chatList = mutableListOf<ChatModel>()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_main)

        recyclerView = findViewById(R.id.chat_main)
        recyclerView.layoutManager = LinearLayoutManager(this)
        chatListAdapter = ChatListAdapter(chatList)
        recyclerView.adapter = chatListAdapter

        if (currentUserId != null) {
            loadChatRooms(currentUserId)
        }
    }

    private fun loadChatRooms(userId: String) {
        val database = FirebaseDatabase.getInstance().getReference("chatrooms")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                snapshot.children.forEach { child ->
                    val chatModel = child.getValue(ChatModel::class.java)
                    Log.d("ChatMain", "Chat room: $chatModel")
                    chatModel?.let {
                        if (it.users.containsKey(userId)) {
                            chatList.add(it.copy(chatId = child.key ?: ""))
                        }
                    }
                }
                chatListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // 에러 처리
            }
        })
    }

}
