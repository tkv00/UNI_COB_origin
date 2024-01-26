package com.example.uni_cob.utility

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ApplicationInfo(
    var name: String? = null, // 신청자 이름
    var email: String? = null, // 신청자 이메일
    var phone: String? = null // 신청자 전화번호
) : Parcelable