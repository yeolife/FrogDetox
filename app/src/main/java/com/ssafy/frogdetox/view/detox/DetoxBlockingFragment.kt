package com.ssafy.frogdetox.view.detox

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.frogdetox.common.Permission.isAccessibilityServiceEnabled
import com.ssafy.frogdetox.common.SharedPreferencesManager.setAppState
import com.ssafy.frogdetox.data.AppInfoDto
import com.ssafy.frogdetox.databinding.FragmentDetoxBlockingBinding
import com.ssafy.frogdetox.view.MainActivity

private const val TAG = "DetoxBlockingFragment"
class DetoxBlockingFragment : Fragment() {
    private lateinit var mainActivity: MainActivity

    private var _binding: FragmentDetoxBlockingBinding? = null
    private val binding get() = _binding!!

    private lateinit var blockingRecycler: RecyclerView
    private lateinit var blockingAdapter: DetoxBlockingAdapter

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

        initRecyclerView()
    }

    private fun checkPermission() {
        val overlayPermission = Settings.canDrawOverlays(mainActivity)

        val notiPermission = NotificationManagerCompat.from(mainActivity).areNotificationsEnabled()

        val accessibilityPermission = isAccessibilityServiceEnabled(mainActivity, AccessibilityService::class.java)

        if (!overlayPermission || !notiPermission || !accessibilityPermission) {
            val bottomSheet = DetoxBlockingBottomSheetFragment()

            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }
    }

    private fun initRecyclerView() {
        blockingRecycler = binding.rvAppBlocking

        val packageManager = mainActivity.packageManager
        val installedApps = getInstalledApps(packageManager,requireContext())

        blockingAdapter = DetoxBlockingAdapter(installedApps)

        blockingRecycler.apply {
            adapter = blockingAdapter

            setHasFixedSize(true)

            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))

            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
    }

    private fun getInstalledApps(packageManager: PackageManager,context: Context): List<AppInfoDto> {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val apps = packageManager.queryIntentActivities(intent, 0)
        return apps.filter { app ->
            app.activityInfo.packageName != context.packageName
        }.map { app ->
            AppInfoDto(
                appTitle = app.loadLabel(packageManager).toString(),
                appIcon = app.loadIcon(packageManager),
                appPackage = app.activityInfo.packageName
            )
        }
    }
}

// 1번 이 목록들로 화면 위에 화면 띄우게 설정하기
// sharedpreference로 패키지명을 키로하고 boolean값을 저장해놓는다.
// 앱을 켤때마다 shared 초기화하고 다시 저장