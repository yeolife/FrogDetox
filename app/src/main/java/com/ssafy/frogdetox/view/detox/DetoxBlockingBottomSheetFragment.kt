package com.ssafy.frogdetox.view.detox

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
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

        binding.llUsageInfo.setOnClickListener {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                val uri = Uri.fromParts("package", mainActivity.packageName, null)
                data = uri
            }

            activityLauncher.launch(intent)
        }
    }

    private fun checkPermission(): Boolean {
        // 다른 앱 위에 띄우는 권한 확인
        val overlayPermission = Settings.canDrawOverlays(mainActivity)

        // 알람 권한 확인
        val notiPermission = NotificationManagerCompat.from(mainActivity).areNotificationsEnabled()

        // 사용정보 권한 확인
        val appOps = mainActivity.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                mainActivity.applicationInfo.uid, mainActivity.packageName)
        } else {
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                mainActivity.applicationInfo.uid, mainActivity.packageName)
        }
        val usagePermission = mode == AppOpsManager.MODE_ALLOWED
        
        // 권한에 따라 view 보여주기
        binding.llOverlay.visibility = if(overlayPermission) View.GONE else View.VISIBLE
        binding.llNotification.visibility = if(notiPermission) View.GONE else View.VISIBLE
        binding.llUsageInfo.visibility = if(usagePermission) View.GONE else View.VISIBLE

        return (overlayPermission && notiPermission && usagePermission)
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}