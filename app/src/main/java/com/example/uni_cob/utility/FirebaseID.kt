package com.example.uni_cob.utility

object FirebaseID {
    data class User(
        val department:String?,
        val email: String?,
        val name: String?,
        val password: String?,
        val profileImageUrl:String?,
        val schoolName: String?,
        val selectedGrade:String?,
        val stNumber: String?,
        var uid:String?,
        val userPhoneNumber: String?
    )



    // 나머지 상수 필드는 여기에 추가할 수 있습니다.
    const val name="name"
    const val email="email"
    const val userPhoneNumber="userPhoneNumber"
    const val department = "department"
    const val password = "password"
    const val stNumber="stNumber"
    const val schoolName="schoolName"
    const val uid="uid"
    const val profileImageUrl="profileImageUrl"
    const val selectedGrade="selectedGrade"
}
