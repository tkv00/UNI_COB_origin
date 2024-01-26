package com.example.uni_cob.writeboard

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uni_cob.R
import com.example.uni_cob.utility.Board2
import com.example.uni_cob.utility.Board3
import com.example.uni_cob.utility.Board4
import com.example.uni_cob.utility.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Board3_4_All : AppCompatActivity() {

    private lateinit var recyclerView3: RecyclerView
    private lateinit var recyclerView4: RecyclerView
    private lateinit var board3List: MutableList<Board3>
    private lateinit var board4List: MutableList<Board4>
    private lateinit var adapter3: Board3Adapter
    private lateinit var adapter4: Board4Adapter
    private lateinit var go_to_board3: Button
    private lateinit var go_to_board4: Button
    private lateinit var dbRef3: DatabaseReference
    private lateinit var dbRef4: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board3_4_all)
        recyclerView3 = findViewById(R.id.agora_list_view_1)
        recyclerView4 = findViewById(R.id.agora_list_view_2)

        go_to_board3=findViewById(R.id.btn_show_more1)
        go_to_board4=findViewById(R.id.btn_show_more2)
        go_to_board3.setOnClickListener{
            val intent=Intent(this,Board3All::class.java)
            startActivity(intent)
        }
        go_to_board4.setOnClickListener{
            val intent=Intent(this,Board4All::class.java)
            startActivity(intent)
        }

        recyclerView3.layoutManager = LinearLayoutManager(this)
        board3List = mutableListOf()

        recyclerView4.layoutManager = LinearLayoutManager(this)
        board4List = mutableListOf()

        adapter3 = Board3Adapter(board3List) { board3 ->
            val intent = Intent(this@Board3_4_All, Board3Detail::class.java).apply {
                putExtra("BOARD_DETAIL3", board3 as Parcelable)
                putExtra("TITLE",board3.title)
                putExtra("UID",board3.userId)
                putExtra("TIME",board3.time)
                putExtra("CONTENT",board3.content)
                putExtra("PEOPLE",board3.numberOfPeople)

            }
            startActivity(intent)
        }
        recyclerView3.adapter=adapter3

        adapter4 = Board4Adapter(board4List) { board4 ->
            val intent = Intent(this@Board3_4_All, Board4Detail::class.java).apply {
                putExtra("BOARD_DETAIL4", board4 as Parcelable)
                putExtra("TITLE",board4.title)
                putExtra("UID",board4.userId)
                putExtra("TIME",board4.time)
                putExtra("CONTENT",board4.content)
                putExtra("PEOPLE",board4.numberOfPeople)

            }
            startActivity(intent)
        }
        recyclerView4.adapter=adapter4


        dbRef3 = FirebaseDatabase.getInstance().getReference("Board3")
        dbRef3.addValueEventListener(object : ValueEventListener {
            override  fun onDataChange(snapshot: DataSnapshot) {
                board3List.clear()
                snapshot.children.mapNotNullTo(board3List) { dataSnapshot ->
                    val board3 = dataSnapshot.getValue(Board3::class.java)
                    board3?.let { boardItem ->
                        val uid = boardItem.userId // Board2의 userId 가져옴

                        // 사용자 정보 가져오는 함수 호출
                        if (uid != null) {
                            fetchUserInfo(uid) { userName, userProfileImageUrl ->
                                // 사용자 정보를 Board2 객체에 설정

                                runOnUiThread {
                                    adapter3.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                    board3 // 수정된 변수 반환
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error fetching data", Toast.LENGTH_LONG).show()
            }
        })

        dbRef4 = FirebaseDatabase.getInstance().getReference("Board4")
        dbRef4.addValueEventListener(object : ValueEventListener {
            override  fun onDataChange(snapshot: DataSnapshot) {
                board3List.clear()
                snapshot.children.mapNotNullTo(board4List) { dataSnapshot ->
                    val board4 = dataSnapshot.getValue(Board4::class.java)
                    board4?.let { boardItem ->
                        val uid = boardItem.userId // Board2의 userId 가져옴

                        // 사용자 정보 가져오는 함수 호출
                        if (uid != null) {
                            fetchUserInfo(uid) { userName, userProfileImageUrl ->
                                // 사용자 정보를 Board2 객체에 설정

                                runOnUiThread {
                                    adapter4.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                    board4 // 수정된 변수 반환
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