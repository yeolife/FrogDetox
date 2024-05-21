package com.ssafy.frogdetox.common.alarm

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.ssafy.frogdetox.view.LoginActivity
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.common.SharedPreferencesManager

class AlarmReceiver : BroadcastReceiver() {
    private lateinit var manager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder

    private val CHANNEL_ID = "TodayAlarm"
    private val CHANNEL_NAME = "Alarm"
    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context?, intent: Intent?) {

        if(intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // TODO 재부팅 알람 재등록

        } else {
            manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
            )

            builder = NotificationCompat.Builder(context, CHANNEL_ID)

            val intent2 = Intent(context, LoginActivity::class.java)
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            val requestCode = intent?.extras!!.getInt("alarm_rqCode")
            val content = "오늘 이거는 하지마세요 개굴!\n"+intent.extras!!.getString("content")

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
                .setPriority(NotificationCompat.PRIORITY_HIGH) // 우선순위를 높게 설정
                .setDefaults(NotificationCompat.DEFAULT_ALL) // 기본 설정 (소리, 진동 등)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .build()

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                manager.notify(1, notification)
            }
        }
    }
}