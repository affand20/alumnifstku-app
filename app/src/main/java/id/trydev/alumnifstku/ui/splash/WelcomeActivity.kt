package id.trydev.alumnifstku.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.ActivityWelcomeBinding
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.ui.dashboard.DashboardActivity
import id.trydev.alumnifstku.ui.login.LoginActivity

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var prefs: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences(this)

        Handler().postDelayed({
            // check if user has been signed in
            if (prefs.token != null) {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            // if user has not signed in
            } else {
                // check if this is user's first time open the apps
                if (prefs.firstOpen) {
                    startActivity(Intent(this, SplashActivity::class.java))
                    finish()
                // if its not first time for user, then navigate to login
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }

//            var intent = Intent(this, SplashActivity::class.java)
//            startActivity(intent)
//            finish()
        }, 1500)

    }
}