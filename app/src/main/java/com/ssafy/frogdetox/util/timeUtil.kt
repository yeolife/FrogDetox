package com.ssafy.frogdetox.util

import android.annotation.SuppressLint
import java.time.LocalDateTime
import java.time.ZoneId

object timeUtil {
    @SuppressLint("NewApi")
    val currentMillis = LocalDateTime.now()
        .atZone(ZoneId.systemDefault())
        .toInstant()?.toEpochMilli() ?: 0
}