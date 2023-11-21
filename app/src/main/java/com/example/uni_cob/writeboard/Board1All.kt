package com.example.uni_cob.writeboard

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uni_cob.R
import com.example.uni_cob.utility.Board1
import com.example.uni_cob.utility.Board2
import com.example.uni_cob.utility.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class Board1All:AppCompatActivity() {
    private lateinit var et_department:TextView
    private val auth = FirebaseAuth.getInstance()
    // AuthStateListener 멤버 변수 추가
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        if (firebaseAuth.currentUser != null) {
            // 사용자 정보가 변경되었을 때 UI 업데이트
            updateUserName()
        }
    }
    private lateinit var btn_register:Button
    private lateinit var recyclerView: RecyclerView
    private var board1List= mutableListOf <Board1>()
    private lateinit var adapter: Board1Adapter
    private lateinit var dbRef: DatabaseReference




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_department_communication)
        // Intent에서 데이터 추출

        // AuthStateListener를 FirebaseAuth 인스턴스에 추가
        auth.addAuthStateListener(authStateListener)
        et_department=findViewById(R.id.department)


        updateUserName()

        btn_register=findViewById(R.id.btn_board1_resister)
        btn_register.setOnClickListener{
            val intent= Intent(this,WriteBoard_base::class.java)
            startActivity(intent)
        }


        dbRef = FirebaseDatabase.getInstance().getReference("Board1")
        // 게시글 데이터 로드

        // RecyclerView 설정
        recyclerView = findViewById(R.id.deparment_list_view_)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = Board1Adapter(board1List) { board1 ->
            // 클릭 리스너 내용
        }
        recyclerView.adapter = adapter

        // 게시글 데이터 로드
        loadBoard1Data()

    }

    private fun loadBoard1Data() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                board1List.clear()
                for (dataSnapshot in snapshot.children) {
                    val board1 = dataSnapshot.getValue(Board1::class.java)
                    board1?.postId = dataSnapshot.key
                    board1?.let { board1List.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error fetching data", Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun updateUserName()  {
        val uid=FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val database = FirebaseDatabase.getInstance().getReference("users").child(uid)
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val department=snapshot.child("department").getValue(String::class.java)

                        et_department.text=department?:"전공없음"


                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@Board1All,
                            "데이터 로드 실패: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } else {
            Toast.makeText(this, "로그인 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
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