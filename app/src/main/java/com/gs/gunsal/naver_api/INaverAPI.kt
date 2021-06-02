package com.ms129.stockPrediction.naverAPI

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface INaverAPI {
    @GET("v1/search/news.json")
    fun getSearchNews(
        @Header("X-Naver-Client-Id") clientId: String = "4X7Z074B7gOxj0qI58lo",
        @Header("X-Naver-Client-Secret") clientSecret: String = "q9QIDTlWlG",
        @Query("query") query: String,
        @Query("display") display: Int? = null,
        @Query("start") start: Int? = null
    ): Call<ResultGetSearchNews>
}