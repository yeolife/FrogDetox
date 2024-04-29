package com.ssafy.frogdetox.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.frogdetox.MainActivity
import com.ssafy.frogdetox.adapter.FragmentAdapter
import com.ssafy.frogdetox.databinding.FragmentDetoxBinding

class DetoxFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private var _binding: FragmentDetoxBinding? = null
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
        _binding = FragmentDetoxBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewPager()
    }

    private fun initViewPager() {
        val viewPager = binding.viewPager
        val viewPagerAdapter = FragmentAdapter(mainActivity)
        viewPager.adapter = viewPagerAdapter

        val springDotsIndicator = binding.dotsIndicator
        springDotsIndicator.attachTo(viewPager)
    }
}