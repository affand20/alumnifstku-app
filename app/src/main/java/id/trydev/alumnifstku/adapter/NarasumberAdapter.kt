package id.trydev.alumnifstku.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.ItemNarasumberBinding
import id.trydev.alumnifstku.model.Alumni
import id.trydev.alumnifstku.model.DataSpeaker
import id.trydev.alumnifstku.utils.GlideApp
import kotlinx.android.synthetic.main.fragment_password_bottom.view.*

class NarasumberAdapter(private val context: Context): RecyclerView.Adapter<NarasumberAdapter.ViewHolder>() {

    private val listNarasumber = mutableListOf<DataSpeaker>()
    fun populateData(listNarasumber: List<DataSpeaker>) {
        this.listNarasumber.clear()
        this.listNarasumber.addAll(listNarasumber)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNarasumberBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(listNarasumber[position])
    }

    override fun getItemCount(): Int = listNarasumber.size

    inner class ViewHolder(private val binding: ItemNarasumberBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindItem(item: DataSpeaker) {
            binding.tvSpeakerName.text = item.pembicara
            binding.tvSpeakerAbout.text = item.tentang

            GlideApp.with(context)
                    .asBitmap()
                    .placeholder(R.color.grey)
                    .fallback(R.color.grey)
                    .load(item.foto)
                    .into(binding.ivSpeakerPic)

        }
    }
}