package com.example.uni_cob.department.keywords

import com.google.gson.annotations.SerializedName

data class LectureDTO(
    @SerializedName("page")
    val page:Int,
    @SerializedName("perPage")
    val perPage:Int,
    @SerializedName("totalCount")
    val totalCount:Int,
    @SerializedName("currentCount")
    val currentCount:Int,
    @SerializedName("matchCount")
    val matchCount:Int,
    @SerializedName("data")
    val data:MutableList<Lecture>
)

data class Lecture(
    @SerializedName("중분류")//중분류
    val classification:String?,
    @SerializedName("대분류")
    val classification2: String?,
    @SerializedName("학년학기")
    val time:String?,
    @SerializedName("교수")//교슈
    val teacher:String?,
    @SerializedName("대학")
    val where:String?,
    @SerializedName("강의명")
    val lecturnName:String?,
    @SerializedName("바로가기 URL")//바로가주
    val getUrl:String?
)
