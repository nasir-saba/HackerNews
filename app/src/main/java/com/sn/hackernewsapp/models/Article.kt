package com.sn.hackernewsapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sn.hackernewsapp.util.TimeUtil
import java.io.Serializable
import java.lang.StringBuilder
@Entity(
    tableName = "articles"
)
data class Article(
    @PrimaryKey(autoGenerate = true)
    var articleId: Int? = null,
    val comments_count: Int,
    val domain: String? = "",
    val id: Int,
    val points: Int,
    val time: Int,
    val time_ago: String,
    val title: String,
    val type: String,
    var url: String,
    val user: String
): Serializable {
    fun source(): String {
        return StringBuilder().append(user).append(" - ").append( TimeUtil.getDateTime(time.toString()))
            .toString()
    }
}