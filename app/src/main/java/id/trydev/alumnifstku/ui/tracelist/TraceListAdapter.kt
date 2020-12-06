package id.trydev.alumnifstku.ui.tracelist

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.model.Biodata
import id.trydev.alumnifstku.ui.tracedetails.TraceDetailsActivity
import kotlinx.android.synthetic.main.layout_trace_list.view.*

class TraceListAdapter: RecyclerView.Adapter<TraceListAdapter.ViewHolder>() {

    private val data = ArrayList<Biodata>()

    fun setData(items: ArrayList<Biodata>) {
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(biodata: Biodata){
            with(itemView) {

                tv_user_list_name.text = biodata.nama

                itemView.setOnClickListener{
                    val intent = Intent(itemView.context, TraceDetailsActivity::class.java)
                    intent.putExtra(TraceDetailsActivity.ARG_NAME, biodata.nama)
                    itemView.context.startActivity(intent)
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.layout_trace_list, parent, false)
        return ViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}