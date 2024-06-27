package com.ssafy.frogdetox.common

import android.content.Context
import android.content.pm.PackageManager

object LocalAPIKey {
    fun getSecretKey(context: Context, info: String): String {
        return try {
            val applicationInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            val bundle = applicationInfo.metaData
            bundle.getString(info) ?: throw IllegalArgumentException("Meta-data not found")
        } catch (e: PackageManager.NameNotFoundException) {
            throw IllegalStateException("Unable to load meta-data: ${e.message}")
        }
    }
}