package id.trydev.alumnifstku.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.ui.dashboard.DashboardActivity

class WelcomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        Handler().postDelayed({
//            var intent = Intent(this, SplashActivity::class.java)
//            startActivity(intent)
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }, 3000)

    }
}