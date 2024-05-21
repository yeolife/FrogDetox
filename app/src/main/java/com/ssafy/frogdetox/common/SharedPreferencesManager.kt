package com.ssafy.frogdetox.common

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {

    private lateinit var preferences: SharedPreferences
    private const val SHARED_PREFERENCES_NAME = "todo_preference"

    fun init(context: Context) {
        preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    //사용자 정보 저장
    fun putUId(id:String){
        preferences.edit().apply {
            putString("id", id)
            apply()
        }
    }

    fun getUId(): String? {
        return preferences.getString("id", "")
    }

    //detox sleep hour
    fun putHour(hour:Int){
        preferences.edit().apply {
            putInt("hour", hour)
            apply()
        }
    }

    fun getHour(): Int {
        return preferences.getInt("hour", 11)
    }

    //detox sleep minute
    fun putMinute(minute:Int){
        preferences.edit().apply {
            putInt("minute", minute)
            apply()
        }
    }

    fun getMinute(): Int {
        return preferences.getInt("minute", 0)
    }

    // 앱 제한 여부를 저장
    fun getAppState(packageName: String): Boolean {
        return preferences.getBoolean(packageName, false)
    }

    fun setAppState(packageName: String, state: Boolean) {
        preferences.edit().apply {
            putBoolean(packageName, state)
            apply()
        }
    }
}