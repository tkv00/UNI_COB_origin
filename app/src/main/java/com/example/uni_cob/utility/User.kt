package com.example.uni_cob.utility
import com.google.firebase.database.FirebaseDatabase
import com.bumptech.glide.Glide
import android.widget.ImageView
import android.widget.TextView
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.uni_cob.R
import java.io.Serializable

// 사용자 정보를 담는 데이터 클래스입니다.
data class User(
    val department: String? = null,
    val email: String? = null,
    val name: String? = null,
    val password: String? = null,
    val profileImageUrl: String? = null,
    val schoolName: String? = null,
    val selectedGrade: String? = null,
    val stNumber: String? = null,
    var uid: String? = null,
    val userPhoneNumber: String? = null // 'userPhoneNumbergs'에서 'userPhoneNumber'로 수정
): Serializable
enum class UserStatus {
    NONE, FRIEND, DECLINED,REQUEST, HIDDEN
}

