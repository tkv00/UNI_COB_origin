package com.example.uni_cob

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uni_cob.FindFriend.FriendRequest
import com.example.uni_cob.utility.UserStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.apache.commons.lang3.ObjectUtils.Null


class FriendList : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private var friendRequestList: MutableList<FriendRequest> = mutableListOf()
    private lateinit var adapter: FriendsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)

        recyclerView = findViewById(R.id.recy_friendlist) // 실제 리사이클러뷰 ID로 교체
        recyclerView.layoutManager = LinearLayoutManager(this)
        Log.d("FriendList", "RecyclerView layoutManager set")
        // 데이터 불러오기 로직 추가

        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: return
        // 데이터를 불러온 후에 어댑터를 설정

        adapter = FriendsAdapter(friendRequestList, currentUserID)
        recyclerView.adapter = adapter
        loadUserData()
        Log.d("FriendList", "RecyclerView adapter set")
    }

    private fun loadUserData() {
        Log.d("FriendList", "loadUserData called")
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: return
        Log.d("FriendList", "Current User ID: $currentUserID")
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
        Log.d("FriendList", "Loading data for userID: $currentUserID")

        databaseReference.child(currentUserID).child("FriendRequests").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                friendRequestList.clear()
                Log.d("FriendList", "DataSnapshot: $snapshot")
                for (requestSnapshot in snapshot.children) {
                    val friendRequest = requestSnapshot.getValue(FriendRequest::class.java)
                    Log.d("FriendList", "FriendRequest: $friendRequest")
                    if (friendRequest != null && friendRequest.toUserId == currentUserID &&friendRequest.status==UserStatus.REQUEST) {
                        friendRequestList.add(friendRequest)
                        Log.d("FriendList", "FriendRequest added: $friendRequest")
                    }
                }
                Log.d("FriendList", "Final list size after load: ${friendRequestList.size}")
                adapter.notifyDataSetChanged()
                Log.d("FriendList", "Data loaded, list size: ${friendRequestList.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FriendList", "Failed to load friend request data", error.toException())
            }
        })
    }

}
