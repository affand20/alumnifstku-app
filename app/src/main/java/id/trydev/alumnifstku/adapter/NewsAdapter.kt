package id.trydev.alumnifstku.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.ItemNewsBinding
import id.trydev.alumnifstku.model.News
import id.trydev.alumnifstku.utils.GlideApp
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter(private val context: Context, private val onClick: (News) -> Unit): RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    private val listNews = mutableListOf<News>()
    fun populateItem(listNews: List<News>) {
        this.listNews.clear()
        this.listNews.addAll(listNews)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(listNews[position])
    }

    override fun getItemCount(): Int = listNews.size

    inner class ViewHolder(val binding: ItemNewsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindItem(item: News) {
            binding.tvTitle.text = item.judul
            val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale("in", "ID"))
            val strToDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("in", "ID"))
                .parse(item.createdAt.toString())

            binding.tvNewsCreate.text = formatter.format(strToDate)
            if (item.uploader != null) {
                binding.tvUploader.text = String.format(context.resources.getString(
                    R.string.news_uploader), item.uploader.name)
            } else {
                binding.tvUploader.text = String.format(context.resources.getString(
                    R.string.like_count_template), "anonim")
            }

            GlideApp.with(context)
                .asBitmap()
                .centerCrop()
                .load(item.gambar)
                .placeholder(R.color.grey)
                .fallback(R.color.grey)
                .into(binding.ivNews)

            binding.itemBody.setOnClickListener { onClick(item) }

        }
    }

}