package id.trydev.alumnifstku.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.ItemNotifMemoryBinding
import id.trydev.alumnifstku.model.Notif
import id.trydev.alumnifstku.model.Post
import id.trydev.alumnifstku.utils.GlideApp
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NotifMemoryAdapter(private val context: Context, val onClick: (Post) -> Unit): RecyclerView.Adapter<NotifMemoryAdapter.ViewHolder>() {

    private val listNotif = mutableListOf<Notif>()
    fun populateData(listNotif: List<Notif>) {
        this.listNotif.clear()
        this.listNotif.addAll(listNotif)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotifMemoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listNotif[position])
    }

    override fun getItemCount(): Int = listNotif.size


    inner class ViewHolder(private val binding: ItemNotifMemoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Notif) {

            if (item.is_read == "0") {
                binding.itemBody.setBackgroundColor(Color.parseColor("#579EFE"))
            }

            binding.notifMsg.text = item.text
            binding.notifTime.text = item.createdAt?.let { timeFormatter(it) }

            GlideApp.with(context)
                    .asBitmap()
                    .centerInside()
                    .fallback(R.color.grey)
                    .load(item.post?.foto)
                    .into(binding.notifImg)

            binding.itemBody.setOnClickListener { item.post?.let { it1 -> onClick(it1) } }
        }

    }

    private fun timeFormatter(dateString: String): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("in","ID"))
        val past = format.parse(dateString)

        val now = Date()

        val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
        val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time)
        val days = TimeUnit.MILLISECONDS.toDays(now.time - past.time)

        var kembalian = ""

        if(seconds<60)
        {
            kembalian = "${seconds.toString()} dtk"
        }
        else if(minutes<60)
        {
            kembalian = "${minutes.toString()} mnt"
        }
        else if(hours<24)
        {
            kembalian = "${hours.toString()} jam"
        }
        else
        {
            kembalian = "${days.toString()} hari"
        }

        return kembalian
    }
}