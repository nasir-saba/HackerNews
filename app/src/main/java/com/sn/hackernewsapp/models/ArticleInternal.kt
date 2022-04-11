package com.sn.hackernewsapp.models

import com.sn.hackernewsapp.util.TimeUtil
import java.io.Serializable
import java.lang.StringBuilder

data class ArticleInternal(
    val comments: ArrayList<Comment>,
    val comments_count: Int,
    val content: String,
    val id: Int,
    val points: Int,
    val time: Int,
    val time_ago: String,
    val title: String,
    val type: String,
    val url: String,
    val user: String
): Serializable