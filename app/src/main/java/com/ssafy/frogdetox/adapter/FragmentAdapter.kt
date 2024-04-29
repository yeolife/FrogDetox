package com.ssafy.frogdetox.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ssafy.frogdetox.fragment.DetoxSleepFragment
import com.ssafy.frogdetox.fragment.DetoxToggleFragment

class FragmentAdapter (fragmentActivity : FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    private val fragments = listOf<Fragment>(DetoxSleepFragment(), DetoxToggleFragment())

    //tab의 개수만큼 return
    override fun getItemCount(): Int = 2

    //tab의 이름 fragment마다 바꾸게 하기
    override fun createFragment(position: Int): Fragment = fragments[position]
}