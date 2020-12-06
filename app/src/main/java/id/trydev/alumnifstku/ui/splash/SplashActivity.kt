package id.trydev.alumnifstku.ui.splash

import id.trydev.alumnifstku.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.trydev.alumnifstku.databinding.ActivitySplashBinding
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = SplashPageAdapter(supportFragmentManager)
        binding.viewPagerSplash?.adapter = adapter
        binding.dotsIndicator?.setViewPager(view_pager_splash)

    }
}