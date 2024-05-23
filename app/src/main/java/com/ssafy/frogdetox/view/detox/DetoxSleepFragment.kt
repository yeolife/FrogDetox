package com.ssafy.frogdetox.view.detox

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.common.Permission
import com.ssafy.frogdetox.common.SharedPreferencesManager.getHour
import com.ssafy.frogdetox.common.SharedPreferencesManager.getMinute
import com.ssafy.frogdetox.common.SharedPreferencesManager.getSleepState
import com.ssafy.frogdetox.common.SharedPreferencesManager.putHour
import com.ssafy.frogdetox.common.SharedPreferencesManager.putMinute
import com.ssafy.frogdetox.common.SharedPreferencesManager.putSleepState
import com.ssafy.frogdetox.common.alarm.AlarmManager
import com.ssafy.frogdetox.common.getTimeInMillis
import com.ssafy.frogdetox.common.getTodayInMillis
import com.ssafy.frogdetox.databinding.DialogSleepBinding
import com.ssafy.frogdetox.databinding.FragmentDetoxSleepBinding
import com.ssafy.frogdetox.view.LoginActivity
import com.ssafy.frogdetox.view.MainActivity

class DetoxSleepFragment : Fragment() {
    private lateinit var mainActivity: MainActivity

    lateinit var binding : FragmentDetoxSleepBinding
    lateinit var alarmManager: AlarmManager
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
        // Inflate the layout for this fragment
        binding = FragmentDetoxSleepBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("ServiceCast")
    private fun checkPermission() : Boolean {
        var overlayPermission = Settings.canDrawOverlays(mainActivity)
        var reminderPermission = Permission.isExactAlarmPermissionGranted(mainActivity)

        if (!overlayPermission|| !reminderPermission) {
            Toast.makeText(requireContext(), "잠자기 알림 기능을 사용하시려면 아래 권한을 허용하셔야합니다.",Toast.LENGTH_SHORT).show()
            val bottomSheet = DetoxBlockingBottomSheetFragment(2)
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }
        overlayPermission = Settings.canDrawOverlays(mainActivity)
        reminderPermission = Permission.isExactAlarmPermissionGranted(mainActivity)
        return overlayPermission&&reminderPermission
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alarmManager = AlarmManager(context as MainActivity)

        Glide.with(this)
            .load(R.drawable.sleepfrog)
            .into(binding.imgFrog)
        Glide.with(this)
            .load(R.drawable.sleepballoon)
            .into(binding.imgBall)
        val scale = resources.displayMetrics.density // 디바이스의 화면 밀도를 가져옵니다.
        val textSizeInPx = 25 * scale // dp를 px로 변환합니다.

        if(getSleepState()){
            binding.night.visibility=View.GONE
            binding.ivon.visibility=View.VISIBLE
            binding.ivoff.visibility=View.GONE
            binding.tvon.visibility=View.VISIBLE
            binding.tvoff.visibility=View.GONE
        }
        else{
            binding.night.visibility=View.VISIBLE
            binding.ivon.visibility=View.GONE
            binding.ivoff.visibility=View.VISIBLE
            binding.tvon.visibility = View.GONE
            binding.tvoff.visibility=View.VISIBLE
        }
        if(getHour()!=-1){
            if(getMinute()==0){
                binding.tvSleepTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeInPx) // 텍스트 크기를 설정합니다.
                binding.tvSleepTime.text = getHour().toString()+"시에\n자야지"
            }
            else {
                binding.tvSleepTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeInPx) // 텍스트 크기를 설정합니다.
                binding.tvSleepTime.text = getHour().toString() + "시" + getMinute().toString() + "분에\n자야지"
            }
        }
        binding.tvSleepTime.setOnClickListener {
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
                        binding.night.visibility= View.GONE
                        binding.ivon.visibility=View.VISIBLE
                        binding.ivoff.visibility=View.GONE
                        binding.tvon.visibility=View.VISIBLE
                        binding.tvoff.visibility=View.GONE
                        alarmManager.setScreenSaverAlarm(requireContext(), getHour(), getMinute())

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
                binding.night.visibility=View.VISIBLE
                binding.ivon.visibility=View.GONE
                binding.ivoff.visibility=View.VISIBLE
                binding.tvon.visibility = View.GONE
                binding.tvoff.visibility=View.VISIBLE
                putSleepState(false)
                alarmManager.cancelScreenSaverAlarm()
            }
        }
        binding.ivoff.setOnClickListener {
            if(getHour()!=-1){
                if(checkPermission()){
                    alarmManager.setScreenSaverAlarm(requireContext(), getHour(), getMinute())
                    binding.night.visibility=View.GONE
                    binding.ivon.visibility=View.VISIBLE
                    binding.ivoff.visibility=View.GONE
                    binding.tvon.visibility=View.VISIBLE
                    binding.tvoff.visibility=View.GONE
                    putSleepState(true)
                }
            }
            else{
                Toast.makeText(requireContext(),"알람 시간을 설정해주세요",Toast.LENGTH_SHORT).show()
            }
        }
    }
}