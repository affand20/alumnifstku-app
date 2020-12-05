package id.trydev.alumnifstku.splash

import id.trydev.alumnifstku.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    private var tabpost = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        view_pager_splash.adapter = SplashPageAdapter(supportFragmentManager)
        dots_indicator.setViewPager(view_pager_splash)

    }

    fun dotset(state:Boolean){
        if (state){
            dots_indicator.visibility = View.GONE
        }else {
            dots_indicator.visibility = View.INVISIBLE
        }
    }




}