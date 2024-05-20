package com.ssafy.frogdetox.view.detox

import android.app.AppOpsManager
import android.content.Context
import android.os.Build
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
import com.ssafy.frogdetox.data.AppInfoDto
import com.ssafy.frogdetox.databinding.FragmentDetoxBlockingBinding
import com.ssafy.frogdetox.view.MainActivity


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
            val bottomSheet = DetoxBlockingBottomSheetFragment()

            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }
    }

    private fun initRecyclerView() {
        blockingRecycler = binding.rvAppBlocking

        blockingAdapter = DetoxBlockingAdapter(dummy)

        blockingRecycler.apply {
            adapter = blockingAdapter

            setHasFixedSize(true)

            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))

            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
    }

    val dummy: MutableList<AppInfoDto> = arrayListOf(
        AppInfoDto("", "Youtube", false),
        AppInfoDto("", "KakaoTalk", true),
        AppInfoDto("", "FrogDetox", false),
        )
}