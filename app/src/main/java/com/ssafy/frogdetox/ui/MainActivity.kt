package com.ssafy.frogdetox.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.databinding.ActivityMainBinding
import com.ssafy.frogdetox.ui.detox.DetoxSleepFragment
import com.ssafy.frogdetox.ui.todo.TodoFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var todoFragment: TodoFragment
    private lateinit var detoxFragment: DetoxSleepFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavFragment()
    }

    private fun initBottomNavFragment() {
        todoFragment = TodoFragment()
        detoxFragment = DetoxSleepFragment()

        // Initialize with the first fragment
        supportFragmentManager.beginTransaction().apply {
            add(binding.mainFrameLayout.id, todoFragment)
            add(binding.mainFrameLayout.id, detoxFragment)
            hide(detoxFragment)
            commit()
        }

        binding.bottomNavbar.setOnItemSelectedListener {
            changeFragmentView(it.itemId)
            true
        }
    }

    private fun changeFragmentView(fragment: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        when (fragment) {
            R.id.todoTab -> {
                transaction.hide(detoxFragment)
                transaction.show(todoFragment)
            }
            R.id.detoxTab -> {
                transaction.hide(todoFragment)
                transaction.show(detoxFragment)
            }
        }
        transaction.commit()
    }
}