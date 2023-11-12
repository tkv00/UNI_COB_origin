package com.example.uni_cob.department

    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory

    object RetrofitClient {
        private const val BASE_URL="https://api.odcloud.kr/api/"
        private var instance: Retrofit?=null
        open fun getInstance():Retrofit{
            if(instance==null){
                instance=Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return instance!!
        }

}