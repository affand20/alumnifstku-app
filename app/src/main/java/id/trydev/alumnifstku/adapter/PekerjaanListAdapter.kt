package id.trydev.alumnifstku.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.trydev.alumnifstku.databinding.LayoutRvPekerjaanlistBinding
import id.trydev.alumnifstku.model.Tracing

class PekerjaanListAdapter(private val context: Context): RecyclerView.Adapter<PekerjaanListAdapter.ViewHolder>() {

    private val listPekerjaan = mutableListOf<Tracing>()

    fun setPekerjaan(listPekerjaan: List<Tracing>){
        this.listPekerjaan.clear()
        this.listPekerjaan.addAll(listPekerjaan)
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: LayoutRvPekerjaanlistBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Tracing) {
            binding.traceTahunmasukDetails.text = item.tahunMasuk
            binding.tracePerusahaanDetails.text = item.perusahaan
            binding.traceClusterDetails.text = item.cluster
            binding.tracePekerjaanDetails.text = item.jabatan
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutRvPekerjaanlistBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listPekerjaan[position])
    }

    override fun getItemCount(): Int {
        return  listPekerjaan.size
    }
}