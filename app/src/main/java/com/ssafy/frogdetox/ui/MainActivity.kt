package com.ssafy.frogdetox.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.databinding.ActivityMainBinding
import com.ssafy.frogdetox.ui.detox.DetoxSleepFragment
import com.ssafy.frogdetox.ui.setting.SettingFragment
import com.ssafy.frogdetox.ui.todo.TodoFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var todoFragment: TodoFragment
    private lateinit var detoxFragment: DetoxSleepFragment
    private lateinit var settingFragment: SettingFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavFragment()
    }

    private fun initBottomNavFragment() {
        todoFragment = TodoFragment()
        detoxFragment = DetoxSleepFragment()
        settingFragment = SettingFragment()

        // Initialize with the first fragment
        supportFragmentManager.beginTransaction().apply {
            add(binding.mainFrameLayout.id, todoFragment)
            add(binding.mainFrameLayout.id, detoxFragment)
            add(binding.mainFrameLayout.id, settingFragment)
            hide(detoxFragment)
            hide(settingFragment)
            commit()
        }

        binding.bottomNavbar.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.todoTab -> changeFragmentView(TODO_FRAGMENT)
                R.id.detoxTab -> changeFragmentView(DETOX_FRAGMENT)
            }
            true
        }
    }

    fun changeFragmentView(fragment: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        when (fragment) {
            TODO_FRAGMENT -> {
                transaction.hide(detoxFragment)
                transaction.hide(settingFragment)
                transaction.show(todoFragment)
            }
            DETOX_FRAGMENT -> {
                transaction.hide(todoFragment)
                transaction.hide(settingFragment)
                transaction.show(detoxFragment)
            }
            SETTING_FRAGMENT -> {
                transaction.hide(todoFragment)
                transaction.hide(detoxFragment)
                transaction.show(settingFragment)
            }
        }
        transaction.commit()
    }

    companion object {
        const val TODO_FRAGMENT = 1
        const val DETOX_FRAGMENT = 2
        const val SETTING_FRAGMENT = 3
    }
}