package id.trydev.alumnifstku.ui.memory.bottomdialog.detail

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.adapter.CommentAdapter
import id.trydev.alumnifstku.databinding.FragmentDetailPostBinding
import id.trydev.alumnifstku.model.Post
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.utils.GlideApp
import java.text.SimpleDateFormat
import java.util.*

class DetailFragmentPost(private val item:Post): BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDetailPostBinding
    private lateinit var mBehavior: BottomSheetBehavior<View>
    private lateinit var adapter: CommentAdapter
    private lateinit var viewModel: DetailFragmentViewModel
    private lateinit var prefs: AppPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        binding = FragmentDetailPostBinding.inflate(layoutInflater)

        Log.d("POST", "$item")
        prefs = AppPreferences(requireContext())
        adapter = CommentAdapter(requireContext())
        viewModel = ViewModelProvider(this).get(DetailFragmentViewModel::class.java)

        binding.toolbar.title = "Posting"
        binding.toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_round_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            bottomSheetDialog.dismiss()
        }

        GlideApp.with(requireContext())
            .asBitmap()
            .centerInside()
            .placeholder(R.color.grey)
            .fallback(R.color.grey)
            .load(item.foto)
            .into(binding.ivPost)

        if (item.alumni != null) {
            binding.tvUsername.text = item.alumni.username
            if (item.alumni.biodata?.foto != null) {
                GlideApp.with(requireContext())
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.color.grey)
                    .fallback(R.color.grey)
                    .load(item.alumni.biodata.foto)
                    .into(binding.ivProfilePic)
            } else {
                binding.ivProfilePic.setImageResource(R.color.grey)
            }
        }

        binding.tvPostCaption.text = item.deskripsi
        Log.d("CAPTION", "${binding.tvPostCaption.visibility == View.VISIBLE}")

        if (item.likes != null) {
            binding.tvPostLike.text = String.format(requireContext().resources.getString(R.string.like_count_template), item.likes.size)
        } else {
            binding.tvPostLike.text = String.format(requireContext().resources.getString(R.string.like_count_template), 0)
        }

        if (item.comments != null) {
            binding.tvPostComment.text = String.format(requireContext().resources.getString(R.string.comment_count_template), item.comments.size)
        } else {
            binding.tvPostComment.text = String.format(requireContext().resources.getString(R.string.comment_count_template), 0)
        }

        if (item.createdAt != null) {
            val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale("in", "ID"))
            val strToDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("in", "ID"))
                    .parse(item.createdAt.toString())
            binding.tvPostCreate.text = formatter.format(strToDate)
            Log.d("CREATED_AT", "$strToDate")
        } else {
            binding.tvPostCreate.visibility = View.GONE
        }

        binding.rvComment.layoutManager = LinearLayoutManager(requireContext())
        binding.rvComment.adapter = adapter

        viewModel.getComments(prefs.token.toString(), item.id.toString().toInt())

        binding.btnSendComment.setOnClickListener {
            if (validate(binding)) {
                /* send comment */
                viewModel.postComment(prefs.token.toString(), item.id.toString().toInt(), binding.edtComment.text.toString())
            }
        }

        /* Observe Request post state changes */
        viewModel.state.observe({ lifecycle }, { state ->
            Log.d("OBSERVE", "state $state")
            when(state) {
                RequestState.REQUEST_START -> {
                    /* Show progress bar */
                    binding.progressBar.visibility = View.VISIBLE
//                    binding.stateEmpty.visibility = View.GONE
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

        /* Observe Request comment state changes */
        viewModel.stateComment.observe({ lifecycle }, { state ->
            Log.d("OBSERVE", "state $state")
            when(state) {
                RequestState.REQUEST_START -> {
                    /* Show progress bar */
                    binding.progressBarComment.visibility = View.VISIBLE
                    binding.rvComment.visibility = View.GONE
                }
                RequestState.REQUEST_END -> {
                    /* Hide progress bar and fetch response */
                    binding.progressBarComment.visibility = View.GONE
                    binding.rvComment.visibility = View.VISIBLE
                }
                RequestState.REQUEST_ERROR -> {
                    /* Something happen.. Hide progress bar and fetch errors */
                    binding.progressBarComment.visibility = View.GONE
                    binding.rvComment.visibility = View.VISIBLE
                }
                else -> { /* do nothing */ }
            }
        })

        /* Observe Response comment changes */
        viewModel.responseComment.observe({ lifecycle }, { response ->
            Log.d("RESPONSE", response.toString())
            if (response != null) {
                // check server response
                // if success, populate data
                // else, show error Toast
                if (response.success == true) {
                    // populate data
                    response.data?.let { comments ->
                        adapter.populateData(comments)
                        if (comments.isNotEmpty()) {
                            binding.tvPostComment.text = String.format(requireContext().resources.getString(R.string.comment_count_template), comments.size)
                        } else {
                            val layout = binding.rvComment
                            val params = layout.layoutParams
                            params.height = 344 * 2
                            layout.layoutParams = params
                            binding.tvPostComment.text = String.format(requireContext().resources.getString(R.string.comment_count_template), 0)
                        }
                    }
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

    private fun validate(binding: FragmentDetailPostBinding): Boolean {
        if (binding.edtComment.text.toString().isEmpty()) {
            return false
        }
        return true
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