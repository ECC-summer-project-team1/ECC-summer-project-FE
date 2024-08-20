package com.example.toiletfinder

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("/api/toilets/search/location")
    fun sendRadiusData(
        @Part file: MultipartBody.Part,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
        @Part("radius") radius: RequestBody
    ): Call<ResponseBody>

    @POST("/api/search")
    fun search(@Body query: String): Call<SearchResponse>
}