package com.sn.hackernewsapp.models

data class Comment(
    val comments: ArrayList<CommentModel>,
    val comments_count: Int,
    val content: String,
    val id: Int,
    val level: Int,
    val time: Int,
    val time_ago: String,
    val type: String,
    val url: String,
    val user: String
)