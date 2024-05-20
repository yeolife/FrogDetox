package com.ssafy.frogdetox.common

import android.content.Context
import android.content.SharedPreferences

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
}