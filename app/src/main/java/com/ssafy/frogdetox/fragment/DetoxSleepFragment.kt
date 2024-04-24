package com.ssafy.frogdetox.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.ssafy.frogdetox.R
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
            Toast.makeText(requireContext(),"시간 설정 다이어로그 띄우기",Toast.LENGTH_SHORT).show()
        }
    }
}