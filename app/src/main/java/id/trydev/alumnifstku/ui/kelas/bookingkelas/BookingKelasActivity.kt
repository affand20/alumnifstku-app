package id.trydev.alumnifstku.ui.kelas.bookingkelas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.ActivityBookingKelasBinding
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.utils.GlideApp
import java.text.SimpleDateFormat
import java.util.*

class BookingKelasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingKelasBinding
    private lateinit var prefs: AppPreferences
    private lateinit var viewModel: BookingKelasViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingKelasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val kelasId = intent?.getStringExtra("kelasId")

        prefs = AppPreferences(this)
        viewModel = ViewModelProvider(this).get(BookingKelasViewModel::class.java)

        binding.toolbar.title = "Booking Kelas"
        binding.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            if (validate(binding)) {
                // send data to server.
                viewModel.bookingKelas(
                    prefs.token.toString(),
                    kelasId.toString().toInt(),
                    binding.etKelasalumniNama.text.toString(),
                    binding.etKelasalumniEmail.text.toString(),
                    binding.etKelasalumniWhatsapp.text.toString()
                )
            }
        }

        /* Observe Request state changes */
        viewModel.state.observe({ lifecycle }, { state ->
            Log.d("OBSERVE", "state $state")
            when(state) {
                RequestState.REQUEST_START -> {
                    /* Show progress bar */
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSubmit.visibility = View.INVISIBLE
                }
                RequestState.REQUEST_END -> {
                    /* Hide progress bar and fetch response */
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.btnSubmit.visibility = View.VISIBLE
                }
                RequestState.REQUEST_ERROR -> {
                    /* Something happen.. Hide progress bar and fetch errors */
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.btnSubmit.visibility = View.VISIBLE
                }
                else -> { /* do nothing */ }
            }
        })

        /* Observe Response changes */
        viewModel.response.observe({ lifecycle }, { response ->
            Log.d("RESPONSE", response.toString())
            if (response != null) {
                // check server response
                // if success, populate data
                // else, show error Toast
                if (response.success == true) {
                    // populate speaker data
                    panggang("Booking berhasil!\nSilakan cek email untuk melihat tiket anda.")
                    finish()
                } else {
                    panggang(response.message.toString())
                }
            }
        })

        /* Observe another Error possibility changes */
        viewModel.error.observe({ lifecycle }, { error ->
            if (error.isNotEmpty()) {
                panggang(error)
            }
        })

    }

    private fun validate(binding: ActivityBookingKelasBinding): Boolean {
        if (binding.etKelasalumniNama.text.toString().isEmpty()) {
            binding.etKelasalumniNama.error = "Wajib diisi"
            binding.etKelasalumniNama.requestFocus()
            return false
        }
        if (binding.etKelasalumniEmail.text.toString().isEmpty()) {
            binding.etKelasalumniEmail.error = "Wajib diisi"
            binding.etKelasalumniEmail.requestFocus()
            return false
        }
        if (binding.etKelasalumniWhatsapp.text.toString().isEmpty()) {
            binding.etKelasalumniWhatsapp.error = "Wajib diisi"
            binding.etKelasalumniWhatsapp.requestFocus()
            return false
        }
        return true
    }

    private fun panggang(msg: String, duration: String = "short") {
        if (duration == "long")
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        else
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}