package com.example.uni_cob.department.SocialScience

import com.example.uni_cob.department.NaturalScience.ApiService_humanities
import com.example.uni_cob.department.keywords.Lecture
import com.example.uni_cob.department.keywords.LectureDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
//인문과학
interface ApiService_SocialScience{
    companion object{


        private const val authKey="BLfPhU3GpfIqwUrvP5dMa8vQ+yqXkP/mX+4Cx/nA64012Y77o9wxwLSNB4JwaaGXQqKe4s56GF1FDauYkhI7UQ=="
    }

    @GET("15067381/v1/uddi:950b9060-466a-4a66-b2f5-d7c958aa9dbf?page=1&perPage=3916")
    fun getLectureDetail(
        @Query("page")
        page: Int =1,
        @Query("perPage")
        perPage:Int=3916,
        @Query("serviceKey")
        serviceKey: String = authKey
    ): Call<LectureDTO>
    @GET("15067381/v1/uddi:950b9060-466a-4a66-b2f5-d7c958aa9dbf?page=1&perPage=3916")
    fun getSocialScienceApi(
        @Query("serviceKey") serviceKey: String= authKey
    ):Call<LectureDTO>
}