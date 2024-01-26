package com.example.uni_cob.FindFriend

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uni_cob.R
import com.example.uni_cob.utility.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FriendsAdapter(
    private val friendRequestList: MutableList<FriendRequest>,
    private val currentUserId: String
) : RecyclerView.Adapter<FriendsAdapter.FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_friendlist2, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(friendRequestList[position])
    }

    override fun getItemCount(): Int {
        return friendRequestList.size
    }

    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var nameTextView: TextView = itemView.findViewById(R.id.findfriend_name)
        private var departmentTextView: TextView = itemView.findViewById(R.id.findfriend_uni)
        private var profileImage: ImageView = itemView.findViewById(R.id.register_profile1)

        fun bind(friendRequest: FriendRequest) {
            Log.d("FriendViewHolder", "bind - FriendRequest: $friendRequest")
            friendRequest.fromUserId?.let {
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
}
