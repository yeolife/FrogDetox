package com.ssafy.frogdetox.view.detox

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

private const val TAG = "accessibilityService_싸피"
class AccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            // event.packageName을 사용하여 현재 활성화된 앱의 패키지 이름을 얻습니다.
            val currentAppPackageName = event.packageName.toString()

            // 특정 앱의 패키지 이름과 비교
            if (currentAppPackageName == "com.sec.android.app.camera") {
                // 특정 앱이 전면으로 왔을 때 수행할 작업
                Log.d(TAG, "onAccessibilityEvent: $123123123")
            }
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "onInterrupt: 앱 감지함니당 ")
    }
}

// shared에 패키지 이름으로 키로 저장해서 꺼내온다.