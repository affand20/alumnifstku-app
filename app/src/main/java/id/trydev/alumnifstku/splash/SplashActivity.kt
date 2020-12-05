package id.trydev.alumnifstku.splash

import id.trydev.alumnifstku.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        view_pager_splash.adapter = SplashPageAdapter(supportFragmentManager)


    }
}