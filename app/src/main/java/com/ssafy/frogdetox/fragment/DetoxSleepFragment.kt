package com.ssafy.frogdetox.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.databinding.DialogSleepBinding
import com.ssafy.frogdetox.databinding.FragmentDetoxSleepBinding

class DetoxSleepFragment : Fragment() {
    lateinit var binding : FragmentDetoxSleepBinding
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
        Glide.with(this)
            .load(R.drawable.sleepfrog)
            .into(binding.imageView2)
        binding.tvSleepTime.setOnClickListener {
            val binding2 =
                DialogSleepBinding.inflate(LayoutInflater.from(requireContext()))
            val dialog = AlertDialog.Builder(requireContext())
                .setPositiveButton("확인") { dialog, _ ->
                    // TODO: 알람시간 설정

                    dialog.dismiss()
                }
                .setNegativeButton("취소") { dialog, _ ->
                    dialog.dismiss()
                }
            dialog.setView(binding2.root)
            dialog.show()
        }
    }

}