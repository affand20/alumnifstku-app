package id.trydev.alumnifstku.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.ItemCommentBinding
import id.trydev.alumnifstku.model.Comment
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.utils.GlideApp

class CommentAdapter(private val context: Context, private val prefs: AppPreferences, val onClick: (Comment) -> Unit): RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    private val listComment = mutableListOf<Comment>()
    fun populateData(listComment: List<Comment>) {
        this.listComment.clear()
        this.listComment.addAll(listComment)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listComment[position])
    }

    override fun getItemCount(): Int = listComment.size

    inner class ViewHolder(private val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Comment) {
            binding.tvUsername.text = item.alumni?.username
            binding.tvComment.text = item.comment
            if (item.alumni?.biodata?.foto != null) {
                GlideApp.with(context)
                    .asBitmap()
                    .placeholder(R.color.grey)
                    .fallback(R.color.grey)
                    .load(item.alumni.biodata.foto)
                    .into(binding.ivProfilePic)
            } else {
                binding.ivProfilePic.setImageResource(R.color.grey)
            }

            if (prefs.userId == item.commentatorId) {
                binding.ibDeleteComment.visibility = View.VISIBLE
                binding.ibDeleteComment.setOnClickListener {
                    onClick(item)
                }
            }
        }
    }
}