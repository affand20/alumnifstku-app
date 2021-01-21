package id.trydev.alumnifstku.ui.memory.notif

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.trydev.alumnifstku.adapter.NotifMemoryAdapter
import id.trydev.alumnifstku.databinding.ActivityNotifMemoryBinding
import id.trydev.alumnifstku.model.Notif
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.ui.memory.detail.DetailPostActivity

class NotifMemoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotifMemoryBinding
    private lateinit var prefs: AppPreferences
    private lateinit var adapter: NotifMemoryAdapter
    private lateinit var viewModel: NotifViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotifMemoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences(this)

        adapter = NotifMemoryAdapter(this) { post ->
            startActivity(
                    Intent(this, DetailPostActivity::class.java)
                            .putExtra("postId",post.id.toString())
            )
        }

        /**
         *
         * set rv (recycler view) layout manager and adapter
         */
        binding.rvNotif.layoutManager = LinearLayoutManager(this)
        binding.rvNotif.adapter = adapter

        viewModel = ViewModelProvider(this).get(NotifViewModel::class.java)

        viewModel.getMyNotif(prefs.token.toString())

        /*
        Observe Request state changes
         */
        viewModel.state.observe({lifecycle}, { state ->
            Log.d("OBSERVE","state $state")
            when(state) {
                RequestState.REQUEST_START -> {
                    binding.swipeRefresh.isRefreshing = true
                    binding.stateEmpty.visibility = View.GONE
                }
                RequestState.REQUEST_END -> {
                    binding.swipeRefresh.isRefreshing = false
                }
                RequestState.REQUEST_ERROR -> {
                    binding.swipeRefresh.isRefreshing = false
                }
                else -> { /* do nothing */}
            }
        })

        viewModel.response.observe({ lifecycle }, { response ->
            Log.d("RESPONSE", response.toString())
            if (response != null) {
                if (response.success == true) {
                    Log.d("ACTIVITY","populate here")
                     response.data?.let { adapter.populateData(it)}
                } else {
                    binding.stateEmpty.visibility = View.VISIBLE
                    binding.stateEmpty.text = response.message
                }
            }
        })

        viewModel.error.observe({ lifecycle }, { error ->
            if (error.isNotEmpty()) {
                binding.stateEmpty.visibility = View.VISIBLE
                binding.stateEmpty.text = error
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getMyNotif(prefs.token.toString())
        }


    }
}