package com.ssafy.frogdetox.view.detox

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.common.alarm.AlarmManager
import com.ssafy.frogdetox.databinding.DialogSleepBinding
import com.ssafy.frogdetox.databinding.FragmentDetoxSleepBinding
import com.ssafy.frogdetox.view.MainActivity
import com.ssafy.frogdetox.view.todo.TodoViewModel

class DetoxSleepFragment : Fragment() {
    lateinit var binding : FragmentDetoxSleepBinding
    lateinit var alarmManager: AlarmManager
    val viewModel : DetoxViewModel by viewModels()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alarmManager = AlarmManager(context as MainActivity)

        Glide.with(this)
            .load(R.drawable.sleepfrog)
            .into(binding.imageView2)
        binding.tvSleepTime.setOnClickListener {
            val binding2 =
                DialogSleepBinding.inflate(LayoutInflater.from(requireContext()))
            val dialog = AlertDialog.Builder(requireContext())
                .setPositiveButton("확인") { dialog, _ ->
                    viewModel.setHour(binding2.calendarView.hour)
                    viewModel.setMinute(binding2.calendarView.minute)
                    binding.tvSleepTime.text = binding2.calendarView.hour.toString()+"시"+binding2.calendarView.minute.toString()+"분에\n자야지"
                    dialog.dismiss()
                }
                .setNegativeButton("취소") { dialog, _ ->
                    dialog.dismiss()
                }
            dialog.setView(binding2.root)
            dialog.show()
        }
        binding.switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                alarmManager.setScreenSaverAlarm(requireContext(),viewModel.hour.value,viewModel.minute.value)
            }
            else{
                //삭제
            }
        }
    }

}