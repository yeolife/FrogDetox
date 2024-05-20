package com.ssafy.frogdetox.view.detox

import android.app.AppOpsManager
import android.app.Dialog
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.databinding.FragmentDetoxBlockingBinding
import com.ssafy.frogdetox.view.MainActivity

class DetoxBlockingFragment : Fragment() {
    private lateinit var mainActivity: MainActivity

    private var _binding: FragmentDetoxBlockingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetoxBlockingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermission()
    }

    private fun checkPermission() {
        val overlayPermission = Settings.canDrawOverlays(mainActivity)

        val notiPermission = NotificationManagerCompat.from(mainActivity).areNotificationsEnabled()

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

        if (!overlayPermission || !notiPermission || !usagePermission) {
            showModalBottomSheet()
        }
    }

    // Modal BottomSheet 띄우기
    private fun showModalBottomSheet() {
        val bottomSheet = DetoxBlockingBottomSheetFragment()

        bottomSheet.show(childFragmentManager, bottomSheet.tag)
    }
}

// TODO: 백그라운드로 청깨구리가 실행 돼야 함