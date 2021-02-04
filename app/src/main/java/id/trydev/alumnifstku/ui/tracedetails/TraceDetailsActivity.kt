package id.trydev.alumnifstku.ui.tracedetails

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import id.trydev.alumnifstku.adapter.PekerjaanListAdapter
import id.trydev.alumnifstku.databinding.ActivityTraceDetailsBinding
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences

class TraceDetailsActivity : AppCompatActivity() {

    companion object {
        val ARG_UID = "user_uid"
    }

    private lateinit var binding: ActivityTraceDetailsBinding
    private lateinit var userid: String
    private lateinit var prefs: AppPreferences
    private lateinit var adapter: PekerjaanListAdapter
    private lateinit var viewModel: TraceDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTraceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = PekerjaanListAdapter(this, false, null)

        userid = intent.getStringExtra(ARG_UID)

        prefs = AppPreferences(this)

        viewModel = ViewModelProvider(this).get(TraceDetailsViewModel::class.java)

        viewModel.getAlumni(prefs.token.toString(), userid.toInt() )

        binding.rvPekerjaanlist.layoutManager = LinearLayoutManager(this)
        binding.rvPekerjaanlist.adapter = adapter

        /* Observe Request state changes */
        viewModel.state.observe({ lifecycle }, { state ->
            Log.d("OBSERVE", "state $state")
            when(state) {
                RequestState.REQUEST_START -> {
                    /* Show progress bar */
                    // binding.swipeRefresh.isRefreshing = true
                    // binding.stateEmpty.visibility = View.GONE
                }
                RequestState.REQUEST_END -> {
                    /* Hide progress bar and fetch response */
                    // binding.swipeRefresh.isRefreshing = false
                }
                RequestState.REQUEST_ERROR -> {
                    /* Something happen.. Hide progress bar and fetch errors */
                    // binding.swipeRefresh.isRefreshing = false
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
                    // populate data
                    response.data?.biodata?.let { alumni ->

                        if (alumni.foto != null) {
                            Glide.with(this)
                                .asBitmap()
                                .centerCrop()
                                .load(alumni.foto)
                                .into(binding.traceImgDetails)
                        }

                        binding.traceNamaDetails.text = alumni.nama
                        binding.traceJurusanDetails.text = alumni.jurusan
                        binding.traceAngkatanDetails.text = alumni.angkatan
                        if (alumni.linkedin == null) {
                            binding.traceLinkedinDetails.visibility = View.VISIBLE
                            binding.traceLinkedinDetails.text = alumni.linkedin
                        } else {
                            binding.traceLinkedinDetails.visibility = View.VISIBLE
                            binding.traceLinkedinDetails.text = "null"
                        }
                        binding.traceAlamatDetails.text = alumni.alamat
                        binding.traceDomisiliDetails.text = alumni.domisili

                    }

                    response.data?.tracing?.let {
                        /* do nothing */
                        adapter.setPekerjaan(it)
                        // belum bikin recycler nya bozz
                    }
                } else {
                    Toast.makeText(this, response.message, Toast.LENGTH_LONG)
                    // binding.stateEmpty.visibility = View.VISIBLE
                    // binding.stateEmpty.text = response.message
                }
            }
        })

        /* Observe another Error possibility changes */
        viewModel.error.observe({ lifecycle }, { error ->
            if (error.isNotEmpty()) {
                // binding.stateEmpty.visibility = View.VISIBLE
                // binding.stateEmpty.text = error
                Toast.makeText(this, error, Toast.LENGTH_LONG)
            }
        })

        binding.traceLinkedinDetails.setOnClickListener {
            if (binding.traceLinkedinDetails.text.isNotEmpty() && binding.traceLinkedinDetails.text != "-") {
                val builder = CustomTabsIntent.Builder()
                val customTab = builder.build()
                customTab.launchUrl(this, Uri.parse(binding.traceLinkedinDetails.text.toString()))
            }
        }

    }
}