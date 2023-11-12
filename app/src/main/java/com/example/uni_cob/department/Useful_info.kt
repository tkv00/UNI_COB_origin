package com.example.uni_cob.department

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uni_cob.CategoryAdapter
import com.example.uni_cob.Chatting.ChatFragment
import com.example.uni_cob.MainActivity
import com.example.uni_cob.R
import com.example.uni_cob.utility.Category
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Useful_info : AppCompatActivity() {
    private lateinit var btn_to_find_department: Button
    private val auth = FirebaseAuth.getInstance()
    private lateinit var et_name:TextView
    // AuthStateListener 멤버 변수 추가
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        if (firebaseAuth.currentUser != null) {
            // 사용자 정보가 변경되었을 때 UI 업데이트
            updateUserName()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_useful_info)
        // AuthStateListener를 FirebaseAuth 인스턴스에 추가
        auth.addAuthStateListener(authStateListener)


        //하단바 작동액티비티
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->


            when (item.itemId) {
                R.id.menu_home -> {
                    // 홈 메뉴 아이템을 눌렀을 때 MainActivity로 이동
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_chat -> {
                    // 채팅 메뉴 아이템을 눌렀을 때 ChatFragment로 교체
                    val chatFragment = ChatFragment.newInstance()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, chatFragment)
                        .commit()
                    true
                }
                R.id.menu_profile -> {

                    true
                }
                R.id.menu_notification -> {
                    true
                }
                R.id.menu_register -> {
                    true
                }
                else -> false
            }
        }



        btn_to_find_department=findViewById(R.id.button2)
        //세부전공 선택페이지로 이동
        btn_to_find_department.setOnClickListener{
            val intent=Intent(this, find_department::class.java)
            startActivity(intent)

        }
        val categories = listOf(
            Category(
                R.drawable.info_image_1,
                "교육학",
                "http://www.kocw.net/home/search/majorCourses.do#subject/05"
            ),
            Category(
                R.drawable.info_image_2,
                "의약학",
                "http://www.kocw.net/home/search/majorCourses.do#subject/06"
            ),
            Category(
                R.drawable.info_image_3,
                "사회과학",
                "http://www.kocw.net/home/search/majorCourses.do#subject/02"
            ),
            Category(
                R.drawable.info_image_4,
                "에술체육",
                "http://www.kocw.net/home/search/majorCourses.do#subject/07"
            ),
            Category(
                R.drawable.info_image_5,
                "자연과학",
                "http://www.kocw.net/home/search/majorCourses.do#subject/04"
            ),
            Category(
                R.drawable.info_image_6,
                "공학",
                "http://www.kocw.net/home/search/majorCourses.do#subject/03"
            ),
            Category(
                R.drawable.info_image_7,
                "인문과학",
                "http://www.kocw.net/home/search/majorCourses.do#subject/01"
            )
        )

        findViewById<RecyclerView>(R.id.recyclerViewCategories).apply {
            layoutManager =
                LinearLayoutManager(this@Useful_info, LinearLayoutManager.HORIZONTAL, false)
            adapter = CategoryAdapter(this@Useful_info, categories)
        }
        // TextView 초기화 및 사용자 이름 설정
        et_name = findViewById(R.id.et_name2)  // 레이아웃에서 TextView의 ID를 확인하세요
        updateUserName()
    }

    private fun updateUserName() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val database = FirebaseDatabase.getInstance().getReference("users")
            database.orderByChild("uid").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val name = userSnapshot.child("name").getValue(String::class.java)
                            et_name.text = name ?: "익명"
                            break // 첫 번째 일치하는 사용자 이름을 찾으면 반복을 중단합니다.
                        }
                    } else {
                        et_name.text = "익명"
                        Log.d("Firebase", "No matching user found.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@Useful_info, "데이터 로드 실패: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "로그인 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }


}