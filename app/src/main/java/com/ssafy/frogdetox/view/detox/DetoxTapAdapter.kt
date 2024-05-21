package com.ssafy.frogdetox.view.detox

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class DetoxTapAdapter (fragmentActivity : FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    private val fragments = listOf<Fragment>(DetoxSleepFragment(), DetoxBlockingFragment())

    //tab의 개수만큼 return
    override fun getItemCount(): Int = 2

    //tab의 이름 fragment마다 바꾸게 하기
    override fun createFragment(position: Int): Fragment = fragments[position]
}