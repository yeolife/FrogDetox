package com.ssafy.frogdetox.ui.detox

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ssafy.frogdetox.service.service.ScreenSaverService

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
                Toast.makeText(this,"권한이 없어 실행시킬 수 없습니다. 설정에서 권한을 부여하세요.",Toast.LENGTH_SHORT).show()
                // 설정 액티비티로 이동
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                overlayPermissionLauncher.launch(intent)
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