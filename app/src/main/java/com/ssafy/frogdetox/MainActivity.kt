package com.ssafy.frogdetox

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ssafy.frogdetox.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragmentLayout, TodoFragment())
            .commit()

        binding.bottomNavbar.setOnItemSelectedListener {
            val transaction = supportFragmentManager.beginTransaction()
            when(it.itemId){
                R.id.todoTab -> transaction.replace(R.id.mainFragmentLayout, TodoFragment())
                R.id.detoxTab -> transaction.replace(R.id.mainFragmentLayout, DetoxFragment())
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
                transaction.replace(binding.mainFragmentLayout.id, TodoFragment())
                    .commit()
            }
            DETOX_FRAGMENT -> {
                transaction.replace(binding.mainFragmentLayout.id, DetoxFragment())
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