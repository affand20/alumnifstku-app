package id.trydev.alumnifstku.ui.memory.create

import android.app.Dialog
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.FragmentCreatePostBinding
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.utils.GlideApp
import id.trydev.alumnifstku.utils.RealPathUtil

class CreatePostFragment(private val uriImg:Uri): BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCreatePostBinding
    private lateinit var mBehavior: BottomSheetBehavior<View>
    private lateinit var viewModel: CreatePostFragmentViewModel
    private lateinit var prefs: AppPreferences

    private var filePath = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        binding = FragmentCreatePostBinding.inflate(layoutInflater)

        prefs = AppPreferences(requireContext())
        viewModel = ViewModelProvider(this).get(CreatePostFragmentViewModel::class.java)

        binding.toolbar.title = "Buat Posting"
        binding.toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_round_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            bottomSheetDialog.dismiss()
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
                    val path = RealPathUtil.getRealPath(requireContext(), uriImg)
                    // send to server
                    viewModel.postComment(prefs.token.toString(), binding.edtDescription.text.toString(), path)
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
                    bottomSheetDialog.dismiss()
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

        bottomSheetDialog.setContentView(binding.root)
        mBehavior = BottomSheetBehavior.from(binding.root.parent as View)
        mBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(p0: View, p1: Float) {

            }

            override fun onStateChanged(p0: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

        })
        return bottomSheetDialog
    }

    override fun onStart() {
        super.onStart()
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        mBehavior.isHideable = false
        Log.d("BEHAVIOR STATE", "${mBehavior.state}")
    }

    private fun getScreenHeight(): Int {
        Log.d("HEIGHT", "${Resources.getSystem().displayMetrics.heightPixels}")
        return Resources.getSystem().displayMetrics.heightPixels
    }

    private fun panggang(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

}