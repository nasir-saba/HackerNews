package com.sn.hackernewsapp.util

import com.sn.hackernewsapp.util.Constants.Companion.DATE_FORMAT
import java.text.SimpleDateFormat
import java.util.*

class TimeUtil {
    companion object {
        fun getDateTime(s: String): String? {
            return try {
                val sdf = SimpleDateFormat(DATE_FORMAT)
                val netDate = Date(s.toLong() * 1000)
                sdf.format(netDate)
            } catch (e: Exception) {
                e.toString()
            }
        }
    }

}