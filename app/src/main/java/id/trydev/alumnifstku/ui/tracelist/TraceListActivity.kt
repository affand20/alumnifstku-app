package id.trydev.alumnifstku.ui.tracelist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.adapter.TraceListAdapter
import id.trydev.alumnifstku.databinding.ActivityTraceListBinding
import id.trydev.alumnifstku.databinding.LayoutBottomSheetFiltertraceBinding
import id.trydev.alumnifstku.databinding.LayoutBottomSheetTraceBinding
import id.trydev.alumnifstku.model.Alumni
import id.trydev.alumnifstku.model.Biodata
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences
import kotlinx.android.synthetic.main.layout_bottom_sheet_filtertrace.view.*
import kotlinx.android.synthetic.main.layout_bottom_sheet_trace.view.*
import kotlinx.android.synthetic.main.layout_bottom_sheet_trace.view.btn_search_tracing
import java.util.*

class TraceListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTraceListBinding

    private lateinit var rv: RecyclerView
    private lateinit var vAdapter: TraceListAdapter
    private lateinit var vManager: RecyclerView.LayoutManager

    private lateinit var prefs: AppPreferences
    private lateinit var viewModel: TraceListViewModel

    private var query = hashMapOf<String,String?>(
            "angkatan" to "",
            "perusahaan" to "",
            "filter" to "",
            "nama" to "",
            "cluster" to "",
            "jurusan" to ""
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTraceListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vManager = LinearLayoutManager(this)
        vAdapter = TraceListAdapter(this)
        rv = binding.rvTracelist.apply {
            setHasFixedSize(true)

            layoutManager = vManager

            adapter = vAdapter
        }


        prefs = AppPreferences(this)

        viewModel = ViewModelProvider(this).get(TraceListViewModel::class.java)

        viewModel.getAlumni(prefs.token.toString(), query)

        /* Observe Request state changes */
        viewModel.state.observe({ lifecycle }, { state ->
            Log.d("OBSERVE", "state $state")
            when(state) {
                RequestState.REQUEST_START -> {
                    /* Show progress bar */

                    // aku belum bikin mas
                }
                RequestState.REQUEST_END -> {

                    /* Hide progress bar and fetch response */
                    // aku belum bikin mas
                }
                RequestState.REQUEST_ERROR -> {
                    /* Something happen.. Hide progress bar and fetch errors */
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
                    response.data?.let { vAdapter.setData(it) }
                } else {
                    // memunculkan sesuatu jika kosong
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
            }
        })


        // tombol cari orang untuk memunculkan bottom dilaog
        binding.floating.btnCarii.setOnClickListener {

            resetquery()

            val bindCariDialog = LayoutBottomSheetTraceBinding.inflate(LayoutInflater.from(this))

            val dialog = BottomSheetDialog(this)

            ArrayAdapter.createFromResource(
                    this, R.array.list_jurusan, R.layout.spinner_layout
            ).also { adapter ->
                adapter.setDropDownViewResource(R.layout.spinner_layout)
                bindCariDialog.spinnerTracing.adapter = adapter
            }

            bindCariDialog.spinnerTracing.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    if (parent.selectedItem as String == "Jurusan"){
                        query["jurusan"] = ""
                    }else{
                        query["jurusan"] = parent.selectedItem as String
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }

            bindCariDialog.btnSearchTracing.setOnClickListener {

                query["nama"] = bindCariDialog.etTracingNama.text.toString()
                query["angkatan"] = bindCariDialog.etTracingAngkatan.text.toString()
                query["filter"] = "true"

                viewModel.getAlumni(prefs.token.toString(), query)

                // var str = "Nama : ${cariOrang.nama}, Angkatan : ${cariOrang.angkatan}, Jurusan ${cariOrang.jurusan}"
                // Toast.makeText(this, str, Toast.LENGTH_SHORT).show()

                dialog.dismiss()
            }

            dialog.setContentView(bindCariDialog.root)
            dialog.show()

        }

        // tombol filter untuk memunculkan bottom dialog
        binding.floating.btnFilterr.setOnClickListener {

            resetquery()

            val bindFilterDialog = LayoutBottomSheetFiltertraceBinding.inflate(LayoutInflater.from(this))

            val dialog = BottomSheetDialog(this)

            ArrayAdapter.createFromResource(
                    this, R.array.list_jurusan, R.layout.spinner_layout
            ).also { adapter ->
                adapter.setDropDownViewResource(R.layout.spinner_layout)
                bindFilterDialog.spinnerJurusan.adapter = adapter
            }

            ArrayAdapter.createFromResource(
                    this, R.array.cluster_filter, R.layout.spinner_layout
            ).also { adapter ->
                adapter.setDropDownViewResource(R.layout.spinner_layout)
                bindFilterDialog.spinnerCluster.adapter = adapter
            }

            bindFilterDialog.spinnerJurusan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    if (parent.selectedItem as String == "Jurusan"){
                        query["jurusan"] = ""
                    }else{
                        query["jurusan"] = parent.selectedItem as String
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }

            bindFilterDialog.spinnerCluster.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    if(parent.selectedItem as String == "Cluster"){
                        query["cluster"] = ""
                    }else {
                        query["cluster"] = parent.selectedItem as String
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }

            bindFilterDialog.btnSetFilter.setOnClickListener {

                // Log.d("QUERY:", query.toString())
                query["filter"] = "true"
                viewModel.getAlumni(prefs.token.toString(), query)

                dialog.dismiss()

            }

            dialog.setContentView(bindFilterDialog.root)
            dialog.show()
        }

    }

    fun resetquery(){
        this.query.forEach {
            var key = it.key
            this.query[key] = ""
        }
    }

}