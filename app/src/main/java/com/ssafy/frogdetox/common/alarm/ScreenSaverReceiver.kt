package com.ssafy.frogdetox.common.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ssafy.frogdetox.view.detox.ScreenSaverActivity

private const val TAG = "ScreenSaverReceiver_싸피"
class ScreenSaverReceiver :BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "onReceive: 브로드캐스트 뭐 받아서 실행됨요")
        val screenSaverIntent = Intent(context, ScreenSaverActivity::class.java)
        screenSaverIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(screenSaverIntent)
    }
}