package com.ssafy.frogdetox.common

import android.content.Context
import android.content.SharedPreferences
import com.ssafy.frogdetox.view.todo.DataItem.Header.id

class SharedPreferencesUtil (context: Context) {
    private val SHARED_PREFERENCES_NAME = "todo_preference"

    var preferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    //사용자 정보 저장
    fun putUId(id:String){
        val editor = preferences.edit()
        editor.putString("id", id)
        editor.apply()
    }

    fun getUId(): String? {
        return preferences.getString("id", "")
    }

    //detox sleep hour
    fun putHour(hour:Int){
        val editor = preferences.edit()
        editor.putInt("hour", hour)
        editor.apply()
    }

    fun getHour(): Int {
        return preferences.getInt("hour", 11)
    }

    //detox sleep minute
    fun putMinute(minute:Int){
        val editor = preferences.edit()
        editor.putInt("minute", minute)
        editor.apply()
    }

    fun getMinute(): Int {
        return preferences.getInt("minute", 0)
    }

}