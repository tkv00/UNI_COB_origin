package com.example.uni_cob.FindFriend

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uni_cob.R
import com.example.uni_cob.utility.User
import com.google.firebase.database.*
import java.io.Serializable

class FindFriend_main : AppCompatActivity(),OnUserClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var searchField:EditText
    private var fullUserList: List<User> = listOf() // 전체 사용자 목록

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_friend_main)
        searchField=findViewById(R.id.et_search)

        recyclerView = findViewById(R.id.findfriend_recycleview1)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Realtime Database에서 유저 정보 가져오기
        loadUsersFromRealtimeDatabase()
        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterUsers(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
    private fun loadUsersFromRealtimeDatabase() {
        database = FirebaseDatabase.getInstance().getReference("users")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userList = ArrayList<User>()
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let { userList.add(it) }
                }
                fullUserList = userList // 전체 사용자 목록 업데이트
                recyclerView.adapter = UserListAdapter(fullUserList,this@FindFriend_main) // 전체 목록으로 어댑터 초기화
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 실패 시 처리
            }
        })
    }

    private fun filterUsers(query: String) {
        val filteredList = if (query.isEmpty()) {
            fullUserList // 검색 쿼리가 비어 있으면 전체 목록 반환
        } else {
            fullUserList.filter { user ->
                user.name?.contains(query, ignoreCase = true) == true ||
                        user.schoolName?.contains(query, ignoreCase = true) == true ||
                        user.department?.contains(query, ignoreCase = true) == true
            }
        }
        recyclerView.adapter = UserListAdapter(filteredList,this) // 필터링된 목록으로 어댑터 업데이트
    }
    override fun onUserClick(user: User) {
        // 여기에서 유저 정보와 함께 새로운 액티비티나 프래그먼트로 이동하는 로직을 구현
        val intent = Intent(this, FriendProfile::class.java)
        intent.putExtra("USER_INFO", user as Serializable) // 'User' 클래스가 Serializable 또는 Parcelable 인 경우
        startActivity(intent)
    }

}
