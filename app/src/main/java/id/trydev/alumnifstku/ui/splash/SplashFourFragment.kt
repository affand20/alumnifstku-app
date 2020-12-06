package id.trydev.alumnifstku.ui.splash

import android.content.Intent
import id.trydev.alumnifstku.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.trydev.alumnifstku.databinding.FragmentSplashFourBinding
import id.trydev.alumnifstku.ui.dashboard.DashboardActivity
import id.trydev.alumnifstku.ui.login.LoginActivity
import id.trydev.alumnifstku.ui.register.RegisterActivity
import kotlinx.android.synthetic.main.fragment_splash_four.*

class SplashFourFragment: Fragment() {

    private lateinit var binding: FragmentSplashFourBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashFourBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnMasuk.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
//            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.btnDaftar.setOnClickListener {
            startActivity(Intent(requireContext(), RegisterActivity::class.java))
            requireActivity().finish()
        }
    }
}