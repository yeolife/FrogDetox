package com.ssafy.frogdetox.view.detox

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.common.SharedPreferencesManager.getHour
import com.ssafy.frogdetox.common.SharedPreferencesManager.getMinute
import com.ssafy.frogdetox.common.SharedPreferencesManager.putHour
import com.ssafy.frogdetox.common.SharedPreferencesManager.putMinute
import com.ssafy.frogdetox.common.alarm.AlarmManager
import com.ssafy.frogdetox.databinding.DialogSleepBinding
import com.ssafy.frogdetox.databinding.FragmentDetoxSleepBinding
import com.ssafy.frogdetox.view.LoginActivity
import com.ssafy.frogdetox.view.MainActivity

class DetoxSleepFragment : Fragment() {
    lateinit var binding : FragmentDetoxSleepBinding
    lateinit var alarmManager: AlarmManager
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
        if(getMinute()==0){
            binding.tvSleepTime.text = getHour().toString()+"시에\n자야지"
        }
        else
            binding.tvSleepTime.text = getHour().toString()+"시"+getMinute().toString()+"분에\n자야지"

        binding.tvSleepTime.setOnClickListener {
            val binding2 =
                DialogSleepBinding.inflate(LayoutInflater.from(requireContext()))
            val dialog = AlertDialog.Builder(requireContext())
                .setPositiveButton("확인") { dialog, _ ->
                    putHour(binding2.calendarView.hour)
                    putMinute(binding2.calendarView.minute)
                    if(binding2.calendarView.minute==0){
                        binding.tvSleepTime.text = binding2.calendarView.hour.toString()+"시에\n자야지"
                    }
                    else
                        binding.tvSleepTime.text = binding2.calendarView.hour.toString()+"시"+binding2.calendarView.minute.toString()+"분에\n자야지"

                    binding.night.visibility=View.GONE
                    binding.ivon.visibility=View.VISIBLE
                    binding.ivoff.visibility=View.GONE
                    alarmManager.setScreenSaverAlarm(requireContext(), getHour(), getMinute())

                    dialog.dismiss()
                }
                .setNegativeButton("취소") { dialog, _ ->
                    dialog.dismiss()
                }
            dialog.setView(binding2.root)
            dialog.show()
        }
        binding.ivon.setOnClickListener {
            binding.night.visibility=View.VISIBLE
            binding.ivon.visibility=View.GONE
            binding.ivoff.visibility=View.VISIBLE
            alarmManager.cancelScreenSaverAlarm()
        }
        binding.ivoff.setOnClickListener {
            binding.night.visibility=View.GONE
            binding.ivon.visibility=View.VISIBLE
            binding.ivoff.visibility=View.GONE
            alarmManager.setScreenSaverAlarm(requireContext(), getHour(), getMinute())
        }
    }

}