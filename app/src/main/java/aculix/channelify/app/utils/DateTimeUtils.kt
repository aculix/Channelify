package aculix.channelify.app.utils

import android.annotation.SuppressLint
import com.github.marlonlom.utilities.timeago.TimeAgo
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    /**
     * Returns time in ago format
     * Eg. 14 hours ago
     * Eg. 2 days ago
     */
    fun getTimeAgo(timeInIso8601: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        val timeInMillis = sdf.parse(timeInIso8601).time
        return TimeAgo.using(timeInMillis)
    }

    /**
     * Returns data in format MMM dd, yyyy
     * Eg. Dec 02, 2019
     */
    @SuppressLint("SimpleDateFormat")
    fun getPublishedDate(timeInIso8601: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        val date = sdf.parse(timeInIso8601)

        val publishedDateSdf = SimpleDateFormat("MMM dd, yyyy ")
        return publishedDateSdf.format(date)
    }
}