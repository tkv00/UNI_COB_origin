package com.example.uni_cob

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.uni_cob.utility.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MyProfile : AppCompatActivity() {
    private lateinit var name:TextView
    private lateinit var university:TextView
    private lateinit var et_department:TextView
    private lateinit var et_grade:TextView
    private lateinit var btn_friend:Button
    private lateinit var btn_list1:Button
    private lateinit var btn_list2:Button
    private lateinit var btn_logout:Button
    private lateinit var imageview:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        name=findViewById(R.id.name_et)
        university=findViewById(R.id.uni_et)
        et_department=findViewById(R.id.uni_dep)
        et_grade=findViewById(R.id.textView35)
        btn_friend=findViewById(R.id.friend)//친구목록
        btn_list1=findViewById(R.id.list1)//신청내역
        btn_list2=findViewById(R.id.list2)//내 게시물
        btn_logout=findViewById(R.id.log)
        imageview=findViewById(R.id.profile_image)


        val uid= Firebase.auth.currentUser?.uid
        val usersRef = FirebaseDatabase.getInstance().getReference("users")

        if (uid != null) {
            usersRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(User::class.java)
                        val userName = user?.name
                        val uni=user?.schoolName
                        val department=user?.department
                        val grade=user?.selectedGrade

                        name.text=userName
                        university.text=uni
                        et_department.text=department
                        et_grade.text=grade
                        Glide.with(imageview)
                            .load(user?.profileImageUrl)
                            .error(R.drawable.image1)
                            .into(imageview)
                    } else {
                        // 일치하는 사용자 정보가 없을 경우 처리

                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    // 오류 처리
                    Log.e("FetchUserInfoError", "Error fetching user info: ${error.message}")
                }
            })
        }

        //로그아웃
        btn_logout.setOnClickListener {
            // Create an AlertDialog Builder
            AlertDialog.Builder(this).apply {
                setTitle("Logout Confirmation")
                setMessage("정말로 로그아웃하시겠어요?")
                // If the user clicks "Yes", log them out and return to the first screen
                setPositiveButton("네") { _, _ ->
                    // Perform Firebase logout
                    Firebase.auth.signOut()

                    // Navigate back to the first activity
                    val intent = Intent(this@MyProfile, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                // If the user clicks "No", dismiss the dialog and do nothing
                setNegativeButton("아니요") { dialog, _ ->
                    dialog.dismiss()
                }
                // Show the AlertDialog
                show()
            }
        }

        //자신이 신청한 게시글목록 띄우기
        btn_list1.setOnClickListener{
            val intent=Intent(this,ShowMyApplication::class.java)
            startActivity(intent)
        }

        //친구목록
        btn_friend.setOnClickListener{
            val intent=Intent(this,FriendList::class.java)
            intent.putExtra("UserId",uid)
            startActivity(intent)
        }







    }
}