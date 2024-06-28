package com.ssafy.frogdetox.ui.detox

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.common.Permission
import com.ssafy.frogdetox.data.local.SharedPreferencesManager.getHour
import com.ssafy.frogdetox.data.local.SharedPreferencesManager.getMinute
import com.ssafy.frogdetox.data.local.SharedPreferencesManager.getSleepState
import com.ssafy.frogdetox.data.local.SharedPreferencesManager.putHour
import com.ssafy.frogdetox.data.local.SharedPreferencesManager.putMinute
import com.ssafy.frogdetox.data.local.SharedPreferencesManager.putSleepState
import com.ssafy.frogdetox.databinding.DialogSleepBinding
import com.ssafy.frogdetox.databinding.FragmentDetoxSleepBinding
import com.ssafy.frogdetox.ui.MainActivity

class DetoxSleepFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var binding : FragmentDetoxSleepBinding

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
        binding = FragmentDetoxSleepBinding.inflate(layoutInflater)

        return binding.root
    }

    @SuppressLint("ServiceCast")
    private fun checkPermission() : Boolean {
        var overlayPermission = Settings.canDrawOverlays(mainActivity)
        var reminderPermission = Permission.isExactAlarmPermissionGranted(mainActivity)

        if (!overlayPermission|| !reminderPermission) {
            Toast.makeText(requireContext(), "잠자기 알림 기능을 사용하시려면 아래 권한을 허용하셔야합니다.",Toast.LENGTH_SHORT).show()
            val bottomSheet = DetoxBlockingBottomSheetFragment.newInstance(DetoxBlockingBottomSheetFragment.DETOX_PERMISSION)
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }
        overlayPermission = Settings.canDrawOverlays(mainActivity)
        reminderPermission = Permission.isExactAlarmPermissionGranted(mainActivity)
        return overlayPermission&&reminderPermission
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(this)
            .load(R.drawable.sleepfrog)
            .into(binding.imgFrog)
        Glide.with(this)
            .load(R.drawable.sleepballoon)
            .into(binding.imgBall)
        val scale = resources.displayMetrics.density // 디바이스의 화면 밀도를 가져옵니다.
        val textSizeInPx = 25 * scale // dp를 px로 변환합니다.

        if(getSleepState()) setSleepUI(SleepState.ON) else setSleepUI(SleepState.OFF)

        if(getHour() != -1) {
            if(getMinute() == 0) {
                binding.tvSleepTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeInPx) // 텍스트 크기를 설정합니다.
                binding.tvSleepTime.text = getHour().toString()+"시에\n자야지"
            }
            else {
                binding.tvSleepTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeInPx) // 텍스트 크기를 설정합니다.
                binding.tvSleepTime.text = getHour().toString() + "시" + getMinute().toString() + "분에\n자야지"
            }
        }

        binding.tvSleepTime.setOnClickListener {
            ScreenSaverManager.cancelScreenSaverAlarm(mainActivity)
            if(checkPermission()){
                val binding2 =
                    DialogSleepBinding.inflate(LayoutInflater.from(requireContext()))
                val dialog = AlertDialog.Builder(requireContext())
                    .setPositiveButton("확인") { dialog, _ ->
                        putHour(binding2.calendarView.hour)

                        putMinute(binding2.calendarView.minute)
                        if(binding2.calendarView.minute==0){
                            binding.tvSleepTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeInPx) // 텍스트 크기를 설정합니다.
                            binding.tvSleepTime.text = binding2.calendarView.hour.toString()+"시에\n자야지"
                        }
                        else {
                            binding.tvSleepTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeInPx) // 텍스트 크기를 설정합니다.
                            binding.tvSleepTime.text = binding2.calendarView.hour.toString() + "시" + binding2.calendarView.minute.toString() + "분에\n자야지"
                        }
                        setSleepUI(SleepState.ON)
                        ScreenSaverManager.setScreenSaverAlarm(mainActivity, getHour(), getMinute())

                        dialog.dismiss()
                    }
                    .setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                    }
                dialog.setView(binding2.root)
                dialog.show()
            }
        }
        binding.ivon.setOnClickListener {
            if(checkPermission()){
                setSleepUI(SleepState.OFF)
                putSleepState(false)
                ScreenSaverManager.cancelScreenSaverAlarm(mainActivity)
            }
        }
        binding.ivoff.setOnClickListener {
            if(getHour()!=-1){
                if(checkPermission()){
                    setSleepUI(SleepState.ON)
                    putSleepState(true)
                    ScreenSaverManager.setScreenSaverAlarm(mainActivity, getHour(), getMinute())
                }
            }
            else{
                Toast.makeText(requireContext(),"알람 시간을 설정해주세요",Toast.LENGTH_SHORT).show()
            }
        }
    }

    enum class SleepState {
        ON, OFF
    }

    private fun setSleepUI(state: SleepState) {
        when (state) {
            SleepState.ON -> {
                binding.night.isVisible = false
                binding.ivon.isVisible = true
                binding.ivoff.isVisible = false
                binding.tvon.isVisible = true
                binding.tvoff.isVisible = false
            }
            SleepState.OFF -> {
                binding.night.isVisible = true
                binding.ivon.isVisible = false
                binding.ivoff.isVisible = true
                binding.tvon.isVisible = false
                binding.tvoff.isVisible = true
            }
        }
    }
}