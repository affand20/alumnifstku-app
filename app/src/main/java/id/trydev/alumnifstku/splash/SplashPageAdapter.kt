package id.trydev.alumnifstku.splash

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SplashPageAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {

    private val pages = listOf(
        SplashLogoFragment(),
        SplashOneFragment(),
        SplashTwoFragment(),
        SplashThreeFragment(),
        SplashFourFragment()
    )
    override fun getCount(): Int {
        return pages.size
    }

    override fun getItem(position: Int): Fragment {
        return pages[position]
    }

//    override fun getPageTitle(position: Int): CharSequence? {
//        return super.getPageTitle(position)
//    }
}