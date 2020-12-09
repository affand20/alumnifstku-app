package id.trydev.alumnifstku.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.LayoutKelasListBinding
import id.trydev.alumnifstku.model.Kelas
import id.trydev.alumnifstku.ui.kelas.KelasListActivity
import id.trydev.alumnifstku.ui.kelas.kelasdetails.KelasDetailsActivity
import id.trydev.alumnifstku.utils.GlideApp
import kotlinx.android.synthetic.main.layout_kelas_list.view.*
import java.text.SimpleDateFormat
import java.util.*

class KelasListAdapter(private val context: Context, private val onClick: (Kelas) -> Unit): RecyclerView.Adapter<KelasListAdapter.ViewHolder>() {

    private val data = mutableListOf<Kelas>()

    fun setData(items: List<Kelas>){
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: LayoutKelasListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(kelas: Kelas) {
            with(itemView) {

                binding.tvTitle.text = kelas.judul
                if (kelas.speaker != null) {
                    // map list of DataSpeaker into list of String
                    val speakers = kelas.speaker.map { it.pembicara }
                    // if speaker more than 3, show it using string templates
                    if (kelas.speaker.size > 3) {
                        binding.tvSpeaker.text = String.format(
                                context.resources.getString(R.string.speaker_list_template),
                                speakers.subList(0,3).joinToString(", "),
                                kelas.speaker.size - 3
                        )
                    // otherwise just separate with comma
                    } else {
                        binding.tvSpeaker.text = String.format(
                                context.resources.getString(R.string.speaker_one_template),
                                speakers.joinToString(", ")
                        )
                    }
                }
                binding.tvQuotaLeft.text = String.format(
                    context.resources.getString(R.string.quota_left),
                    (kelas.kuota.toString().toInt() - kelas.participants?.size.toString().toInt()))
                // date format converter
                val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale("in", "ID"))
                // date parser
                val strToDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("in", "ID"))
                        .parse(kelas.tanggal.toString())
                binding.tvDate.text = formatter.format(strToDate)

                binding.itemBody.setOnClickListener { onClick(kelas) }

                GlideApp.with(context)
                        .asBitmap()
                        .fallback(R.color.grey)
                        .placeholder(R.color.grey)
                        .load(kelas.poster)
                        .into(binding.ivPosterKelas)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // refactor using viewBinding
        val binding = LayoutKelasListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // yang bener
         holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size
}