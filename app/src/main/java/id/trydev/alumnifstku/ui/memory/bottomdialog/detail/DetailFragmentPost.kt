package id.trydev.alumnifstku.ui.memory.bottomdialog.detail

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

class DetailFragmentPost(private val postId:Int): BottomSheetDialogFragment() {

    private lateinit var post: Post

    private lateinit var binding: FragmentDetailPostBinding
    private lateinit var mBehavior: BottomSheetBehavior<View>
    private lateinit var adapter: CommentAdapter
    private lateinit var viewModel: DetailFragmentViewModel
    private lateinit var prefs: AppPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        binding = FragmentDetailPostBinding.inflate(layoutInflater)

        prefs = AppPreferences(requireContext())
        adapter = CommentAdapter(requireContext(), prefs) { comment ->
            viewModel.removeComment(prefs.token.toString(), postId, comment.id.toString().toInt())
        }
        viewModel = ViewModelProvider(this).get(DetailFragmentViewModel::class.java)

        binding.toolbar.title = "Posting"
        binding.toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_round_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            bottomSheetDialog.dismiss()
        }

        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.ctx_delete -> {
                    viewModel.removePost(prefs.token.toString(), postId)
                    true
                }
                else -> false
            }
        }

        val layout = binding.rvComment
        val params = layout.layoutParams
        params.height = 400 * 2
        layout.layoutParams = params
        binding.rootView.smoothScrollTo(0, 0)

        binding.rvComment.layoutManager = LinearLayoutManager(requireContext())
        binding.rvComment.adapter = adapter

        viewModel.getPosts(prefs.token.toString(), postId)
        viewModel.getComments(prefs.token.toString(), postId)

        binding.btnSendComment.setOnClickListener {
            if (validate(binding)) {
                /* send comment */
                viewModel.postComment(prefs.token.toString(), postId, binding.edtComment.text.toString())
                // clear edittext
                binding.edtComment.text.clear()
            }
        }

        binding.ibToggleLike.setOnClickListener {
            /* make sure post variable has initialized */
            if (::post.isInitialized) {
                /* check is user has liked this post or not */
                val isLiked = post.likes?.find { likes ->
                    likes.alumniId == prefs.userId
                }
                if (isLiked != null) {
                    /* if user has liked, then click will make it unlike */
                    viewModel.unlikePost(prefs.token.toString(), postId)
                    panggang("Unlike woy")
                } else {
                    /* if user has not liked, then click will make it like */
                    viewModel.likePost(prefs.token.toString(), postId)
                    panggang("Like woy")
                }
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

        /* Observe Request comment state changes */
        viewModel.stateComment.observe({ lifecycle }, { state ->
            Log.d("OBSERVE", "state $state")
            when(state) {
                RequestState.REQUEST_START -> {
                    /* Show progress bar */
                    binding.progressBarComment.visibility = View.VISIBLE
                    binding.btnSendComment.visibility = View.GONE
                }
                RequestState.REQUEST_END -> {
                    /* Hide progress bar and fetch response */
                    binding.progressBarComment.visibility = View.GONE
                    binding.btnSendComment.visibility = View.VISIBLE
                }
                RequestState.REQUEST_ERROR -> {
                    /* Something happen.. Hide progress bar and fetch errors */
                    binding.progressBarComment.visibility = View.GONE
                    binding.btnSendComment.visibility = View.VISIBLE
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
                    response.data?.let { item ->
                        this.post = item
                        populateItem(post, binding)
                    }
                } else {
                    response.message?.let { panggang(it) }
                }
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
                            if (comments.size > 3) {
                                val l = binding.rvComment
                                val p = l.layoutParams
                                p.height = RecyclerView.LayoutParams.WRAP_CONTENT
                                l.layoutParams = p
                            }
                        } else {
                            val l = binding.rvComment
                            val p = l.layoutParams
                            p.height = 400 * 2
                            l.layoutParams = p
                            binding.tvPostComment.text = String.format(requireContext().resources.getString(R.string.comment_count_template), 0)
                        }
                    }
                } else {
                    response.message?.let { panggang(it) }
                }
            }
        })

        /* Observe Response remove post changes */
        viewModel.responseRemovePost.observe({ lifecycle }, { response ->
            Log.d("RESPONSE", response.toString())
            if (response != null) {
                // check server response
                // if success, populate data
                // else, show error Toast
                if (response.success == true) {
                    // populate data
                    panggang(response.message.toString())
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

    private fun populateItem(item: Post, binding: FragmentDetailPostBinding) {

        if (prefs.userId == item.alumniId) {
            binding.toolbar.inflateMenu(R.menu.menu_comment)
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

        if (item.createdAt != null) {
            val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale("in", "ID"))
            val strToDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("in", "ID"))
                .parse(item.createdAt.toString())
            binding.tvPostCreate.text = formatter.format(strToDate)
        } else {
            binding.tvPostCreate.visibility = View.GONE
        }

        binding.tvPostCaption.text = item.deskripsi

        if (item.likes != null) {
            binding.tvPostLike.text = String.format(requireContext().resources.getString(R.string.like_count_template), item.likes.size)
            val isLiked = item.likes.find { likes ->
                likes.alumniId == prefs.userId
            }
            if (isLiked != null) {
                binding.ibToggleLike.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(),
                        R.drawable.ic_round_favorite_24)
                )
            } else {
                binding.ibToggleLike.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(),
                        R.drawable.ic_round_favorite_border_24)
                )
            }
        } else {
            binding.tvPostLike.text = String.format(requireContext().resources.getString(R.string.like_count_template), 0)
        }

        if (item.comments != null) {
            binding.tvPostComment.text = String.format(requireContext().resources.getString(R.string.comment_count_template), item.comments.size)
            if (item.comments.size > 3) {
                val l = binding.rvComment
                val p = l.layoutParams
                p.height = RecyclerView.LayoutParams.WRAP_CONTENT
                l.layoutParams = p
            }
        } else {
            val l = binding.rvComment
            val p = l.layoutParams
            p.height = 400 * 2
            l.layoutParams = p
            binding.tvPostComment.text = String.format(requireContext().resources.getString(R.string.comment_count_template), 0)
        }
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