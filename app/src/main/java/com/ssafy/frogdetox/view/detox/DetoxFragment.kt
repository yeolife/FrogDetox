package com.ssafy.frogdetox.view.detox

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ssafy.frogdetox.view.MainActivity
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
        val viewPagerAdapter = DetoxTapAdapter(mainActivity)
        viewPager.adapter = viewPagerAdapter

        val springDotsIndicator = binding.dotsIndicator
        springDotsIndicator.attachTo(viewPager)
    }
}