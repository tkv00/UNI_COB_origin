package com.example.uni_cob.department.NaturalScience

import com.example.uni_cob.department.keywords.ApiService
import com.example.uni_cob.department.keywords.Lecture
import com.example.uni_cob.department.keywords.LectureDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
//인문과학
interface ApiService_humanities{
    companion object{
        private const val authKey="BLfPhU3GpfIqwUrvP5dMa8vQ+yqXkP/mX+4Cx/nA64012Y77o9wxwLSNB4JwaaGXQqKe4s56GF1FDauYkhI7UQ=="
    }

    @GET("15067388/v1/uddi:6345bbf5-25c3-4a95-8268-daf12fbe0cc2?page=1&perPage=3096")
    fun getLectureDetail(
        @Query("page")
        page: Int =1,
        @Query("perPage")
        perPage:Int=3096,
        @Query("serviceKey")
        serviceKey: String = authKey
    ): Call<LectureDTO>
    @GET("15067388/v1/uddi:6345bbf5-25c3-4a95-8268-daf12fbe0cc2?page=1&perPage=3096")
    fun getHumanitiesApi(
        @Query("serviceKey") serviceKey: String= authKey
    ):Call<LectureDTO>
}