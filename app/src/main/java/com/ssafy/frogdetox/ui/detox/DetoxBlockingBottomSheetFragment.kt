package com.ssafy.frogdetox.ui.detox

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ssafy.frogdetox.common.Permission.isAccessibilityServiceEnabled
import com.ssafy.frogdetox.common.Permission.isExactAlarmPermissionGranted
import com.ssafy.frogdetox.databinding.FragmentDetoxBlockingBottomSheetBinding
import com.ssafy.frogdetox.ui.MainActivity

class DetoxBlockingBottomSheetFragment(flag:Int) : BottomSheetDialogFragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var activityLauncher: ActivityResultLauncher<Intent> // 권한 인텐트

    private var _binding: FragmentDetoxBlockingBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val flag2 = flag
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
                    dismiss()
                }
            }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply{
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

    override fun onResume() {
        super.onResume()
        checkPermission()
    }
    private fun checkPermission(): Boolean {
        // 다른 앱 위에 띄우는 권한 확인
        val overlayPermission = Settings.canDrawOverlays(mainActivity)

        // 알람 권한 확인
        val notiPermission = NotificationManagerCompat.from(mainActivity).areNotificationsEnabled()

        //시간 설정하고 알림 주도록 함.
        val reminderPermission = isExactAlarmPermissionGranted(mainActivity)

        val accessibilityPermission = isAccessibilityServiceEnabled(mainActivity, AccessibilityService::class.java)

        // 권한에 따라 view 보여주기
        if(flag2==2||flag2==3) {
            binding.llOverlay.visibility = if (overlayPermission) View.GONE else View.VISIBLE
        }
        if(flag2==1) {
            binding.llNotification.visibility = if (notiPermission) View.GONE else View.VISIBLE
        }
        if(flag2==1||flag2==2||flag2==3) {
            binding.llReminder.visibility = if (reminderPermission) View.GONE else View.VISIBLE
        }
        if(flag2==3) {
            binding.llAccessibility.visibility = if (accessibilityPermission) View.GONE else View.VISIBLE
        }

        if(flag2==1){
            binding.tvpermission.text = "todo 알림 기능을 위해서 권한을 허용해주세요"
        }
        if(flag2==2){
            binding.tvpermission.text = "잠자기 알람 기능을 위해서 권한을 허용해주세요"
        }
        if(flag2==3){
            binding.tvpermission.text = "앱 차단 기능을 위해서 권한을 허용해주세요"
        }
        return if(flag2==1){
            (notiPermission&&reminderPermission)
        } else if(flag2==2){
            (overlayPermission&&reminderPermission)
        } else{
            (accessibilityPermission&&overlayPermission&&reminderPermission)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}