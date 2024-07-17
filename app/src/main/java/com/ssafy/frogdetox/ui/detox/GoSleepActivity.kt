package com.ssafy.frogdetox.ui.detox

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.ssafy.frogdetox.data.local.SharedPreferencesManager
import com.ssafy.frogdetox.databinding.ActivityGoSleepBinding
import com.ssafy.frogdetox.ui.LoginActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GoSleepActivity : AppCompatActivity() {
    lateinit var binding: ActivityGoSleepBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGoSleepBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.night1.isVisible = false
        binding.night2.isVisible = false
        binding.night3.isVisible = false
        ScreenSaverManager.cancelScreenSaverAlarm(this)
        ScreenSaverManager.setScreenSaverAlarm(this,SharedPreferencesManager.getHour(),SharedPreferencesManager.getMinute())
        SharedPreferencesManager.putSleepState(true)

        lifecycleScope.launch {
            delay(1000)
            binding.night1.isVisible = true
            delay(500)
            binding.night2.isVisible = true
            delay(500)
            binding.night3.isVisible = true
            delay(500)
            val loginIntent = Intent(this@GoSleepActivity, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            // ActivityOptionsCompat을 사용하여 애니메이션을 설정
            val options = ActivityOptionsCompat.makeCustomAnimation(
                this@GoSleepActivity,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            startActivity(loginIntent, options.toBundle())
            finish()
        }
    }
}