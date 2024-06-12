package com.ssafy.frogdetox.common.alarm

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import androidx.core.content.getSystemService
import com.ssafy.frogdetox.view.detox.ScreenSaverActivity

private const val TAG = "ScreenSaverReceiver_싸피"
class ScreenSaverReceiver :BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent?) {
        val screenSaverIntent = Intent(context, ScreenSaverActivity::class.java)
        if(isOnScreen(context)){
            // 현재 액티비티가 포그라운드에 있는지 확인하는 함수 호출
            val isActivityForeground = isActivityInForeground(context)
            // 현재 액티비티가 포그라운드에 있는지 여부에 따라 플래그 설정
            if (isActivityForeground) {
                screenSaverIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            } else {
                screenSaverIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(screenSaverIntent)
        }
    }
    private fun isOnScreen(context: Context):Boolean{
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        Log.d(TAG, "isOnScreen: ${powerManager.isInteractive} 켜졌는지~")
        return powerManager.isInteractive // 켜져있으면 true
    }
    private fun isActivityInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses ?: return false

        for (processInfo in runningAppProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (processInfo.processName == context.packageName) {
                    return true
                }
            }
        }
        return false
    }
}