package com.example.uni_cob.FindFriend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.uni_cob.MessageActivity
import com.example.uni_cob.R
import com.example.uni_cob.utility.User
import com.example.uni_cob.utility.UserStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FriendProfile : AppCompatActivity() {
    private lateinit var btnCom: Button
    private lateinit var btnReq: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_profile)

        val user = intent.getSerializableExtra("USER_INFO") as? User

        findViewById<TextView>(R.id.friendProfile_name).text = user?.name
        findViewById<TextView>(R.id.friendProfile_uni).text = "${user?.schoolName}  ${user?.selectedGrade}"
        findViewById<TextView>(R.id.friendProfile_department).text = user?.department
        findViewById<TextView>(R.id.et_friend1).text="${user?.name}님의 글"
        btnCom = findViewById(R.id.btn_friendChat)
        btnReq = findViewById(R.id.btn_friendReq)
        btnCom.setOnClickListener {
            changeButtonStyle(btnCom, btnReq)
            startMessageActivity(user)}
        btnReq.setOnClickListener {
            changeButtonStyle(btnReq, btnCom)
            sendFriendRequest(user)
                }
        

        val profileImageView = findViewById<ImageView>(R.id.register_profile1)
        user?.profileImageUrl?.let { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.user) // 기본 프로필 이미지
                .into(profileImageView)
        }
    }
    private fun startMessageActivity(user: User?) {
        user?.let {
            val intent = Intent(this, MessageActivity::class.java).apply {
                putExtra("destinationUid", user.uid)
                putExtra("destinationName", user.name)
                putExtra("destinationProfileImageUrl", user.profileImageUrl)
            }
            startActivity(intent)
        }
    }
    private fun sendFriendRequest(toUser: User?) {
        val fromUserId = FirebaseAuth.getInstance().currentUser?.uid
        val toUserId = toUser?.uid

        if (fromUserId != null && toUserId != null) {
            val database = FirebaseDatabase.getInstance().getReference("users")

            // B의 데이터베이스에 친구 요청 정보 저장
            val friendRequest = FriendRequest(fromUserId, toUserId, UserStatus.REQUEST)
            database.child(toUserId).child("FriendRequests").child(fromUserId).setValue(friendRequest)
                .addOnSuccessListener {
                    Toast.makeText(this,"친구 신청 완료!",Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"친구 신청 실패!",Toast.LENGTH_SHORT).show()
                }
        }
    }




    private fun changeButtonStyle(clickedButton: Button, otherButton: Button) {
        // 변경할 버튼 스타일 설정
        clickedButton.setBackgroundResource(R.drawable.btn_friend_onclick) // 예시 스타일
        clickedButton.setTextColor(resources.getColor(R.color.white)) // 예시 텍스트 색상

        // 다른 버튼을 기본 스타일로 설정
        otherButton.setBackgroundResource(R.drawable.btn_friend_noclick) // 예시 스타일
        otherButton.setTextColor(resources.getColor(R.color.skyblue)) // 예시 텍스트 색상
    }
}