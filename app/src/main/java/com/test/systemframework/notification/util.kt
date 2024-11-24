package com.test.systemframework.notification

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class Util {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun convertEpochLongToString(epochLong: Long): String {
            val instant = Instant.ofEpochMilli(epochLong)
            val zoneId = ZoneId.systemDefault()
            val localDate = instant.atZone(zoneId).toLocalDateTime()
            val formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm")
            return localDate.format(formatter)
        }
    }

}


fun timestampToDateTimeString(timestamp: Long): String {
    try {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault())
        return format.format(date)
    }catch (e: Exception){
        return timestamp.toString()
    }

}