package com.example.uni_cob.Chatting
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.uni_cob.MessageActivity
import com.example.uni_cob.R

import com.example.uni_cob.utility.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class ChatListAdapter(private val chatList: List<ChatModel>) : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat_view, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        val lastComment = chat.comments.values.maxByOrNull { it.timestamp }

        val unreadMessagesCount = chat.comments.values.count { comment ->
            comment.uid != FirebaseAuth.getInstance().currentUser?.uid && !comment.isRead
        }

        holder.itemView.setOnClickListener {
            val destinationUid = chat.users.keys.first { it != FirebaseAuth.getInstance().currentUser?.uid }
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(destinationUid)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let {
                        val intent = Intent(holder.itemView.context, MessageActivity::class.java)
                        intent.putExtra("destinationUid", destinationUid)
                        intent.putExtra("destinationName", user.name)
                        intent.putExtra("destinationProfileImageUrl", user.profileImageUrl)
                        holder.itemView.context.startActivity(intent)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        lastComment?.let { comment ->
            val commenterRef = FirebaseDatabase.getInstance().getReference("users").child(comment.uid!!)
            commenterRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let {
                        holder.bind(it, comment, unreadMessagesCount)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }




    override fun getItemCount(): Int = chatList.size
    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val userNameTextView: TextView = view.findViewById(R.id.messageItem_textview_name)
        private val lastMessageTextView: TextView =
            view.findViewById(R.id.messageItem_textView_message)
        private val messageTimeTextView: TextView =
            view.findViewById(R.id.messageItem_textView_time) // 메시지 시간 표시용 TextView
        private val userProfileImageView: ImageView =
            view.findViewById(R.id.messageItem_imageview_profile)
        private val unreadMessagesCountTextView: TextView = view.findViewById(R.id.msg_cnt)
        fun bind(user: User, comment: ChatModel.Comment, unreadMessagesCount: Int) {
            userNameTextView.text = user.name
            lastMessageTextView.text = comment.message
            messageTimeTextView.text = comment.timestamp.toString()
            Glide.with(itemView.context).load(user.profileImageUrl).into(userProfileImageView)

            if (unreadMessagesCount > 0) {
                unreadMessagesCountTextView.visibility = View.VISIBLE
                unreadMessagesCountTextView.text = unreadMessagesCount.toString()
            } else {
                unreadMessagesCountTextView.visibility = View.GONE
            }
        }
    }
}



