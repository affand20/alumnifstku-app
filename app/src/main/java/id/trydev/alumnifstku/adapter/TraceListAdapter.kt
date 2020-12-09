package id.trydev.alumnifstku.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.LayoutTraceListBinding
import id.trydev.alumnifstku.model.Alumni
import id.trydev.alumnifstku.model.Biodata
import id.trydev.alumnifstku.ui.tracedetails.TraceDetailsActivity
import kotlinx.android.synthetic.main.layout_trace_list.view.*

class TraceListAdapter(private val context: Context): RecyclerView.Adapter<TraceListAdapter.ViewHolder>() {

    private val data = mutableListOf<Alumni>()

    fun setData(items: List<Alumni>) {
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: LayoutTraceListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(alumni: Alumni){
            with(itemView) {


                binding.tvUserListName.text = alumni.biodata?.nama.toString()
                binding.tvUserListJurusan.text = alumni.biodata?.jurusan.toString()
                binding.tvUserListAngkatan.text = alumni.biodata?.angkatan.toString()

                if (alumni.biodata?.foto != null) {
                    Glide.with(context)
                        .asBitmap()
                        .centerCrop()
                        .fallback(ContextCompat.getDrawable(context, R.color.splash_blue))
                        .load(alumni.biodata?.foto)
                        .into(binding.imgUserList)
                }else{
                    binding.imgUserList.setImageResource(R.color.splash_blue)
                }

                itemView.setOnClickListener{
                    val intent = Intent(itemView.context, TraceDetailsActivity::class.java)

                    // gimana caranya naruh BIODATA buat dikirim ??
                    // apa dibikin parcelable ?

                    intent.putExtra(TraceDetailsActivity.ARG_UID, alumni.id)
                    itemView.context.startActivity(intent)
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutTraceListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}