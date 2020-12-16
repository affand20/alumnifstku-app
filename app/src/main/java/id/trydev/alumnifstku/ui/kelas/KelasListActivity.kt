package id.trydev.alumnifstku.ui.kelas

import android.content.Intent
import android.graphics.Color.parseColor
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.adapter.KelasListAdapter
import id.trydev.alumnifstku.databinding.ActivityKelasListBinding
import id.trydev.alumnifstku.databinding.DialogFilterKelasBinding
import id.trydev.alumnifstku.databinding.DialogFilterNewsBinding
import id.trydev.alumnifstku.model.Kelas
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.ui.kelas.kelasdetails.KelasDetailsActivity
import id.trydev.alumnifstku.utils.ItemDecoration
import id.trydev.alumnifstku.utils.ItemDecorationPost
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class KelasListActivity : AppCompatActivity() {

    private lateinit var binding : ActivityKelasListBinding
    private val kelasList = mutableListOf<Kelas>()
    private lateinit var prefs: AppPreferences
    private lateinit var viewModel: KelasViewModel
    private lateinit var adapter: KelasListAdapter

    private var query = hashMapOf(
            "filter" to "",
            "judul" to "",
            "tanggal" to "",
            "order" to "",
            "kategori" to ""
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKelasListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences(this)
        viewModel = ViewModelProvider(this).get(KelasViewModel::class.java)

        // gradient judul
        binding.titlePage.apply {
            setTextGradientColor(intArrayOf(
                parseColor("#599efa"),
                parseColor("#4275F3")
            ))
        }

        // set adapter untuk recycler view
        adapter = KelasListAdapter(this) {
            // handle item onClick
            startActivity(
                    Intent(this, KelasDetailsActivity::class.java)
                            .putExtra("kelasId", it.id)
            )
        }

        binding.rvKelaslist.setHasFixedSize(true)
        binding.rvKelaslist.layoutManager = LinearLayoutManager(this)
        binding.rvKelaslist.adapter = adapter
        binding.rvKelaslist.addItemDecoration(ItemDecoration(32))

        viewModel.getKelas(prefs.token.toString(), query)

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
                    response.data?.let { adapter.setData(it) }
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
            viewModel.getKelas(prefs.token.toString(), query)
        }

        binding.floating.floatingBtn.setOnClickListener {
            val bindDialog = DialogFilterKelasBinding.inflate(LayoutInflater.from(this))
            val dialog = BottomSheetDialog(this)
            dialog.setContentView(bindDialog.root)

            bindDialog.toolbar.title = "Filter"
            bindDialog.toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_round_close_24)
            bindDialog.toolbar.setNavigationOnClickListener {
                dialog.dismiss()
            }

            /* Date Picker settings */
            val builder = MaterialDatePicker.Builder.datePicker()
            val picker = builder.build()
            // Date Picker trigger
            bindDialog.edtDate.setOnClickListener {
                picker.show(this.supportFragmentManager, picker.toString())
            }
            picker.addOnPositiveButtonClickListener {
                Log.d(
                        "DatePicker Activity",
                        "Date String = ${picker.headerText}::  Date epoch value = ${picker.selection} ${Date(it)}"
                )
                // assign to edittext
                val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale("in", "ID"))
                bindDialog.edtDate.setText(formatter.format(Date(it)))
            }

            bindDialog.tvClearFilter.setOnClickListener {
                query["order"] = ""
                query["kategori"] = ""
                query["filter"] = ""
                query["judul"] = ""
                query["tanggal"] = ""
                bindDialog.edtOrder.setText("")
                bindDialog.edtTema.setText("")
                bindDialog.edtDate.setText("")
                bindDialog.edtKategori.setText("")
            }

            query.forEach {
                if (it.key == "order" && it.value != "") {
                    bindDialog.edtOrder.setText(it.value)
                    bindDialog.tvClearFilter.visibility = View.VISIBLE
                }
                if (it.key == "kategori" && it.value != "") {
                    bindDialog.edtKategori.setText(it.value)
                    bindDialog.tvClearFilter.visibility = View.VISIBLE
                }
                if (it.key == "judul" && it.value != "") {
                    bindDialog.edtTema.setText(it.value)
                    bindDialog.tvClearFilter.visibility = View.VISIBLE
                }
                if (it.key == "tanggal" && it.value != "") {
                    val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale("in", "ID"))
                    val strToDate = SimpleDateFormat("yyyy-MM-dd", Locale("in", "ID"))
                            .parse(it.value)
                    bindDialog.edtDate.setText(formatter.format(strToDate))
                    bindDialog.tvClearFilter.visibility = View.VISIBLE
                }
            }

            /* Populate spinner data */
            val orders = resources.getStringArray(R.array.order_by)
            val kategori = resources.getStringArray(R.array.kelas_kategori)
            // apply to adapter and spinner
            bindDialog.edtOrder.setAdapter(
                    ArrayAdapter(this, R.layout.simple_item_spinner, orders)
            )
            bindDialog.edtKategori.setAdapter(
                    ArrayAdapter(this, R.layout.simple_item_spinner, kategori)
            )

            bindDialog.btnApply.setOnClickListener {
                this.query = getFilter(bindDialog, query)
                Log.d("FILTER_QUERY", "$query")
                /* Call API again with filters */
                viewModel.getKelas(prefs.token.toString(), query)
                dialog.dismiss()
            }

            dialog.show()
        }

    }

    private fun getFilter(binding: DialogFilterKelasBinding, query: HashMap<String, String>): HashMap<String, String> {
        if (binding.edtTema.text.toString().isNotEmpty()) {
            this.query["judul"] = binding.edtTema.text.toString()
            this.query["filter"] = "true"
        } else {
            this.query["judul"] = ""
        }

        if (binding.edtDate.text.toString().isNotEmpty()) {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale("in", "ID"))
            val strToDate = SimpleDateFormat("EEE, dd MMM yyyy", Locale("in", "ID"))
                    .parse(binding.edtDate.text.toString())
            this.query["tanggal"] = formatter.format(strToDate)
            this.query["filter"] = "true"
        } else {
            this.query["tanggal"] = ""
        }

        if (binding.edtKategori.toString().isNotEmpty()) {
            this.query["kategori"] = binding.edtKategori.text.toString()
            this.query["filter"] = "true"
        } else {
            this.query["kategori"] = ""
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

    // function untuk mengatur warna gradasi sebuah Text
    private fun TextView.setTextGradientColor(colors: IntArray) {
        val width = paint.measureText(text.toString())
        val textShader = LinearGradient(
            0f, 0f, width, textSize, colors, null, Shader.TileMode.CLAMP
        )
        setTextColor(colors[0])
        paint.shader = textShader
    }
}