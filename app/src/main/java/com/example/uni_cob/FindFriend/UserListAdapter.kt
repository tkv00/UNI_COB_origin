package com.example.uni_cob.FindFriend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uni_cob.R
import com.example.uni_cob.utility.User

class UserListAdapter(private val userList: List<User>, private val listener: FindFriend_main) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.findfriend_name)
        val universityTextView: TextView = itemView.findViewById(R.id.findfriend_uni)
        val profileImage:ImageView=itemView.findViewById(R.id.register_profile1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_findfriend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.userNameTextView.text = user.name
        holder.itemView.setOnClickListener{listener.onUserClick(user)}
        holder.universityTextView.text =
            "${user.schoolName} ${user.selectedGrade} ${user.department}"

        Glide.with(holder.itemView.context)
            .load(user.profileImageUrl)
            .placeholder(R.drawable.user) // 로딩 중 표시할 이미지
            .error(R.drawable.user) // 로드 실패 시 표시할 이미지
            .into(holder.profileImage)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

}