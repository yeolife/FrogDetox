package com.ssafy.frogdetox

import android.app.Application
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ssafy.frogdetox.data.local.SharedPreferencesManager

class AppClass: Application() {
    override fun onCreate() {
        super.onCreate()

        SharedPreferencesManager.init(this)

        Firebase.database.setPersistenceEnabled(true)
    }
}