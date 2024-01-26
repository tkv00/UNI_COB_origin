package com.example.uni_cob.FindFriend

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uni_cob.R
import com.example.uni_cob.utility.User
import com.example.uni_cob.utility.UserStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FriendRequestsAdapter(
    private val friendRequestList: MutableList<FriendRequest>,
    private val onAccept: (FriendRequest, Int) -> Unit,
    private val onDeny: (FriendRequest, Int) -> Unit
) : RecyclerView.Adapter<FriendRequestsAdapter.FriendRequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_friendlist, parent, false)
        return FriendRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendRequestViewHolder, position: Int) {
        holder.bind(friendRequestList[position], position)
    }

    override fun getItemCount(): Int {
        return friendRequestList.size
    }

    inner class FriendRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var nameTextView: TextView = itemView.findViewById(R.id.findfriend_name)
        private var acceptButton: Button = itemView.findViewById(R.id.btn_accept)
        private var denyButton: Button = itemView.findViewById(R.id.btn_deny)
        private var departmentTextView: TextView = itemView.findViewById(R.id.findfriend_uni)
        private var profileImage: ImageView = itemView.findViewById(R.id.register_profile1)

        fun bind(friendRequest: FriendRequest, position: Int) {
            friendRequest.fromUserId?.let {
                fetchUserData(it) { user ->
                    nameTextView.text = user.name
                    departmentTextView.text = user.department
                    Glide.with(itemView.context).load(user.profileImageUrl).into(profileImage)

                    acceptButton.setOnClickListener { acceptFriendRequest(friendRequest, position) }
                    denyButton.setOnClickListener { denyFriendRequest(friendRequest, position) }
                }
            }
        }


        private fun acceptFriendRequest(friendRequest: FriendRequest, position: Int) {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            val fromUserId = friendRequest.fromUserId

            if (currentUserId != null && fromUserId != null) {
                val database = FirebaseDatabase.getInstance().getReference("users")

                // 수락한 경우 양쪽 사용자의 FriendList에 서로 추가
                val friendForCurrent = FriendRequest(fromUserId, currentUserId, UserStatus.FRIEND)
                val friendForFrom = FriendRequest(currentUserId, fromUserId, UserStatus.FRIEND)

                database.child(currentUserId).child("FriendList").child(fromUserId)
                    .setValue(friendForFrom)
                database.child(fromUserId).child("FriendList").child(currentUserId)
                    .setValue(friendForCurrent)

                // 요청 목록에서 제거
                database.child(currentUserId).child("FriendRequests").child(fromUserId)
                    .removeValue()
                friendRequestList.removeAt(position)

                onAccept(friendRequest, position)
            }
        }


        private fun denyFriendRequest(friendRequest: FriendRequest, position: Int) {
            val fromUserId = friendRequest.fromUserId
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

            if (currentUserId != null && fromUserId != null) {
                val database = FirebaseDatabase.getInstance().getReference("users")

                // 거부한 경우 요청 상태 업데이트
                database.child(currentUserId).child("FriendRequests").child(fromUserId)
                    .child("status")
                    .setValue(UserStatus.DECLINED)
                friendRequestList.removeAt(position)

                onDeny(friendRequest, position)
            }
        }


        private fun fetchUserData(userId: String, callback: (User) -> Unit) {
            Log.d("FriendsAdapter", "fetchUserData called with userId: $userId")
            val databaseReference = FirebaseDatabase.getInstance().getReference("users")
            databaseReference.child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        Log.e("fetchUserData", "${user}")
                        if (user != null) {

                            callback(user)
                        } else {
                            Log.e("FriendsAdapter", "User data is null for userId: $userId")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(
                            "FriendsAdapter",
                            "Failed to fetch user data for userId: $userId, error: ${error.message}",
                            error.toException()
                        )
                    }
                })
        }
    }
}
