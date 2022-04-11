package com.sn.hackernewsapp.repository

import com.sn.hackernewsapp.api.RetrofitInstance
import com.sn.hackernewsapp.db.ArticleDatabase
import com.sn.hackernewsapp.models.Article

class HackerNewsRepository(val db: ArticleDatabase ){

    suspend fun getNewArticles(pageNo: Int) =
        RetrofitInstance.api.getNewArticles(pageNo)

    suspend fun getArticleExternal(articleId: String) =
        RetrofitInstance.api.getArticleExternal(articleId)

    suspend fun getArticleInternal(articleId: String) =
        RetrofitInstance.api.getArticleInternal(articleId)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedArticles() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}
