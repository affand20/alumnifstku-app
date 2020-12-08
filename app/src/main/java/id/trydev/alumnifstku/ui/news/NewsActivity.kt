package id.trydev.alumnifstku.ui.news

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.adapter.NewsAdapter
import id.trydev.alumnifstku.databinding.ActivityNewsBinding
import id.trydev.alumnifstku.databinding.DialogFilterLokerBinding
import id.trydev.alumnifstku.databinding.DialogFilterNewsBinding
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding
    private lateinit var prefs: AppPreferences
    private lateinit var viewModel: NewsViewModel
    private lateinit var adapter: NewsAdapter
    private var query = hashMapOf(
        "order" to "",
        "filter" to "",
        "judul" to ""
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences(this)
        viewModel = ViewModelProvider(this).get(NewsViewModel::class.java)

        adapter = NewsAdapter(this) { news ->
            val builder = CustomTabsIntent.Builder()
            val customTab = builder.build()
            customTab.launchUrl(this, Uri.parse(news.link))
        }

        binding.rvNews.layoutManager = LinearLayoutManager(this)
        binding.rvNews.adapter = adapter

        viewModel.getNews(prefs.token.toString(), query)

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
            viewModel.getNews(prefs.token.toString(), query)
        }

        binding.floating.tvFilter.setOnClickListener {
            val bindDialog = DialogFilterNewsBinding.inflate(LayoutInflater.from(this))
            val dialog = BottomSheetDialog(this)
            dialog.setContentView(bindDialog.root)

            bindDialog.toolbar.title = "Filter Loker"
            bindDialog.toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_round_close_24)
            bindDialog.toolbar.setNavigationOnClickListener {
                dialog.dismiss()
            }

            /* Populate spinner data */
            val orders = resources.getStringArray(R.array.order_by)
            // apply to adapter and spinner
            bindDialog.edtOrder.setAdapter(
                ArrayAdapter(this, R.layout.simple_item_spinner, orders)
            )

            bindDialog.tvClearFilter.setOnClickListener {
                query["order"] = ""
                query["filter"] = ""
                query["judul"] = ""
                bindDialog.edtOrder.setText("")
                bindDialog.edtJudul.setText("")
            }

            query.forEach {
                if (it.key == "order" && it.value != "") {
                    bindDialog.edtOrder.setText(it.value)
                    bindDialog.tvClearFilter.visibility = View.VISIBLE
                }
                if (it.key == "judul" && it.value != "") {
                    bindDialog.edtJudul.setText(it.value)
                    bindDialog.tvClearFilter.visibility = View.VISIBLE
                }
            }

            bindDialog.btnApply.setOnClickListener {
                this.query = getFilter(bindDialog, query)
                Log.d("FILTER_QUERY", "$query")
                /* Call API again with filters */
                viewModel.getNews(prefs.token.toString(), query)
                dialog.dismiss()
            }

            dialog.show()
        }

    }

    private fun getFilter(binding: DialogFilterNewsBinding, query: HashMap<String, String>): HashMap<String, String> {
        if (binding.edtJudul.text.toString().isNotEmpty()) {
            this.query["judul"] = binding.edtJudul.text.toString()
            this.query["filter"] = "true"
        } else {
            this.query["judul"] = ""
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
            this.query["filter"] = ""
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