package com.example.uni_cob.department.NaturalScience

import com.example.uni_cob.department.keywords.ApiService
import com.example.uni_cob.department.keywords.Lecture
import com.example.uni_cob.department.keywords.LectureDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
//공학
interface ApiService_Education{
    companion object{
        private const val authKey="BLfPhU3GpfIqwUrvP5dMa8vQ+yqXkP/mX+4Cx/nA64012Y77o9wxwLSNB4JwaaGXQqKe4s56GF1FDauYkhI7UQ=="
    }

    @GET("15067378/v1/uddi:7d599be1-78e4-4c41-a0bf-b40157b5149f?page=1&perPage=2103")
    fun getLectureDetail(
        @Query("page")
        page: Int =1,
        @Query("perPage")
        perPage:Int=2103,
        @Query("serviceKey")
        serviceKey: String = authKey
    ): Call<LectureDTO>
    @GET("15067378/v1/uddi:7d599be1-78e4-4c41-a0bf-b40157b5149f?page=1&perPage=2103")
    fun getHumanitiesApi(
        @Query("serviceKey") serviceKey: String= authKey
    ):Call<LectureDTO>
}