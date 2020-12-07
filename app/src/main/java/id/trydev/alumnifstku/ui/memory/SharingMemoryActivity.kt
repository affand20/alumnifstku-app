package id.trydev.alumnifstku.ui.memory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.trydev.alumnifstku.adapter.SharingMemoryAdapter
import id.trydev.alumnifstku.databinding.ActivitySharingMemoryBinding
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.ui.memory.bottomdialog.detail.DetailFragmentPost
import id.trydev.alumnifstku.ui.memory.bottomdialog.detail.DetailPostActivity
import id.trydev.alumnifstku.ui.memory.mypost.MyPostActivity
import id.trydev.alumnifstku.utils.ItemDecorationPost

class SharingMemoryActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySharingMemoryBinding
    private lateinit var prefs: AppPreferences
    private lateinit var adapter: SharingMemoryAdapter
    private lateinit var viewModel: SharingMemoryViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySharingMemoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences(this)

        adapter = SharingMemoryAdapter(this) { post ->
            /* Navigate to Detail Loker activity */
//            val detailFragment = DetailFragmentPost(post.id.toString().toInt())
//            Log.d("POST ID", post.id.toString())
//            detailFragment.show(supportFragmentManager, detailFragment.tag)
            startActivity(
                Intent(this, DetailPostActivity::class.java)
                    .putExtra("postId", post.id.toString())
            )
        }

        binding.rvPost.layoutManager = LinearLayoutManager(this)
        binding.rvPost.adapter = adapter
        binding.rvPost.addItemDecoration(ItemDecorationPost(32))

        viewModel = ViewModelProvider(this).get(SharingMemoryViewModel::class.java)

        viewModel.getPosts(prefs.token.toString())
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
                    response.data?.let { adapter.populateData(it) }
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
            viewModel.getPosts(prefs.token.toString())
        }

        binding.ibMyPost.setOnClickListener {
            startActivity(Intent(this, MyPostActivity::class.java))
        }

    }

    override fun onResume() {
        super.onResume()
        Log.d("SHARING MEMORY ACTIVITY", "ON RESUME")
        viewModel.getPosts(prefs.token.toString())
    }

}