package com.ssafy.frogdetox.view.detox

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ssafy.frogdetox.common.Permission.isAccessibilityServiceEnabled
import com.ssafy.frogdetox.databinding.FragmentDetoxBlockingBottomSheetBinding
import com.ssafy.frogdetox.view.MainActivity

class DetoxBlockingBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var activityLauncher: ActivityResultLauncher<Intent> // 권한 인텐트

    private var _binding: FragmentDetoxBlockingBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetoxBlockingBottomSheetBinding.inflate(inflater, container, false)

        activityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if(checkPermission()) {
                    Toast.makeText(mainActivity, "모든 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermission()

        binding.llOverlay.setOnClickListener {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${mainActivity.packageName}")
            )

            activityLauncher.launch(intent)
        }

        binding.llNotification.setOnClickListener {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, mainActivity.packageName)
            }

            activityLauncher.launch(intent)
        }

        binding.llReminder.setOnClickListener {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply{
                putExtra(Settings.EXTRA_APP_PACKAGE, mainActivity.packageName)
            }
            activityLauncher.launch(intent)
        }

        binding.llAccessibility.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            activityLauncher.launch(intent)
        }
    }

    private fun checkPermission(): Boolean {
        // 다른 앱 위에 띄우는 권한 확인
        val overlayPermission = Settings.canDrawOverlays(mainActivity)

        // 알람 권한 확인
        val notiPermission = NotificationManagerCompat.from(mainActivity).areNotificationsEnabled()

        //시간 설정하고 알림 주도록 함.
        val reminderPermission = Settings.canDrawOverlays(context)

        val accessibilityPermission = isAccessibilityServiceEnabled(mainActivity, AccessibilityService::class.java)

        // 권한에 따라 view 보여주기
        binding.llOverlay.visibility = if(overlayPermission) View.GONE else View.VISIBLE
        binding.llNotification.visibility = if(notiPermission) View.GONE else View.VISIBLE
        binding.llReminder.visibility = if(reminderPermission) View.GONE else View.VISIBLE
        binding.llAccessibility.visibility = if(accessibilityPermission) View.GONE else View.VISIBLE

        return (overlayPermission && notiPermission && accessibilityPermission && reminderPermission)
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}