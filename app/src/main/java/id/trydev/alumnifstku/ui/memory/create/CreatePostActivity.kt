package id.trydev.alumnifstku.ui.memory.create

import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.FragmentCreatePostBinding
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.utils.GlideApp
import id.trydev.alumnifstku.utils.RealPathUtil
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class CreatePostActivity: AppCompatActivity() {

    private lateinit var uriImg: Uri
    private lateinit var binding: FragmentCreatePostBinding
    private lateinit var viewModel: CreatePostFragmentViewModel
    private lateinit var prefs: AppPreferences

    private var filePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uriImg = intent?.getStringExtra("uriImg").toString().toUri()
        Log.d("URI IMG", "$uriImg")

        prefs = AppPreferences(this)
        viewModel = ViewModelProvider(this).get(CreatePostFragmentViewModel::class.java)

        binding.toolbar.title = "Buat Posting"
        binding.toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_round_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.rootView.smoothScrollTo(0,0)

        GlideApp.with(this)
            .asBitmap()
            .centerInside()
            .placeholder(R.color.grey)
            .fallback(R.color.grey)
            .load(uriImg)
            .into(binding.ivPost)

        binding.toolbar.inflateMenu(R.menu.menu_submit_post)
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.submit_post -> {
                    val foto = RealPathUtil.getRealPath(this, uriImg)
                    // send to server
                    viewModel.postComment(prefs.token.toString(), binding.edtDescription.text.toString(), foto)
                    true
                }
                else -> false
            }

        }

        /* Observe Request post state changes */
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

        /* Observe Response changes */
        viewModel.response.observe({ lifecycle }, { response ->
            Log.d("RESPONSE", response.toString())
            if (response != null) {
                // check server response
                // if success, populate data
                // else, show error Toast
                if (response.success == true) {
                    // populate data
                    finish()
                } else {
                    response.message?.let { panggang(it) }
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

    private fun panggang(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }



}