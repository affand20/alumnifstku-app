package id.trydev.alumnifstku.ui.memory.detail

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.adapter.CommentAdapter
import id.trydev.alumnifstku.databinding.FragmentDetailPostBinding
import id.trydev.alumnifstku.model.Post
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.utils.GlideApp
import java.text.SimpleDateFormat
import java.util.*

class DetailPostActivity: AppCompatActivity() {

    private lateinit var post: Post
    private lateinit var binding: FragmentDetailPostBinding
    private lateinit var postId: String
    private lateinit var adapter: CommentAdapter
    private lateinit var viewModel: DetailFragmentViewModel
    private lateinit var prefs: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postId = intent?.getStringExtra("postId").toString()

        prefs = AppPreferences(this)
        adapter = CommentAdapter(this, prefs) { comment ->
            viewModel.removeComment(prefs.token.toString(), postId.toInt(), comment.id.toString().toInt())
        }
        viewModel = ViewModelProvider(this).get(DetailFragmentViewModel::class.java)

        binding.toolbar.title = "Posting"
        binding.toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_round_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.ctx_delete -> {
                    viewModel.removePost(prefs.token.toString(), postId.toInt())
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

        binding.rvComment.layoutManager = LinearLayoutManager(this)
        binding.rvComment.adapter = adapter

        viewModel.getPosts(prefs.token.toString(), postId.toInt())
        viewModel.getComments(prefs.token.toString(), postId.toInt())

        binding.btnSendComment.setOnClickListener {
            if (validate(binding)) {
                /* send comment */
                viewModel.postComment(prefs.token.toString(), postId.toInt(), binding.edtComment.text.toString())
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
                    viewModel.unlikePost(prefs.token.toString(), postId.toInt())
                } else {
                    /* if user has not liked, then click will make it like */
                    viewModel.likePost(prefs.token.toString(), postId.toInt())
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
                            binding.tvPostComment.text = String.format(this.resources.getString(R.string.comment_count_template), comments.size)
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
                            binding.tvPostComment.text = String.format(this.resources.getString(R.string.comment_count_template), 0)
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

    private fun populateItem(item: Post, binding: FragmentDetailPostBinding) {

        if (prefs.userId == item.alumniId) {
            binding.toolbar.menu.clear()
            binding.toolbar.inflateMenu(R.menu.menu_comment)
        }

        GlideApp.with(this)
            .asBitmap()
            .centerInside()
            .placeholder(R.color.grey)
            .fallback(R.color.grey)
            .load(item.foto)
            .into(binding.ivPost)

        if (item.alumni != null) {
            binding.tvUsername.text = item.alumni.username
            if (item.alumni.biodata?.foto != null) {
                GlideApp.with(this)
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
            binding.tvPostLike.text = String.format(this.resources.getString(R.string.like_count_template), item.likes.size)
            val isLiked = item.likes.find { likes ->
                likes.alumniId == prefs.userId
            }
            if (isLiked != null) {
                binding.ibToggleLike.setImageDrawable(
                    ContextCompat.getDrawable(this,
                        R.drawable.ic_round_favorite_24)
                )
            } else {
                binding.ibToggleLike.setImageDrawable(
                    ContextCompat.getDrawable(this,
                        R.drawable.ic_round_favorite_border_24)
                )
            }
        } else {
            binding.tvPostLike.text = String.format(this.resources.getString(R.string.like_count_template), 0)
        }

        if (item.comments != null) {
            binding.tvPostComment.text = String.format(this.resources.getString(R.string.comment_count_template), item.comments.size)
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
            binding.tvPostComment.text = String.format(this.resources.getString(R.string.comment_count_template), 0)
        }
    }

    private fun validate(binding: FragmentDetailPostBinding): Boolean {
        if (binding.edtComment.text.toString().isEmpty()) {
            return false
        }
        return true
    }

    private fun getScreenHeight(): Int {
        Log.d("HEIGHT", "${Resources.getSystem().displayMetrics.heightPixels}")
        return Resources.getSystem().displayMetrics.heightPixels
    }

    private fun panggang(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}