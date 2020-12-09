package id.trydev.alumnifstku.ui.pengaturan.pekerjaan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.ActivityPengaturanPekerjaanBinding
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.utils.GlideApp

class PengaturanPekerjaanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengaturanPekerjaanBinding
    private lateinit var prefs: AppPreferences
    private lateinit var viewModel: PengaturanPekerjaanViewModel

    private var statusedit = false

    private var query = hashMapOf<String, Any>(
            "cluster" to "",
            "company" to "",
            "jabatan" to "",
            "tahun_masuk" to "",
            "id" to ""
    )

    companion object {
        var ARG_EDIT = "edit_status"
        var ARG_ID = "trace_id"
        var ARG_COMPANY = "trace_company"
        var ARG_CLUSTER = "trace_cluster"
        var ARG_IN = "trace_in"
        var ARG_POS = "trace_pos"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengaturanPekerjaanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        statusedit = intent.getBooleanExtra(ARG_EDIT, false)

        if(statusedit){
            query["cluster"] = intent.getStringExtra(ARG_CLUSTER)
            query["company"] = intent.getStringExtra(ARG_COMPANY)
            query["jabatan"] = intent.getStringExtra(ARG_POS)
            query["tahun_masuk"] = intent.getStringExtra(ARG_IN)
            query["id"] = intent.getStringExtra(ARG_ID)

        }
        if (statusedit){
            binding.btnCreateTrace.visibility = View.GONE
            binding.btnEditCancel.visibility = View.VISIBLE
            binding.btnEditSubmit.visibility = View.VISIBLE

            binding.edtCompany.setText(query["company"].toString())
            binding.edtPosition.setText(query["jabatan"].toString())
            binding.edtYearJoined.setText(query["tahun_masuk"].toString())
            binding.edtCluster.setText(query["cluster"].toString())
        }

        val clusters = resources.getStringArray(R.array.cluster)

        binding.edtCluster.setAdapter(
                ArrayAdapter(this, R.layout.simple_item_spinner, clusters)
        )

        prefs = AppPreferences(this)

        viewModel = ViewModelProvider(this).get(PengaturanPekerjaanViewModel::class.java)

        /* Observe Request state changes */
        viewModel.state.observe({ lifecycle }, { state ->
            Log.d("OBSERVE", "state $state")
            when(state) {
                RequestState.REQUEST_START -> {
                    /* Show progress bar */
                    // binding.progressBar.visibility = View.VISIBLE
                    // binding.stateEmpty.visibility = View.GONE
                }
                RequestState.REQUEST_END -> {
                    /* Hide progress bar and fetch response */
                    // binding.progressBar.visibility = View.GONE
                    // binding.swipeRefresh.isRefreshing = false
                }
                RequestState.REQUEST_ERROR -> {
                    // binding.progressBar.visibility = View.GONE
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
                    Toast.makeText(this, "SUCCESS CREATE NEW DATA", Toast.LENGTH_LONG).show()
                    finish()

                } else {
                    // binding.stateEmpty.visibility = View.VISIBLE
                    // binding.stateEmpty.text = response.message
                    Toast.makeText(this, response.message, Toast.LENGTH_LONG).show()
                }
            }
        })

        /* Observe Response changes */
        viewModel.responseupdate.observe({ lifecycle }, { response ->
            Log.d("RESPONSE", response.toString())
            if (response != null) {
                // check server response
                // if success, populate data
                // else, show error Toast
                if (response.success == true) {
                    // populate data
                    Toast.makeText(this, "SUCCESS UPDATE NEW DATA", Toast.LENGTH_LONG).show()
                    finish()

                } else {
                    // binding.stateEmpty.visibility = View.VISIBLE
                    // binding.stateEmpty.text = response.message
                    Toast.makeText(this, response.message, Toast.LENGTH_LONG).show()
                }
            }
        })

        /* Observe another Error possibility changes */
        viewModel.error.observe({ lifecycle }, { error ->
            if (error.isNotEmpty()) {
                // binding.stateEmpty.visibility = View.VISIBLE
                // binding.stateEmpty.text = error
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        })

        // tombol submit kerjaan baru
        binding.btnCreateTrace.setOnClickListener {

            query["company"] = binding.edtCompany.text.toString()
            query["cluster"] = binding.edtCluster.text.toString()
            query["jabatan"] = binding.edtPosition.text.toString()
            query["tahun_masuk"] = binding.edtYearJoined.text.toString()

            viewModel.addTraceAttr(query)
            viewModel.createTrace(prefs.token.toString())
        }



        // tombol untuk submit update pekerjaan
        binding.btnEditSubmit.setOnClickListener {

            query["company"] = binding.edtCompany.text.toString()
            query["cluster"] = binding.edtCluster.text.toString()
            query["jabatan"] = binding.edtPosition.text.toString()
            query["tahun_masuk"] = binding.edtYearJoined.text.toString()

            viewModel.addTraceAttr(query)
            viewModel.updateTrace(prefs.token.toString())
        }

        // tombol untuk batalkan update pekerjaan
        binding.btnEditCancel.setOnClickListener {
            finish()
        }
    }
}