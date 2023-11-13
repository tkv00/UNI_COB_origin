package com.example.uni_cob.department.NaturalScience

import com.example.uni_cob.department.keywords.Lecture
import com.example.uni_cob.department.keywords.LectureDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
//자연과학pi
interface ApiService_natural_science {
    companion object{
        private const val authKey="BLfPhU3GpfIqwUrvP5dMa8vQ+yqXkP/mX+4Cx/nA64012Y77o9wxwLSNB4JwaaGXQqKe4s56GF1FDauYkhI7UQ=="
    }

    @GET("15067391/v1/uddi:2a5f4150-71ff-4cda-968c-fe1501d6b677?page=1&perPage=2307")
    fun getLectureDetail(
        @Query("page")
        page: Int =1,
        @Query("perPage")
        perPage:Int=2307,
        @Query("serviceKey")
        serviceKey: String = authKey
    ): Call<LectureDTO>
    @GET("15067391/v1/uddi:2a5f4150-71ff-4cda-968c-fe1501d6b677?page=1&perPage=2307")
    fun getNaturalScience(
        @Query("serviceKey") serviceKey: String= authKey
    ):Call<LectureDTO>
}