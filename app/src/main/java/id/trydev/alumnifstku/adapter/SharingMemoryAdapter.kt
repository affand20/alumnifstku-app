package id.trydev.alumnifstku.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.ItemSharingMemoryBinding
import id.trydev.alumnifstku.model.Post
import id.trydev.alumnifstku.utils.GlideApp

class SharingMemoryAdapter(private val context: Context, val onClick: (Post) -> Unit): RecyclerView.Adapter<SharingMemoryAdapter.ViewHolder>() {

    private val listPost = mutableListOf<Post>()
    fun populateData(listPost: List<Post>) {
        this.listPost.clear()
        this.listPost.addAll(listPost)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSharingMemoryBinding.inflate(LayoutInflater.from(context), parent,  false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listPost[position])
    }

    override fun getItemCount(): Int = listPost.size

    inner class ViewHolder(private val binding: ItemSharingMemoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Post) {
            binding.tvPostCaption.text = item.deskripsi

            if (item.likes != null) {
                binding.tvPostLike.text = String.format(context.resources.getString(R.string.like_count_template), item.likes.size)
            } else {
                binding.tvPostLike.text = String.format(context.resources.getString(R.string.like_count_template), 0)
            }

            if (item.comments != null) {
                binding.tvPostComment.text = String.format(context.resources.getString(R.string.comment_count_template), item.comments.size)
            } else {
                binding.tvPostComment.text = String.format(context.resources.getString(R.string.comment_count_template), 0)
            }

            if (item.alumni != null) {
                binding.tvUsername.text = item.alumni.username
                if (item.alumni.biodata?.foto != null) {
                    GlideApp.with(context)
                        .asBitmap()
                        .centerCrop()
                        .fallback(R.color.grey)
                        .load(item.alumni.biodata.foto)
                        .into(binding.ivProfilePic)
                }
            }

            GlideApp.with(context)
                .asBitmap()
                .centerInside()
                .fallback(R.color.grey)
                .load(item.foto)
                .into(binding.ivPost)

        }

    }
}
