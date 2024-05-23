package com.ssafy.frogdetox.view.detox

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.ssafy.frogdetox.common.SharedPreferencesManager
import com.ssafy.frogdetox.common.SharedPreferencesManager.getBlockingState

private const val TAG = "accessibilityService_싸피"
class AccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if(getBlockingState()){
            if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                // event.packageName을 사용하여 현재 활성화된 앱의 패키지 이름을 얻습니다.
                val currentAppPackageName = event.packageName.toString()

                // 특정 앱의 패키지 이름과 비교
                if (SharedPreferencesManager.getAppState(currentAppPackageName)) {
                    // 특정 앱이 전면으로 왔을 때 수행할 작업
                    Log.d(TAG, "onAccessibilityEvent: $currentAppPackageName")
                    // 오버레이 서비스 시작

                    val intent = Intent(this, OverlayService::class.java)
                    startService(intent)
                }
            }
        }
        else{
            Log.d(TAG, "onAccessibilityEvent: 감지됐지만 실행안됨")
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "onInterrupt: 앱 감지함니당 ")
    }
}