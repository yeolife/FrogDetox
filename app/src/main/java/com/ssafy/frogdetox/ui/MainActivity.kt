package com.ssafy.frogdetox.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.databinding.ActivityMainBinding
import com.ssafy.frogdetox.ui.detox.DetoxSleepFragment
import com.ssafy.frogdetox.ui.setting.SettingFragment
import com.ssafy.frogdetox.ui.todo.TodoFragment
import com.ssafy.frogdetox.ui.todo.TodoViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var todoFragment: TodoFragment
    private lateinit var detoxFragment: DetoxSleepFragment
    private lateinit var settingFragment: SettingFragment

    private val viewModel: TodoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavFragment()

        supportFragmentManager.addOnBackStackChangedListener {
            handleBackStackChanged()
        }
    }

    private fun handleBackStackChanged() {
        val fragment = supportFragmentManager.findFragmentById(R.id.mainFrameLayout)
        if (fragment is SettingFragment) {
            binding.bottomNavbar.isVisible = false
        } else {
            binding.bottomNavbar.isVisible = true
        }
    }

    private fun initBottomNavFragment() {
        todoFragment = TodoFragment()
        detoxFragment = DetoxSleepFragment()
        settingFragment = SettingFragment()

        supportFragmentManager.beginTransaction().apply {
            add(binding.mainFrameLayout.id, todoFragment)
            add(binding.mainFrameLayout.id, detoxFragment)
            hide(detoxFragment)
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
                transaction.show(todoFragment)
            }
            DETOX_FRAGMENT -> {
                transaction.hide(todoFragment)
                transaction.show(detoxFragment)
            }
            SETTING_FRAGMENT -> {
                transaction.replace(binding.mainFrameLayout.id, settingFragment)
                    .addToBackStack(null)
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