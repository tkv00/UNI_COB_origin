package com.example.uni_cob.department.Medical


import com.example.uni_cob.department.keywords.LectureDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
//인문과학
interface ApiService_medical{
    companion object{
        private const val authKey="BLfPhU3GpfIqwUrvP5dMa8vQ+yqXkP/mX+4Cx/nA64012Y77o9wxwLSNB4JwaaGXQqKe4s56GF1FDauYkhI7UQ=="
    }

    @GET("15067385/v1/uddi:b72ed125-a2c5-4c5c-b4d8-9cdcd4ef9175?page=1&perPage=928")
    fun getLectureDetail(
        @Query("page")
        page: Int =1,
        @Query("perPage")
        perPage:Int=928,
        @Query("serviceKey")
        serviceKey: String = authKey
    ): Call<LectureDTO>
    @GET("15067385/v1/uddi:b72ed125-a2c5-4c5c-b4d8-9cdcd4ef9175?page=1&perPage=928")
    fun getHumanitiesApi(
        @Query("serviceKey") serviceKey: String= authKey
    ):Call<LectureDTO>
}