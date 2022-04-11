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
    val points: Int? = null,
    val time: Int,
    val time_ago: String,
    val title: String,
    val type: String,
    var url: String? = "",
    var user: String? = ""
): Serializable {
    fun source(): String {
        if (user == null){user = ""}
        return StringBuilder().append(user).append(" - ").append( TimeUtil.getDateTime(time.toString()))
            .toString()
    }
}