package com.ssafy.frogdetox.data.model

import android.graphics.drawable.Drawable

data class AppInfoDto (
    val appIcon: Drawable,
    val appTitle: String,
    val appPackage: String,
)