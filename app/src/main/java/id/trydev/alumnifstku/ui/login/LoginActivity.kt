package id.trydev.alumnifstku.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.ActivityLoginBinding
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.ui.biodata.BiodataActivity
import id.trydev.alumnifstku.ui.forgotpass.ForgotPasswordActivity
import id.trydev.alumnifstku.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var prefs: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences(this)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        /* Navigate to Register activity */
        binding.toRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        /* Navigate to Forgot Password activity */
        binding.toForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        /* Login action */
        binding.btnLogin.setOnClickListener {
            if (validate(binding)) {
                // call doLogin() from viewModel
                if (binding.edtEmailOrUsername.text.toString().contains('@')) {
                    viewModel.doLogin(
                        email = binding.edtEmailOrUsername.text.toString(),
                        password = binding.edtPassword.text.toString()
                    )
                } else {
                    viewModel.doLogin(
                        username = binding.edtEmailOrUsername.text.toString(),
                        password = binding.edtPassword.text.toString()
                    )
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
                    // save credential to preferences
                    prefs.token = response.data?.apiToken
                    prefs.userId = response.data?.id
                    // debug only
                    Log.d("PREFERENCES", "${prefs.token}, ${prefs.userId}")
                    // TODO: Do checking whether user has filled their biodata or not
                    startActivity(Intent(this, BiodataActivity::class.java))
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

    private fun validate(binding: ActivityLoginBinding): Boolean {
        if (binding.edtEmailOrUsername.text.toString().isEmpty()) {
            binding.edtEmailOrUsername.error = "Wajib diisi"
            return false
        }
        if (binding.edtPassword.text.toString().isEmpty()) {
            binding.edtPassword.error = "Wajib diisi"
            return false
        }
        return true
    }
}