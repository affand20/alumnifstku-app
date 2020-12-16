package id.trydev.alumnifstku.ui.loker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.adapter.LokerAdapter
import id.trydev.alumnifstku.databinding.ActivityLokerBinding
import id.trydev.alumnifstku.databinding.DialogFilterLokerBinding
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.ui.loker.detail.DetailFragmentLoker
import id.trydev.alumnifstku.utils.ItemDecoration

class LokerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLokerBinding
    private lateinit var prefs: AppPreferences
    private lateinit var adapter: LokerAdapter
    private lateinit var viewModel: LokerViewModel

    private var query = hashMapOf<String, String?>(
            "order" to "",
            "filter" to "",
            "jabatan" to "",
            "perusahaan" to "",
            "cluster" to "",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLokerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = LokerAdapter(this) { loker ->
            /* Navigate to Detail Loker activity */
            val detailFragment = DetailFragmentLoker(loker)
            detailFragment.show(supportFragmentManager, detailFragment.tag)
        }

        prefs = AppPreferences(this)

        viewModel = ViewModelProvider(this).get(LokerViewModel::class.java)

        binding.rvLoker.layoutManager = LinearLayoutManager(this)
        binding.rvLoker.adapter = adapter
        binding.rvLoker.addItemDecoration(ItemDecoration(32))

        viewModel.getLoker(prefs.token.toString(), query)
        /* Observe Request state changes */
        viewModel.state.observe({ lifecycle }, { state ->
            Log.d("OBSERVE", "state $state")
            when(state) {
                RequestState.REQUEST_START -> {
                    /* Show progress bar */
                    binding.swipeRefresh.isRefreshing = true
                    binding.stateEmpty.visibility = View.GONE
                }
                RequestState.REQUEST_END -> {
                    /* Hide progress bar and fetch response */
                    binding.swipeRefresh.isRefreshing = false
                }
                RequestState.REQUEST_ERROR -> {
                    /* Something happen.. Hide progress bar and fetch errors */
                    binding.swipeRefresh.isRefreshing = false
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
                    response.data?.let { adapter.populateItem(it) }
                } else {
                    binding.stateEmpty.visibility = View.VISIBLE
                    binding.stateEmpty.text = response.message
                }
            }
        })

        /* Observe another Error possibility changes */
        viewModel.error.observe({ lifecycle }, { error ->
            if (error.isNotEmpty()) {
                binding.stateEmpty.visibility = View.VISIBLE
                binding.stateEmpty.text = error
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getLoker(prefs.token.toString(), query)
        }

        binding.floating.tvFilter.setOnClickListener {
            val bindDialog = DialogFilterLokerBinding.inflate(LayoutInflater.from(this))
            val dialog = BottomSheetDialog(this)
            dialog.setContentView(bindDialog.root)

            bindDialog.toolbar.title = "Filter"
            bindDialog.toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_round_close_24)
            bindDialog.toolbar.setNavigationOnClickListener {
                dialog.dismiss()
            }

            /* Populate spinner data */
            val orders = resources.getStringArray(R.array.order_by)
            val clusters = resources.getStringArray(R.array.cluster)
            // apply to adapter and spinner
            bindDialog.edtOrder.setAdapter(
                    ArrayAdapter(this, R.layout.simple_item_spinner, orders)
            )
            bindDialog.edtCluster.setAdapter(
                    ArrayAdapter(this, R.layout.simple_item_spinner, clusters)
            )

            bindDialog.tvClearFilter.setOnClickListener {
                query["order"] = ""
                query["filter"] = ""
                query["cluster"] = ""
                query["perusahaan"] = ""
                query["jabatan"] = ""
                bindDialog.edtOrder.setText("")
                bindDialog.edtCluster.setText("")
                bindDialog.edtPosition.setText("")
                bindDialog.edtCompany.setText("")
            }

            query.forEach {
                if (it.key == "order" && it.value != "") {
                    bindDialog.edtOrder.setText(it.value)
                    bindDialog.tvClearFilter.visibility = View.VISIBLE
                }
                if (it.key == "cluster" && it.value != "") {
                    bindDialog.edtCluster.setText(it.value)
                    bindDialog.tvClearFilter.visibility = View.VISIBLE
                }
                if (it.key == "perusahaan" && it.value != "") {
                    bindDialog.edtCompany.setText(it.value)
                    bindDialog.tvClearFilter.visibility = View.VISIBLE
                }
                if (it.key == "jabatan" && it.value != "") {
                    bindDialog.edtPosition.setText(it.value)
                    bindDialog.tvClearFilter.visibility = View.VISIBLE
                }
            }

            bindDialog.btnApply.setOnClickListener {
                this.query = getFilter(bindDialog, query)
                Log.d("FILTER_QUERY", "$query")
                /* Call API again with filters */
                viewModel.getLoker(prefs.token.toString(), query)
                dialog.dismiss()
            }

            dialog.show()
        }

    }

    private fun getFilter(binding: DialogFilterLokerBinding, query: HashMap<String, String?>): HashMap<String, String?> {
        if (binding.edtCluster.text.toString().isNotEmpty()) {
            this.query["cluster"] = binding.edtCluster.text.toString()
            this.query["filter"] = "true"
        } else {
            this.query["cluster"] = ""
        }
        if (binding.edtCompany.text.toString().isNotEmpty()) {
            this.query["perusahaan"] = binding.edtCompany.text.toString()
            this.query["filter"] = "true"
        } else {
            this.query["perusahaan"] = ""
        }
        if (binding.edtPosition.text.toString().isNotEmpty()) {
            this.query["jabatan"] = binding.edtPosition.text.toString()
            this.query["filter"] = "true"
        } else {
            this.query["jabatan"] = ""
        }
        if (binding.edtOrder.text.toString().isNotEmpty()) {
            if (binding.edtOrder.text.toString() == "Terbaru") {
                this.query["order"] = "desc"
            }
            if (binding.edtOrder.text.toString() == "Terlama") {
                this.query["order"] = "asc"
            }
            this.query["filter"] = "true"
        } else {
            this.query["order"] = ""
        }
        return query
    }

    private fun panggang(msg: String, duration: String = "short") {
        if (duration == "long")
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        else
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}