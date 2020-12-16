package id.trydev.alumnifstku.ui.kelas.kelasdetails

import android.content.Intent
import android.graphics.Color.parseColor
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.adapter.NarasumberAdapter
import id.trydev.alumnifstku.databinding.ActivityKelasDetails2Binding
import id.trydev.alumnifstku.databinding.ActivityKelasDetailsBinding
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.ui.kelas.bookingkelas.BookingKelasActivity
import id.trydev.alumnifstku.utils.GlideApp
import kotlinx.android.synthetic.main.layout_bottom_sheet_kelasalumni.view.*
import java.text.SimpleDateFormat
import java.util.*

class KelasDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKelasDetails2Binding
    private lateinit var prefs: AppPreferences
    private lateinit var adapter: NarasumberAdapter
    private lateinit var viewModel: KelasDetailViewModel
    private lateinit var kelasId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKelasDetails2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        kelasId = intent?.getStringExtra("kelasId").toString()

        prefs = AppPreferences(this)
        adapter = NarasumberAdapter(this)
        viewModel = ViewModelProvider(this).get(KelasDetailViewModel::class.java)

        binding.toolbar.title = "Detail Kelas"
        binding.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.rvSpeakers.layoutManager = LinearLayoutManager(this)
        binding.rvSpeakers.adapter = adapter

        viewModel.getKelas(prefs.token.toString(), kelasId.toString().toInt())
        /* Observe Request state changes */
        viewModel.state.observe({ lifecycle }, { state ->
            Log.d("OBSERVE", "state $state")
            when(state) {
                RequestState.REQUEST_START -> {
                    /* Show progress bar */
                    binding.progressBar.visibility = View.VISIBLE
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

        viewModel.stateResend.observe({ lifecycle }, { state ->
            Log.d("OBSERVE", "state $state")
            when(state) {
                RequestState.REQUEST_START -> {
                    /* Show progress bar */
                    binding.progressResend.visibility = View.VISIBLE
                    binding.tvResendTicket.visibility = View.GONE
                }
                RequestState.REQUEST_END -> {
                    /* Hide progress bar and fetch response */
                    binding.progressResend.visibility = View.GONE
                    binding.tvResendTicket.visibility = View.VISIBLE
                }
                RequestState.REQUEST_ERROR -> {
                    /* Something happen.. Hide progress bar and fetch errors */
                    binding.progressResend.visibility = View.GONE
                    binding.tvResendTicket.visibility = View.VISIBLE
                }
                else -> { /* do nothing */ }
            }
        })

        viewModel.stateUnbook.observe({ lifecycle }, { state ->
            Log.d("OBSERVE", "state $state")
            when(state) {
                RequestState.REQUEST_START -> {
                    /* Show progress bar */
                    binding.progressUnbook.visibility = View.VISIBLE
                    binding.btnUnbook.visibility = View.INVISIBLE
                }
                RequestState.REQUEST_END -> {
                    /* Hide progress bar and fetch response */
                    binding.progressUnbook.visibility = View.INVISIBLE
                    binding.btnUnbook.visibility = View.VISIBLE
                }
                RequestState.REQUEST_ERROR -> {
                    /* Something happen.. Hide progress bar and fetch errors */
                    binding.progressUnbook.visibility = View.INVISIBLE
                    binding.btnUnbook.visibility = View.VISIBLE
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
                    response.data?.speaker?.let {
                        adapter.populateData(it)
                    }

                    // show posters
                    GlideApp.with(this)
                            .asBitmap()
                            .placeholder(R.color.grey)
                            .fallback(R.color.grey)
                            .load(response.data?.poster)
                            .into(binding.ivPosterKelas)

                    binding.tvTemaKelas.text = response.data?.judul
                    // date format converter
                    val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale("in", "ID"))
                    // date parser
                    val strToDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("in", "ID"))
                            .parse(response.data?.tanggal.toString())
                    binding.tvDate.text = formatter.format(strToDate)
                    binding.tvDetailDescription.text = response.data?.deskripsi
                    binding.tvQuotaLeft.text = String.format(resources.getString(R.string.quota_left),
                            (response.data?.kuota.toString().toInt() - response.data?.participants?.size.toString().toInt()))

                    val filterCurrentUser = response.data?.participants?.find {
                        it.alumniId == prefs.userId
                    }
                    if (filterCurrentUser != null) {
                        binding.btnBook.visibility = View.INVISIBLE
                        binding.btnUnbook.visibility = View.VISIBLE
                        binding.tvResendTicket.visibility = View.VISIBLE
                    } else {
                        binding.btnBook.visibility = View.VISIBLE
                        binding.btnUnbook.visibility = View.INVISIBLE
                        binding.tvResendTicket.visibility = View.INVISIBLE
                    }

                    binding.tvKategori.text = response.data?.kategori

                } else {
                    panggang(response.message.toString())
                }
            }
        })

        viewModel.responseNothing.observe({ lifecycle }, { response ->
            if (response != null) {
                if (response.success == true) {
                    panggang(response.message.toString())
                    viewModel.getKelas(prefs.token.toString(), kelasId.toInt())
                }
            }
        })

        /* Observe another Error possibility changes */
        viewModel.error.observe({ lifecycle }, { error ->
            if (error.isNotEmpty()) {
                panggang(error)
            }
        })

        binding.btnBook.setOnClickListener {
            startActivity(
                Intent(this, BookingKelasActivity::class.java)
                    .putExtra("kelasId", kelasId)
            )
        }

        binding.tvResendTicket.setOnClickListener {
            viewModel.resendTicket(prefs.token.toString(), kelasId.toInt())
        }

        binding.btnUnbook.setOnClickListener {
            viewModel.unbookKelas(prefs.token.toString(), kelasId.toInt())
        }

    }

    private fun panggang(msg: String, duration: String = "short") {
        if (duration == "long")
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        else
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getKelas(prefs.token.toString(), kelasId.toInt())
    }

}