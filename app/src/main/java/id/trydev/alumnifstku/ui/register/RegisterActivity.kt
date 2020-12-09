package id.trydev.alumnifstku.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.ActivityRegisterBinding
import id.trydev.alumnifstku.ui.biodata.BiodataActivity
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    private lateinit var prefs: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences(this)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        /* Navigate to login activity */
        binding.toLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        /* Register Action */
        binding.btnRegister.setOnClickListener {
            if (validate(binding)) {
                // call doRegister() from viewModel
                viewModel.doRegister(
                        binding.edtUsername.text.toString(),
                        binding.edtEmail.text.toString(),
                        binding.edtPassword.text.toString()
                )
            }
        }

        /* Observe Request state changes */
        viewModel.state.observe({ lifecycle }, { state ->
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
                // if success, navigate to biodata page
                // else, show error Toast
                if (response.success == true) {
                    // save credential to preferences
                    prefs.token = response.data?.apiToken
                    prefs.userId = response.data?.id
                    // debug only
                    Log.d("PREFERENCES", "${prefs.token}, ${prefs.userId}")
                    startActivity(Intent(this, BiodataActivity::class.java))
                    Toast.makeText(this, response.message, Toast.LENGTH_LONG).show()
                    finish()
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
    * Validasi input sebelum dikirim ke fungsi doRegister()
    * */
    private fun validate(binding: ActivityRegisterBinding): Boolean {
        if (binding.edtUsername.text.toString().isEmpty()) {
            binding.edtUsername.error = "Wajib diisi"
            return false
        }
        if (binding.edtEmail.text.toString().isEmpty()) {
            binding.edtEmail.error = "Wajib diisi"
            return false
        }
        if (binding.edtPassword.text.toString().isEmpty()) {
            binding.edtPassword.error = "Wajib diisi"
            return false
        }
        return true
    }
}