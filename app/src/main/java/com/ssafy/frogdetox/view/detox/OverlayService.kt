package com.ssafy.frogdetox.view.detox

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.view.LoginActivity
import com.ssafy.frogdetox.view.MainActivity

private const val TAG = "OverlayService_싸피"
class OverlayService : Service() {

    private var overlayView: View? = null
    private var windowManager: WindowManager? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    override fun onCreate() {
        super.onCreate()

        // 화면 보호기 View를 생성합니다.
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)

        // WindowManager를 초기화합니다.
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // WindowManager에 View를 추가합니다.
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            android.graphics.PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.CENTER
        windowManager?.addView(overlayView, params)
        val btnBack = overlayView?.findViewById<TextView>(R.id.btnBack)
        btnBack?.setOnClickListener {
            // 새로운 태스크를 시작합니다.
            val intent = Intent(this, LoginActivity::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val options = ActivityOptionsCompat.makeCustomAnimation(
                this@OverlayService,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            startActivity(intent, options.toBundle())
            stopSelf()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        // 서비스가 종료될 때 WindowManager에서 View를 제거합니다.
        if (overlayView != null && windowManager != null) {
            windowManager?.removeView(overlayView)
        }
    }
}
