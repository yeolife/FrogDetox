package com.ssafy.frogdetox.ui.detox

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
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
        binding.night1.visibility= View.GONE
        binding.night2.visibility= View.GONE
        binding.night3.visibility= View.GONE
        lifecycleScope.launch {
            delay(1000)
            binding.night1.visibility = View.VISIBLE
            delay(500)
            binding.night2.visibility = View.VISIBLE
            delay(500)
            binding.night3.visibility = View.VISIBLE
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