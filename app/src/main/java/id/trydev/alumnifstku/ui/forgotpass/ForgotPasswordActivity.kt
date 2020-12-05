package id.trydev.alumnifstku.ui.forgotpass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.ActivityForgotPasswordBinding
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.ui.biodata.BiodataActivity

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var viewModel: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* init viewmodel */
        viewModel = ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)

        /* Forgot Password submit action */
        binding.btnResetPass.setOnClickListener {
            if (validate(binding)) {
                // check whether user input email or username
                if (binding.edtEmailOrUsername.text.toString().contains('@')) {
                    // call forgot password from viewmodel
                    viewModel.forgotPassword(email = binding.edtEmailOrUsername.text.toString())
                } else {
                    // call forgot password from viewmodel
                    viewModel.forgotPassword(username = binding.edtEmailOrUsername.text.toString())
                }
            }
        }

        /* Observe Request state changes */
        viewModel.state.observe({ lifecycle }, { state ->
            Log.d("OBSERVE", "state $state")
            when(state) {
                RequestState.REQUEST_START -> {
                    /* Show progress bar */
                    binding.progressBar.visibility = View.VISIBLE
                    binding.successMsg.visibility = View.GONE
                    binding.errorMsg.visibility = View.GONE
                }
                RequestState.REQUEST_END -> {
                    /* Hide progress bar and fetch response */
                    binding.progressBar.visibility = View.GONE
                }
                RequestState.REQUEST_ERROR -> {
                    /* Something happen.. Hide progress bar and fetch errors */
                    binding.progressBar.visibility = View.GONE
                }
                else -> { /* do nothing */ }
            }
        })

        /* Observe Response changes */
        viewModel.response.observe({ lifecycle }, { response ->
            Log.d("RESPONSE", response.toString())
            if (response != null) {
                // check server response
                // if success, navigate to dashboard page
                // else, show error Toast
                if (response.success == true) {
                    binding.successMsg.visibility = View.VISIBLE
                    binding.successMsg.text = response.message
                } else {
                    binding.errorMsg.visibility = View.VISIBLE
                    binding.errorMsg.text = response.message
                }
            }
        })

        /* Observe another Error possibility changes */
        viewModel.error.observe({ lifecycle }, { error ->
            if (error.isNotEmpty()) {
                binding.errorMsg.visibility = View.VISIBLE
                binding.errorMsg.text = error
            }
        })

    }

    /*
    * Making sure that form has been filled
    * */
    private fun validate(binding: ActivityForgotPasswordBinding): Boolean {
        if (binding.edtEmailOrUsername.text.toString().isEmpty()) {
            binding.edtEmailOrUsername.error = "Wajib diisi"
            return false
        }

        return true
    }
}