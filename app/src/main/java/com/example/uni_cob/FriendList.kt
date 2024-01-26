package com.example.uni_cob

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uni_cob.FindFriend.FriendRequest
import com.example.uni_cob.FindFriend.FriendRequestsAdapter
import com.example.uni_cob.FindFriend.FriendsAdapter
import com.example.uni_cob.utility.UserStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FriendList : AppCompatActivity() {
    private lateinit var friendRequestsRecyclerView: RecyclerView
    private lateinit var friendsRecyclerView: RecyclerView
    private lateinit var friendRequestsAdapter: FriendRequestsAdapter
    private lateinit var friendsAdapter: FriendsAdapter
    private var friendRequestList: MutableList<FriendRequest> = mutableListOf()
    private var friendsList: MutableList<FriendRequest> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)

        // 리사이클러뷰 초기화
        friendRequestsRecyclerView = findViewById(R.id.recy_friendlist2)
        friendsRecyclerView = findViewById(R.id.recy_friendlist)

        friendRequestsRecyclerView.layoutManager = LinearLayoutManager(this)
        friendsRecyclerView.layoutManager = LinearLayoutManager(this)
        Log.d("FriendList", "RecyclerView layoutManager set")
        // 데이터 불러오기 로직 추가

        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: return
        // 데이터를 불러온 후에 어댑터를 설정

        friendRequestsAdapter = FriendRequestsAdapter(friendRequestList,
            { friendRequest, position ->
                // 수락 버튼 눌렀을 때의 동작
                // 예: 친구 목록으로 추가하고 알림
                friendsList.add(friendRequest)
                friendsAdapter.notifyDataSetChanged()
            },
            { friendRequest, position ->
                // 거부 버튼 눌렀을 때의 동작
                // 예: 요청 목록에서 제거
                friendRequestList.removeAt(position)
                friendRequestsAdapter.notifyItemRemoved(position)
            }
        )
        friendsAdapter = FriendsAdapter(friendsList, currentUserID)

        friendRequestsRecyclerView.adapter = friendRequestsAdapter
        friendsRecyclerView.adapter = friendsAdapter

        // 데이터 로딩
        loadFriendRequests()
        loadFriends()
        Log.d("FriendList", "RecyclerView adapter set")
    }

    private fun loadFriendRequests() {
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")

        databaseReference.child(currentUserID).child("FriendRequests")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    friendRequestList.clear()
                    for (requestSnapshot in snapshot.children) {
                        val friendRequest = requestSnapshot.getValue(FriendRequest::class.java)
                        if (friendRequest != null && friendRequest.status == UserStatus.REQUEST) {
                            friendRequestList.add(friendRequest)
                        }
                    }
                    friendRequestsAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // 오류 처리
                }
            })
    }

    private fun loadFriends() {
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")

        databaseReference.child(currentUserID).child("FriendList")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    friendsList.clear()
                    for (requestSnapshot in snapshot.children) {
                        val friendRequest = requestSnapshot.getValue(FriendRequest::class.java)
                        if (friendRequest != null && friendRequest.status == UserStatus.FRIEND) {
                            friendsList.add(friendRequest)
                        }
                    }
                    friendsAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FriendList", "Failed to load friend list data", error.toException())
                }
            })
    }

}