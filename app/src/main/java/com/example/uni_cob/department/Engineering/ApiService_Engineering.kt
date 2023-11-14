package com.example.uni_cob.department.NaturalScience

import com.example.uni_cob.department.keywords.ApiService
import com.example.uni_cob.department.keywords.Lecture
import com.example.uni_cob.department.keywords.LectureDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
//공학
interface ApiService_Engineering{
    companion object{
        private const val authKey="BLfPhU3GpfIqwUrvP5dMa8vQ+yqXkP/mX+4Cx/nA64012Y77o9wxwLSNB4JwaaGXQqKe4s56GF1FDauYkhI7UQ=="
    }

    @GET("15067374/v1/uddi:a1d8baee-34d8-49c2-a078-df6d70081b9c?page=1&perPage=3251")
    fun getLectureDetail(
        @Query("page")
        page: Int =1,
        @Query("perPage")
        perPage:Int=3251,
        @Query("serviceKey")
        serviceKey: String = authKey
    ): Call<LectureDTO>
    @GET("15067374/v1/uddi:a1d8baee-34d8-49c2-a078-df6d70081b9c?page=1&perPage=3251")
    fun getHumanitiesApi(
        @Query("serviceKey") serviceKey: String= authKey
    ):Call<LectureDTO>
}