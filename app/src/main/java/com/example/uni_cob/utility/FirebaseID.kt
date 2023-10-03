package com.example.uni_cob.utility

object FirebaseID {
    data class User(
        val name: String,
        val department:String,
        val email: String,
        val userPhoneNumber: String,
        val password: String,
        val stNumber: String,
        val schoolName: String
    )

    // 나머지 상수 필드는 여기에 추가할 수 있습니다.
    const val name="name"
    const val email="email"
    const val userPhoneNumber="userPhoneNumber"
    const val department = "department"
    const val password = "password"
    const val stNumber="stNumber"
    const val schoolName="schoolName"
}
