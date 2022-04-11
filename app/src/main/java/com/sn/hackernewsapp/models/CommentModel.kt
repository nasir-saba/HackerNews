package com.sn.hackernewsapp.models

data class CommentModel(
    val `by`: String,
    val id: Int,
    val kids: List<Int>,
    val parent: Int,
    val text: String,
    val time: Int,
    val type: String
)