package com.sn.hackernewsapp.ui.main

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.*
import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Email.TYPE_MOBILE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sn.hackernewsapp.HackerNewsApplication
import com.sn.hackernewsapp.models.Article
import com.sn.hackernewsapp.models.ArticleInternal
import com.sn.hackernewsapp.repository.HackerNewsRepository
import com.sn.hackernewsapp.util.Resource
import com.sn.hackernewsapp.util.Resource.Error
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class HackerNewsViewModel(
    private val hackerNewsRepository: HackerNewsRepository, app: Application
) : AndroidViewModel(app) {
    val article: MutableLiveData<Resource<Article>> = MutableLiveData()
    val articleInternal: MutableLiveData<Resource<ArticleInternal>> = MutableLiveData()
    val articles: MutableLiveData<Resource<ArrayList<Article>>> = MutableLiveData()
    var hackerNewsPage = 1
    var articlesResponse: ArrayList<Article>? = null


    fun getNewArticles() = viewModelScope.launch {
        safeNewArticlesCall()
    }

    fun getArticleExternal(articleId: String) = viewModelScope.launch {
        safeExternalArticlesCall(articleId)
    }

    fun getArticleInternal(articleId: String) = viewModelScope.launch {
        safeInternalArticlesCall(articleId)
    }

    private fun handleNewArticlesResponse(response: Response<ArrayList<Article>>): Resource<ArrayList<Article>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                hackerNewsPage++
                if(articlesResponse == null) {
                    articlesResponse = resultResponse
                } else {

                    val oldArticles = articlesResponse
                    val newArticles = resultResponse
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(articlesResponse ?: resultResponse)
            }
        }
        return Error(response.message())
    }

    private fun handleExternalArticleResponse(response: Response<Article>): Resource<Article> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Error(response.message())
    }

    private fun handleArticleInternalResponse(response: Response<ArticleInternal>): Resource<ArticleInternal> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Error(response.message())
    }

    private suspend fun safeInternalArticlesCall(articleId: String) {
        articleInternal.postValue(Resource.Loading())

        try {
            if (hasInternetConnection()) {
                val response = hackerNewsRepository.getArticleInternal(articleId)
                articleInternal.postValue(handleArticleInternalResponse(response))
            } else {
                articleInternal.postValue(Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> articleInternal.postValue(Error("Network Failure"))
                else -> articleInternal.postValue(Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeExternalArticlesCall(articleId: String) {
        article.postValue(Resource.Loading())

        try {
            if (hasInternetConnection()) {
                val response = hackerNewsRepository.getArticleExternal(articleId)
                article.postValue(handleExternalArticleResponse(response))
            } else {
                article.postValue(Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> article.postValue(Error("Network Failure"))
                else -> article.postValue(Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeNewArticlesCall() {
        articles.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = hackerNewsRepository.getNewArticles(hackerNewsPage)
                articles.postValue(handleNewArticlesResponse(response))
            } else {
                articles.postValue(Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> articles.postValue(Error("Network Failure"))
                else -> articles.postValue(Error("Conversion Error"))
            }
        }
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        hackerNewsRepository.upsert(article)
    }

    fun getSavedArticles() = hackerNewsRepository.getSavedArticles()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        hackerNewsRepository.deleteArticle(article)
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<HackerNewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}