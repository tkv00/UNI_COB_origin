package com.example.uni_cob.writeboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uni_cob.R
import com.example.uni_cob.utility.Board2
import com.example.uni_cob.utility.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AgoraBoardAll : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var board2List: MutableList<Board2>
    private lateinit var adapter: Board2Adapter
    private lateinit var btn_back: Button
    private lateinit var dbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agora_board_all)
        recyclerView = findViewById(R.id.agora_list_all)
        recyclerView.layoutManager = LinearLayoutManager(this)
        board2List = mutableListOf()

        adapter = Board2Adapter(board2List) { board2 ->
            val intent = Intent(this@AgoraBoardAll, Board1Detail::class.java).apply {
                putExtra("BOARD_DETAIL2", board2 as Parcelable)
                putExtra("TITLE",board2.title)
                putExtra("UID",board2.userId)
                putExtra("TIME",board2.time)
                putExtra("LOCATION",board2.location)
                putExtra("CONTENT",board2.content)
                putExtra("PEOPLE",board2.numberOfPeople)

            }
            startActivity(intent)
        }
        recyclerView.adapter=adapter
        dbRef = FirebaseDatabase.getInstance().getReference("Board2")
        dbRef.addValueEventListener(object : ValueEventListener {
            override  fun onDataChange(snapshot: DataSnapshot) {
                board2List.clear()
                snapshot.children.mapNotNullTo(board2List) { dataSnapshot ->
                    val board2 = dataSnapshot.getValue(Board2::class.java)
                    board2?.let { boardItem ->
                        val uid = boardItem.userId // Board2의 userId 가져옴

                        // 사용자 정보 가져오는 함수 호출
                        if (uid != null) {
                            fetchUserInfo(uid) { userName, userProfileImageUrl ->
                                // 사용자 정보를 Board2 객체에 설정
                                boardItem.userName = userName
                                boardItem.profileImageURl = userProfileImageUrl

                                runOnUiThread {
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                    board2 // 수정된 변수 반환
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error fetching data", Toast.LENGTH_LONG).show()
            }
        })
    }

    private  fun fetchUserInfo(
        uid: String,
        onUserInfoFetched: (userName: String?, userProfileImageUrl: String?) -> Unit
    ) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")

        usersRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    val userName = user?.name
                    val userProfileImageUrl = user?.profileImageUrl
                    onUserInfoFetched(userName, userProfileImageUrl)
                } else {
                    // 일치하는 사용자 정보가 없을 경우 처리
                    onUserInfoFetched(null, null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 오류 처리
                Log.e("FetchUserInfoError", "Error fetching user info: ${error.message}")
                onUserInfoFetched(null, null)
            }
        })
    }

}