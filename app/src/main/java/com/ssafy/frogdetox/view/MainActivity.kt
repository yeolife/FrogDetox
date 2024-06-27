package com.ssafy.frogdetox.view

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.databinding.ActivityMainBinding
import com.ssafy.frogdetox.view.detox.DetoxSleepFragment
import com.ssafy.frogdetox.view.todo.TodoFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrameLayout, TodoFragment.newInstance(
                intent.getStringExtra("url"),
                intent.getStringExtra("name")
            ))
            .commit()


        binding.bottomNavbar.setOnItemSelectedListener {
            val transaction = supportFragmentManager.beginTransaction()
            when(it.itemId){
                R.id.todoTab -> transaction.replace(
                    R.id.mainFrameLayout,
                    TodoFragment.newInstance(
                        intent.getStringExtra("url"),
                        intent.getStringExtra("name")
                    )
                )
                R.id.detoxTab -> transaction.replace(R.id.mainFrameLayout, DetoxSleepFragment())
            }
            transaction.commit()

            true
        }
    }

    //FragmentTransaction 을 이용 화면 replace
    fun changeFragmentView(fragment: Int, actionFlag:Int = -1) {
        val transaction = supportFragmentManager.beginTransaction()
        when (fragment) {
            TODO_FRAGMENT -> {
                transaction.replace(binding.mainFrameLayout.id, TodoFragment())
                    .commit()
            }
            DETOX_FRAGMENT -> {
                transaction.replace(binding.mainFrameLayout.id, DetoxSleepFragment())
                    .commit()
            }
        }
    }

    companion object {
        const val TODO_FRAGMENT = 1
        const val DETOX_FRAGMENT = 2
    }
}