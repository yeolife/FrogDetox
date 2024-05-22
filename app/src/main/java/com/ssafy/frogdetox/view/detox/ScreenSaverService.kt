package com.ssafy.frogdetox.view.detox

import android.annotation.SuppressLint
import android.app.ActivityManager
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
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.view.LoginActivity
import java.util.Random

private const val TAG = "ScreenSaverService_싸피"

data class ImageViewData(
    val imageView: ImageView,
    val speed : Float = Random().nextInt(6)+4.toFloat()
)
class ScreenSaverService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: ConstraintLayout
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
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_background, null) as ConstraintLayout

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
    var deletecount=10

    @SuppressLint("ClickableViewAccessibility")
    private fun initTouchListener() {
        for (i in 0..9) {
            val x = (Random().nextInt(realWidth - if (i % 2 == 0) BIGSIZE else SIZE)+if (i % 2 == 0) BIGSIZE/2 else SIZE/2).toFloat()
            val y = (Random().nextInt(realHeight - 200 - if (i % 2 == 0) BIGSIZE else SIZE)+if (i % 2 == 0) BIGSIZE/2 else SIZE/2).toFloat()
            setImageView(x, y, i)
        }
    }
    private var imgList = arrayListOf<ImageViewData>()
    private fun setImageView(x: Float, y: Float, idx: Int) {
        Log.d(TAG, "setImageView: $x $y 에 생성됨.")
        ImageView(this).apply {
            setBackgroundResource(if(idx%2==0) R.drawable.gosleepfrog else R.drawable.cutefrogicon)
            layoutParams= if(idx%2==0) ViewGroup.LayoutParams(BIGSIZE,
                BIGSIZE) else ViewGroup.LayoutParams(SIZE,SIZE)
            this.x = x
            this.y = y
            overlayView.addView(this)
            imgList.add(ImageViewData(this))
            setOnClickListener {
                it.visibility = View.INVISIBLE
                deletecount--
                if(deletecount <= 0) {
                    sensorManager.unregisterListener(listener)
                    windowManager.removeView(overlayView)

                    val loginIntent = Intent(this@ScreenSaverService, GoSleepActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    val options = ActivityOptionsCompat.makeCustomAnimation(
                        this@ScreenSaverService,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    )
                    startActivity(loginIntent, options.toBundle())
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
            imgList.map {
                it.imageView.y += it.speed
                if (it.imageView.x < 0) {
                    it.imageView.x = 0f
                }
                if (it.imageView.x > (realWidth - SIZE))
                    it.imageView.x = (realWidth - SIZE).toFloat()
                if (it.imageView.y < 0) {
                    it.imageView.y = 0f
                }
                if (it.imageView.y > (realHeight - 300))
                    it.imageView.y = 0f
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }
}
