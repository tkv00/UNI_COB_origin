package com.example.uni_cob.writeboard

import android.content.Intent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale




import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uni_cob.R
import com.example.uni_cob.utility.Comment1
import com.example.uni_cob.utility.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Board1Detail : AppCompatActivity() {
    private lateinit var profileImage: ImageView
    private lateinit var commentsAdapter: CommentsAdapter
    private var comments = mutableListOf<Comment1>()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var et_categories:TextView
    private lateinit var btn_back:Button

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board1_detail)
        profileImage = findViewById(R.id.profile)

        // Intent에서 데이터 추출
        val department = intent.getStringExtra("DEPARTMENT") ?: "Unknown"
        val title = intent.getStringExtra("TITLE") ?: ""
        val userName = intent.getStringExtra("NAME") ?: ""
        val content = intent.getStringExtra("CONTENT") ?: ""
        val postId = intent.getStringExtra("POST_ID") ?: ""


        // Views 설정
        findViewById<TextView>(R.id.department_).text = department
        findViewById<TextView>(R.id.et_title).text = title
        findViewById<TextView>(R.id.et_name4).text = userName
        findViewById<TextView>(R.id.textView31).text = content
        btn_back=findViewById(R.id.btn_back)
        btn_back.setOnClickListener{
            val intent=Intent(this,Board1All::class.java)
            startActivity(intent)
        }
        et_categories=findViewById(R.id.et_category)
        // 날짜 포맷팅
        val dateMillis = intent.getLongExtra("TIME", -1)
        findViewById<TextView>(R.id.date).text = if (dateMillis != -1L) {
            SimpleDateFormat("MM월 dd일 HH:mm", Locale.getDefault()).format(Date(dateMillis))
        } else {
            "날짜 정보 없음"
        }

        val uid = intent.getStringExtra("UID") // 'UID' 키로부터 사용자 ID 가져오기
        if (uid != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(uid) // 'users' 노드 아래 'uid'에 해당하는 노드를 참조
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val profileUrl = dataSnapshot.child("profileImageUrl").getValue(String::class.java) // 'profileImageUrl' 필드의 값을 String으로 가져옴
                    if (profileUrl != null && profileUrl.isNotEmpty()) {
                        // Glide를 사용해 프로필 이미지 로드
                        Glide.with(this@Board1Detail)
                            .load(profileUrl)
                            .placeholder(R.drawable.image1) // 로딩 중 표시될 이미지
                            .error(R.drawable.image1) // 로드 실패 시 표시될 이미지
                            .into(profileImage) // 로드할 ImageView
                    } else {
                        // 프로필 이미지 URL이 없거나 빈 경우 기본 이미지 설정
                        profileImage.setImageResource(R.drawable.image1)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 오류 처리, 오류 로깅
                    Log.e("Board1Detail", "Failed to load user profile image", databaseError.toException())
                }
            })
        } else {
            // UID가 null인 경우 기본 이미지 설정
            profileImage.setImageResource(R.drawable.image1)
        }



        // RecyclerView 설정
        val recyclerView = findViewById<RecyclerView>(R.id.comment)
        // 유효한 FirebaseUser 인스턴스(현재 사용자)와 DatabaseReference를 가정합니다.
        val currentUser = FirebaseAuth.getInstance().currentUser
        val databaseReference = FirebaseDatabase.getInstance().getReference()

        commentsAdapter = currentUser?.let { CommentsAdapter(comments, it, databaseReference ) }!!
        recyclerView.adapter = commentsAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 댓글 데이터 로드
        loadComments(postId)

        // 댓글 게시 버튼 클릭 리스너 설정
        findViewById<Button>(R.id.btn_message).setOnClickListener {
            postComment(postId)
        }
        //카테고리 나열
        loadCategories(postId)
    }


    private fun loadComments(postId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("comments")
        databaseReference.orderByChild("postId").equalTo(postId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    comments.clear()
                    snapshot.children.mapNotNullTo(comments) { it.getValue(Comment1::class.java) }
                    commentsAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("loadComments", "Failed to load comments.", databaseError.toException())
                }
            })
    }

    private fun postComment(postId: String) {

        val commentEditText = findViewById<EditText>(R.id.editText)
        val commentText = commentEditText.text.toString().trim()
        if (commentText.isNotEmpty()) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val newComment = Comment1(
                    userId = currentUser.uid,
                    userName = currentUser.displayName ?: "Anonymous",
                    content = commentText,
                    department = "", // This will be fetched and updated later
                    profileImageUrl = "", // This will be fetched and updated later
                    timestamp = System.currentTimeMillis(),
                    postId = postId
                )

                val commentsRef = FirebaseDatabase.getInstance().getReference("comments")
                val newCommentKey = commentsRef.push().key
                newCommentKey?.let {
                    commentsRef.child(it).setValue(newComment).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@Board1Detail, "댓글이 게시되었습니다.", Toast.LENGTH_SHORT)
                                .show()
                            commentEditText.text.clear()
                            loadComments(postId)
                        } else {
                            Toast.makeText(this@Board1Detail, "댓글 게시에 실패했습니다.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "댓글 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }
    //카테고리 나열함수
    private fun loadCategories(postId: String) {
        val postRef = FirebaseDatabase.getInstance().getReference("Board1").child(postId)
        postRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoriesList = snapshot.child("categories").getValue(object : GenericTypeIndicator<List<String>>() {})
                categoriesList?.let { list ->
                    // 첫 번째 줄에 최대 3개의 카테고리만 표시
                    val displayCategories = list.take(5).joinToString("  ") { "#$it" } // 간격을 2개의 공백으로 설정
                    et_categories.text = displayCategories
                    et_categories.text = displayCategories
                } ?: run {
                    et_categories.text = "카테고리 없음"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("loadCategories", "Failed to load categories.", error.toException())
            }
        })
    }
    private fun fetchUserDetails(
        userId: String,
        onUserDetailsFetched: (String, String, String) -> Unit
    ) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                val department =
                    snapshot.child("department").getValue(String::class.java) ?: "Unknown"
                val profileImageUrl =
                    snapshot.child("profileImageUrl").getValue(String::class.java) ?: ""
                onUserDetailsFetched(userName, department, profileImageUrl)
            }

            override fun onCancelled(error: DatabaseError) {
                // 오류 처리
            }
        })
    }
}


