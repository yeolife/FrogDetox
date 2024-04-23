package com.ssafy.frogdetox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ssafy.frogdetox.databinding.ActivityMainBinding
import com.ssafy.frogdetox.fragment.DetoxFragment
import com.ssafy.frogdetox.fragment.TodoFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrameLayout, TodoFragment())
            .commit()

        binding.bottomNavbar.setOnItemSelectedListener {
            val transaction = supportFragmentManager.beginTransaction()
            when(it.itemId){
                R.id.todoTab -> transaction.replace(R.id.mainFrameLayout, TodoFragment())
                R.id.detoxTab -> transaction.replace(R.id.mainFrameLayout, DetoxFragment())
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
                transaction.replace(binding.mainFrameLayout.id, DetoxFragment())
                    .commit()
            }
            SETTING_FRAGMENT -> {

            }
        }
    }

    companion object {
        const val TODO_FRAGMENT = 1
        const val DETOX_FRAGMENT = 2
        const val SETTING_FRAGMENT = 3
    }
}