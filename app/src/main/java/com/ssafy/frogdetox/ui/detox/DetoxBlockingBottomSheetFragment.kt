package com.ssafy.frogdetox.ui.detox

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
import com.ssafy.frogdetox.common.Permission.isExactAlarmPermissionGranted
import com.ssafy.frogdetox.databinding.FragmentDetoxBlockingBottomSheetBinding
import com.ssafy.frogdetox.ui.MainActivity

class DetoxBlockingBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var activityLauncher: ActivityResultLauncher<Intent> // 권한 인텐트

    private var _binding: FragmentDetoxBlockingBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var flag: Int = 0
    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            flag = it.getInt("flag")
        }
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

        // 권한에 따라 view 보여주기
        if(flag == TODO_PERMISSION) {
            binding.llNotification.visibility = if (notiPermission) View.GONE else View.VISIBLE
            binding.tvpermission.text = "todo 알림 기능을 위해서 권한을 허용해주세요"
        }
        if(flag == DETOX_PERMISSION) {
            binding.llOverlay.visibility = if (overlayPermission) View.GONE else View.VISIBLE
            binding.tvpermission.text = "잠자기 알람 기능을 위해서 권한을 허용해주세요"
        }
        if(flag== TODO_PERMISSION || flag == DETOX_PERMISSION) {
            binding.llReminder.visibility = if (reminderPermission) View.GONE else View.VISIBLE
        }

        return when(flag) {
            1 -> (notiPermission&&reminderPermission)
            else -> (overlayPermission&&reminderPermission)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    companion object {
        const val TODO_PERMISSION = 1
        const val DETOX_PERMISSION = 2

        private const val ARG_FLAG = "flag"

        @JvmStatic
        fun newInstance(flag: Int) =
            DetoxBlockingBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_FLAG, flag)
                }
            }
    }
}