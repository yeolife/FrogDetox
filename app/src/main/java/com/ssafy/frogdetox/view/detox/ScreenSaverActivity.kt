package com.ssafy.frogdetox.view.detox

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

private const val TAG = "ScreenSaverActivity_싸피"
class ScreenSaverActivity : AppCompatActivity() {
    private lateinit var overlayPermissionLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: 액티비티 표시안함")
        // Activity Result Launcher 초기화
        overlayPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (Settings.canDrawOverlays(this)) {
                startScreenSaver()
            } else {
                // 권한이 거부됨
            }
        }

        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            overlayPermissionLauncher.launch(intent)
        } else {
            // 권한이 이미 있을 경우, 화면 보호기 시작
            startScreenSaver()
        }
    }
    private fun startScreenSaver() {
        val intent = Intent(this, ScreenSaverService::class.java)
        startService(intent)
    }
}