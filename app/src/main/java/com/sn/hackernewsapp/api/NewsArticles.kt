package com.sn.hackernewsapp.api

import com.sn.hackernewsapp.models.Article
import com.sn.hackernewsapp.models.ArticleInternal
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface NewsArticles {

    @GET("v0/{id}.json?print=pretty")
    suspend fun getArticleExternal(@Path("id") id: String): Response<Article>

    @GET("v0/{id}.json?print=pretty")
    suspend fun getArticleInternal(@Path("id") id: String): Response<ArticleInternal>

    @GET("v0/news/{pageno}.json")
    suspend fun getNewArticles(@Path("pageno") pageNo: Int): Response<ArrayList<Article>>

}