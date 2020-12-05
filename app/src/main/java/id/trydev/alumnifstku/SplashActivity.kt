package id.trydev.alumnifstku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.ui.biodata.BiodataActivity
import id.trydev.alumnifstku.ui.dashboard.DashboardActivity
import id.trydev.alumnifstku.ui.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var prefs: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        prefs = AppPreferences(this)

        /*
        * TODO: Check FIRST_OPEN variables from AppPreferences.
        *       IF  true, then navigate to Onboarding activity.
        *       ELSE, check token variables from AppPreferences.
        *           IF null, then navigate to Login activity
        *           ELSE, navigate to dashboard activity
        * */
        Handler().postDelayed({
            // for now, just navigate to Login activity
            if (prefs.token != null) {
//                startActivity(Intent(this, DashboardActivity::class.java))
                startActivity(Intent(this, BiodataActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }, 2000)

    }
}