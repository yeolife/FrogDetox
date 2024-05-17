package com.ssafy.frogdetox.util.alarm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ssafy.frogdetox.LoginActivity
import com.ssafy.frogdetox.MainActivity
import com.ssafy.frogdetox.R

class AlarmReceiver : BroadcastReceiver() {
    private lateinit var manager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder

    private val CHANNEL_ID = "TodayAlarm"
    private val CHANNEL_NAME = "Alarm"
    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context?, intent: Intent?) {
        manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )

        builder = NotificationCompat.Builder(context, CHANNEL_ID)

        val intent2 = Intent(context, LoginActivity::class.java)
        val requestCode = intent?.extras!!.getInt("alarm_rqCode")
        val content = intent.extras!!.getString("content")

        val pendingIntent = if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
            PendingIntent.getActivity(context,requestCode,intent2,PendingIntent.FLAG_IMMUTABLE); //Activity를 시작하는 인텐트 생성
        }else {
            PendingIntent.getActivity(context,requestCode,intent2,PendingIntent.FLAG_UPDATE_CURRENT);
        }

        val notification = builder.setContentTitle("청깨구리")
            .setContentText(content)
            .setSmallIcon(R.drawable.cutefrogicon)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(1, notification)
    }
}