package com.ssafy.frogdetox.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.ssafy.frogdetox.dto.AlarmData
import java.util.Calendar

class TodoAlarmManager(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    fun setAlarm(hour : Int, minute : Int, alarm_code : Int, content : String, checkedDayList:MutableList<Boolean>){
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm_rqCode", alarm_code)
            putExtra("content", content)
            putExtra("checkedDayList", checkedDayList.toBooleanArray())
        }

        // 알람 동작 시 실행 할 인텐트
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            PendingIntent.getBroadcast(context, alarm_code, intent, PendingIntent.FLAG_IMMUTABLE)
        }else{
            PendingIntent.getBroadcast(context, alarm_code, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        // 트리거 시간 ( 실제 시간 )
        val calendar : Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager?.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            //val alarmClock = AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent)
            //alarmManager?.setAlarmClock(alarmClock, pendingIntent)
        }
    }

    fun cancelAlarm(alarm_code : Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(context,alarm_code, intent, PendingIntent.FLAG_IMMUTABLE)
        } else{
            PendingIntent.getBroadcast(context,alarm_code, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        alarmManager?.cancel(pendingIntent)
    }
}