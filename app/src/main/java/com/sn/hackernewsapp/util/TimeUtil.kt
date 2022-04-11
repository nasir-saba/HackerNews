package com.sn.hackernewsapp.util

import java.text.SimpleDateFormat
import java.util.*

class TimeUtil {
    companion object {
        fun getDateTime(s: String): String? {
            return try {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
                val netDate = Date(s.toLong() * 1000)
                sdf.format(netDate)
            } catch (e: Exception) {
                e.toString()
            }
        }
    }

}