package com.example.uni_cob.writeboard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextClock
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uni_cob.R
import com.example.uni_cob.department.find_department
import com.example.uni_cob.utility.Comment1
import com.example.uni_cob.utility.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CommentsAdapter(
    private var comments: List<Comment1>, // Make sure to initialize this list properly before passing it to the adapter
    private val currentUser: FirebaseUser,
    private val databaseReference: DatabaseReference // Ensure this reference points to the correct path in your database
) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userNameTextView: TextView = view.findViewById(R.id.who)
        val contentTextView: TextView = view.findViewById(R.id.content1)
        val timeTextView: TextView = view.findViewById(R.id.et_time)
        val departmentTextView: TextView = view.findViewById(R.id.et_department)
        val profileImageView: ImageView = view.findViewById(R.id.register_profile)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]
        // Fetch user details only if userId is not null or empty
        if (!comment.userId.isNullOrEmpty()) {
            databaseReference.child("users").child(comment.userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val user = dataSnapshot.getValue(User::class.java)
                        holder.userNameTextView.text = user?.name ?: "Unknown"
                        holder.departmentTextView.text = user?.department ?: "Unknown"
                        if (!user?.profileImageUrl.isNullOrEmpty()) {
                            Glide.with(holder.itemView.context)
                                .load(user?.profileImageUrl)
                                .placeholder(R.drawable.user)
                                .error(R.drawable.user)
                                .into(holder.profileImageView)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseDatabase", "Error fetching user data: ${error.message}")
                    }
                })
        }

        val dateFormat = SimpleDateFormat("MM월 dd일 HH:mm", Locale.getDefault())
        val date = Date(comment.timestamp)
        holder.timeTextView.text = dateFormat.format(date)
        holder.contentTextView.text = comment.content.trim()
    }

    override fun getItemCount(): Int {
        return comments.size
    }
}

