package com.ssafy.frogdetox.view.detox

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.view.LoginActivity
import com.ssafy.frogdetox.view.MainActivity
import java.util.Random

private const val TAG = "ScreenSaverService_싸피"

data class ImageViewData(
    val imageView: ImageView,
    val speed : Float = (Random().nextInt(5)+2).toFloat()
)
class ScreenSaverService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: LinearLayout
    private var realWidth = 0
    private var realHeight = 0
    @RequiresApi(Build.VERSION_CODES.R)
    private fun getScreenSize() {
        val matric = windowManager.currentWindowMetrics
        realWidth = matric.bounds.width()
        realHeight = matric.bounds.height()
        Log.d(TAG, "getScreenSize: $realWidth $realHeight")
    }
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("InflateParams")
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        overlayView = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundResource(android.R.color.transparent)
            orientation = LinearLayout.VERTICAL
        }
        // TextView 생성 및 추가
        val textView = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
            text = "개구리를 10마리 만들고, 10마리를 모두 터치하시고 주무세요!!!!!개굴!!"
            textSize = 20f
            setTextColor(resources.getColor(android.R.color.white, null))
            setBackgroundColor(Color.parseColor("#60000000")) // 배경색 설정 (반투명 검정)
            setPadding(16, 16, 16, 16) // 패딩 추가
        }
        overlayView.addView(textView)
        initSensor()
        getScreenSize()
        initTouchListener()
        sensorManager.registerListener(listener,sensor,SensorManager.SENSOR_DELAY_NORMAL)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FORMAT_CHANGED
        )
        params.gravity = Gravity.CENTER
        windowManager.addView(overlayView, params)
    }
    private lateinit var sensorManager: SensorManager
    private var sensor : Sensor? = null
    private fun initSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }
    var frogcount=0
    var deletecount=10
    @SuppressLint("ClickableViewAccessibility")
    private fun initTouchListener() {
        overlayView.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_DOWN){
                if(frogcount<10){
                    setImageView(event.x,event.y)
                    frogcount++
                }
            }
            true
        }
    }
    private var imgList = arrayListOf<ImageViewData>()
    private fun setImageView(x: Float, y: Float) {
        Log.d(TAG, "setImageView: $x $y 에 생성됨.")
        ImageView(this).apply {
            setBackgroundResource(if(frogcount%2==0) R.drawable.gosleepfrog else R.drawable.cutefrogicon)
            layoutParams= if(frogcount%2==0) ViewGroup.LayoutParams(BIGSIZE,
                BIGSIZE) else ViewGroup.LayoutParams(SIZE,SIZE)
            this.x = if(frogcount%2==0) x - BIGSIZE/2 else x - SIZE/2
            this.y = if(frogcount%2==0) y - BIGSIZE/2 else y - SIZE/2
            overlayView.addView(this)
            imgList.add(ImageViewData(this))
            setOnClickListener {
                if(frogcount>=10){
                    imgList.remove(ImageViewData(this))
                    overlayView.removeView(this)
                    deletecount--
                }
                if(deletecount==0){
                    sensorManager.unregisterListener(listener)
                    windowManager.removeView(overlayView)
                    startActivity(Intent(this@ScreenSaverService, LoginActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                    stopSelf()
                }
            }
        }
    }
    companion object{
        private const val SIZE = 150
        private const val BIGSIZE = 250
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::overlayView.isInitialized&& overlayView.parent != null) {
            sensorManager.unregisterListener(listener)
            windowManager.removeView(overlayView)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    private val listener = object :SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                val newx = event.values[0]
                val newy = event.values[1]
                imgList.map {
                    it.imageView.x -= newx * it.speed
                    it.imageView.y += newy * it.speed
                    if (it.imageView.x < 0) {
                        it.imageView.x = 0f
                    }
                    if (it.imageView.x > (realWidth - SIZE))
                        it.imageView.x = (realWidth - SIZE).toFloat()
                    if (it.imageView.y < 0) {
                        it.imageView.y = 0f
                    }
                    if (it.imageView.y > (realHeight - 405 - SIZE))
                        it.imageView.y = 0f//(realHeight-405-SIZE).toFloat()
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }
}
