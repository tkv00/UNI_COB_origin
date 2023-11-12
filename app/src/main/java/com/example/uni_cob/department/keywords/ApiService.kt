package com.example.uni_cob.department.keywords

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
//예술*체육Api
interface ApiService {
    companion object{
        private const val authKey="BLfPhU3GpfIqwUrvP5dMa8vQ+yqXkP/mX+4Cx/nA64012Y77o9wxwLSNB4JwaaGXQqKe4s56GF1FDauYkhI7UQ=="
    }

    @GET("15067383/v1/uddi:39e30ec5-f1a5-4e9f-b38f-918c9956d68d?page=1&perPage=853")
    fun getLectureDetail(
        @Query("page")
        page: Int =1,
        @Query("perPage")
        perPage:Int=853,
        @Query("serviceKey")
        serviceKey: String = authKey
    ): Call<LectureDTO>
}