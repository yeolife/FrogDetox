package com.ssafy.frogdetox.util

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object timeUtil {
    @SuppressLint("NewApi")
    val currentMillis = LocalDateTime.now()
        .atZone(ZoneId.systemDefault())
        .toInstant()?.toEpochMilli() ?: 0

    @RequiresApi(Build.VERSION_CODES.O)
    fun compareDay(time1: Long, time2: Long): Boolean {
        val Time1 =
            Instant.ofEpochMilli(time1).atZone(ZoneId.systemDefault()).toLocalDateTime()
        val format1 = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(Time1)

        val Time2 =
            Instant.ofEpochMilli(time2).atZone(ZoneId.systemDefault()).toLocalDateTime()
        val format2 = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(Time2)

        if(format1 == format2)
            return true
        else
            return false
    }
}