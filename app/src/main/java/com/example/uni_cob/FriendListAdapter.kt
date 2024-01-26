package com.example.uni_cob

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uni_cob.FindFriend.FriendRequest
import com.example.uni_cob.utility.User
import com.example.uni_cob.utility.UserStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FriendsAdapter(private val friendRequestList: MutableList<FriendRequest>,
                     private val currentUserId: String ) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        val friendRequest = friendRequestList[position]
        // 사용자가 받은 친구 요청만 표시
        return if (!friendRequest.fromUserId.equals(currentUserId) && friendRequest.status == UserStatus.REQUEST) {
            ViewType.REQUEST.ordinal
        } else {
            ViewType.HIDDEN.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.FRIEND.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friendlist2, parent, false)
                FriendViewHolder(view)
            }
            ViewType.REQUEST.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friendlist, parent, false)
                FriendRequestViewHolder(view)
            }
            ViewType.HIDDEN.ordinal -> {
                val emptyView = View(parent.context)
                EmptyViewHolder(emptyView)
            }
            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val friendRequest = friendRequestList[position]
        when (holder) {
            is FriendViewHolder -> holder.bind(friendRequest)
            is FriendRequestViewHolder -> holder.bind(friendRequest, position)
            is EmptyViewHolder -> {}
        }
    }

    override fun getItemCount(): Int {
        // 숨겨진 항목(사용자가 보낸 요청)을 제외한 친구 요청의 수를 반환
        return friendRequestList.count {
            !it.fromUserId.equals(currentUserId) && it.status == UserStatus.REQUEST
        }
    }

    inner class FriendRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var nameTextView: TextView = itemView.findViewById(R.id.findfriend_name)
        private var acceptButton: Button = itemView.findViewById(R.id.btn_accept)
        private var denyButton: Button = itemView.findViewById(R.id.btn_deny)
        private var departmentTextView: TextView = itemView.findViewById(R.id.findfriend_uni)
        private var profileImage: ImageView = itemView.findViewById(R.id.register_profile1)

        fun bind(friendRequest: FriendRequest, position: Int) {
            friendRequest.toUserId?.let {
                fetchUserData(it) { user ->
                    nameTextView.text = user.name
                    departmentTextView.text = user.department
                    Glide.with(itemView.context).load(user.profileImageUrl).into(profileImage)

                    acceptButton.setOnClickListener {
                        friendRequest.fromUserId?.let { it1 -> acceptFriendRequest(it1) }
                    }

                    denyButton.setOnClickListener {
                        denyFriendRequest(friendRequest, position)
                    }
                }
            }
        }

        private fun acceptFriendRequest(fromUserId: String) {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            if (currentUserId != null) {
                val database = FirebaseDatabase.getInstance().getReference("users")

                // B의 FriendRequests에서 해당 요청 삭제
                database.child(currentUserId).child("FriendRequests").child(fromUserId).removeValue()

                // B와 A의 FriendList에 서로 추가
                val friendForB = FriendRequest(fromUserId, currentUserId, UserStatus.FRIEND)
                val friendForA = FriendRequest(currentUserId, fromUserId, UserStatus.FRIEND)

                database.child(currentUserId).child("FriendRequests").child(fromUserId).setValue(friendForB)
                database.child(fromUserId).child("FriendRequests").child(currentUserId).setValue(friendForA)
            }
        }

        private fun denyFriendRequest(friendRequest: FriendRequest, position: Int) {
            val fromUserId = friendRequest.fromUserId
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

            if (currentUserId != null && fromUserId != null) {
                val database = FirebaseDatabase.getInstance().getReference("users")

                database.child(currentUserId).child("FriendRequests").child(fromUserId).child("status")
                    .setValue("DECLINED")
                database.child(fromUserId).child("FriendRequests").child(currentUserId).child("status")
                    .setValue("DECLINED")
            }
        }
        }

        private fun fetchUserData(userId: String, callback: (User) -> Unit) {
            Log.d("FriendsAdapter", "fetchUserData called with userId: $userId")
            val databaseReference = FirebaseDatabase.getInstance().getReference("users")
            databaseReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    Log.e("fetchUserData","${user}")
                    if (user != null) {

                        callback(user)
                    } else {
                        Log.e("FriendsAdapter", "User data is null for userId: $userId")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FriendsAdapter", "Failed to fetch user data for userId: $userId, error: ${error.message}", error.toException())
                }
            })
        }

    }

    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameTextView: TextView = itemView.findViewById(R.id.findfriend_name)
        private var departmentTextView: TextView = itemView.findViewById(R.id.findfriend_uni)
        private var profileImage: ImageView = itemView.findViewById(R.id.register_profile1)

        fun bind(friendRequest: FriendRequest) {
            Log.d("FriendViewHolder", "bind - FriendRequest: $friendRequest")
            friendRequest.toUserId?.let {
                fetchUserData(it) { user ->
                    nameTextView.text = user.name
                    departmentTextView.text = user.department
                    Glide.with(itemView.context).load(user.profileImageUrl).into(profileImage)
                }
            }
        }

        private fun fetchUserData(userId: String, callback: (User) -> Unit) {
            Log.d("FriendsAdapter", "fetchUserData called with userId: $userId")
            val databaseReference = FirebaseDatabase.getInstance().getReference("users")
            databaseReference.child(userId).addListenerForSingleValueEvent(object :
                ValueEventListener {
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

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 빈 뷰 홀더 구현
    }

    enum class ViewType {
        FRIEND,
        REQUEST,
        HIDDEN
    }

