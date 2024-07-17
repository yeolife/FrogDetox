package com.ssafy.frogdetox.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

private const val TAG = "SharedPreferencesManage_싸피"
object SharedPreferencesManager {
    private lateinit var preferences: SharedPreferences
    private const val SHARED_PREFERENCES_NAME = "todo_preference"

    fun init(context: Context) {
        preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    //사용자 정보 저장
    fun putUId(id:String){
        preferences.edit().apply {
            putString("id", id).apply()
        }
    }

    fun getUId(): String? {
        return preferences.getString("id", "")
    }

    fun putUserName(name: String) {
        preferences.edit().apply {
            putString("name", name).apply()
        }
    }

    fun getUserName(): String? {
        return preferences.getString("name", "")
    }

    fun putUserProfile(src: String) {
        preferences.edit().apply {
            putString("src", src).apply()
        }
    }

    fun getUserProfile(): String? {
        return preferences.getString("src", "")
    }

    fun clearPreferences() {
        preferences.edit().clear().apply()
    }

    fun putSleepState(state:Boolean){
        preferences.edit().apply(){
            putBoolean("sleepState",state).apply()
        }
    }
    fun getSleepState():Boolean{
        return preferences.getBoolean("sleepState",false)
    }
    fun putCount(count:Int){
        preferences.edit().apply(){
            putInt("count",count).apply()
        }
        Log.d(TAG, "putCount: count $count 설정함.")
    }
    fun getCount():Int{
        return preferences.getInt("count",10)
    }

    //detox sleep hour
    fun putHour(hour:Int){
        preferences.edit().apply {
            putInt("hour", hour).apply()
        }
    }

    fun getHour(): Int {
        return preferences.getInt("hour", -1)
    }

    //detox sleep minute
    fun putMinute(minute:Int){
        preferences.edit().apply {
            putInt("minute", minute).apply()
        }
    }

    fun getMinute(): Int {
        return preferences.getInt("minute", 0)
    }
}