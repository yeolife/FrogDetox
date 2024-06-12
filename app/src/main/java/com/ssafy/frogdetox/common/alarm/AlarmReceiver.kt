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
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.ssafy.frogdetox.view.LoginActivity
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.common.SharedPreferencesManager
import com.ssafy.frogdetox.domain.FrogDetoxDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

private const val TAG = "AlarmReceiver_싸피"
class AlarmReceiver : BroadcastReceiver() {
    private lateinit var manager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var alarmManager: AlarmManager
    private val coroutineScope by lazy { CoroutineScope(Dispatchers.IO) }

    private val CHANNEL_ID = "TodayAlarm"
    private val CHANNEL_NAME = "Alarm"
    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context, intent: Intent?) {
        val db = FrogDetoxDatabase.getInstance(context)

        if(intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // TODO 재부팅 알람 재등록
            Log.d(TAG, "onReceive: 재부팅팅!!")
            coroutineScope.launch {
                val list = db!!.todoAlarmDao().getAllTodoAlarm()
                val size = db.todoAlarmDao().getAllTodoAlarm().size
                list.let {
                    for (i in 0 until size){
                        val time = list[i].time
                        val code = list[i].alarm_code
                        val content = list[i].content
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd H:mm:ss")
                        var datetime = Date()
                        try {
                            datetime = dateFormat.parse(time)
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }

                        // LocalDateTime을 Date로 변환
                        var nowtime = LocalDateTime.now()
                        val nowtimeAsDate = Date.from(nowtime.atZone(ZoneId.systemDefault()).toInstant())

                        if(datetime > nowtimeAsDate){
                            // datetime이 nowtimeAsDate보다 이후일 때 실행될 코드
                            alarmManager.callAlarm(time, code, content)
                        }
                        else{//db에서 삭제
                            db.todoAlarmDao().delete(code)
                            Log.d(TAG, "onReceive: callAlarm 알람 삭제했다~ code는 $code")
                        }
                    }
                }
            }

        } else {
            manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
            val content = intent.extras!!.getString("content")

            val pendingIntent = if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
                PendingIntent.getActivity(context,requestCode,intent2,PendingIntent.FLAG_IMMUTABLE); //Activity를 시작하는 인텐트 생성
            }else {
                PendingIntent.getActivity(context,requestCode,intent2,PendingIntent.FLAG_UPDATE_CURRENT);
            }

            val notification = builder.setContentTitle("하지마세요! 개굴")
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
            coroutineScope.launch {
                db!!.todoAlarmDao().delete(requestCode)
            }
        }
    }
}