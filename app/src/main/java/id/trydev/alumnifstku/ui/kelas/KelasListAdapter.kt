package id.trydev.alumnifstku.ui.kelas

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.model.Kelas
import id.trydev.alumnifstku.ui.kelasdetails.KelasDetailsActivity
import kotlinx.android.synthetic.main.layout_kelas_list.view.*

class KelasListAdapter: RecyclerView.Adapter<KelasListAdapter.ViewHolder>() {

    private val data = ArrayList<Kelas>()

    private val dummydata = ArrayList<KelasListActivity.DummyKelas>()

    fun setData(items: ArrayList<Kelas>){
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    fun setDummyData(items: ArrayList<KelasListActivity.DummyKelas>){
        dummydata.clear()
        dummydata.addAll(items)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(kelas: Kelas) {
            with(itemView) {

                kelas_tema.text = kelas.judul
                kelas_pembicara.text = kelas.speaker?.get(0)?.pembicara ?: "-"
                kelas_kuota.text = kelas.kuota

                itemView.setOnClickListener {

                    val intent = Intent(itemView.context, KelasDetailsActivity::class.java)

                    // parsing id acaranya jangan lupa dulu
                    // intent.putExtra("STRING_INDEX","VALUE")

                    itemView.context.startActivity(intent)
                }
            }
        }

        fun dummybind(kelas: KelasListActivity.DummyKelas){
            with(itemView){
                kelas_tema.text = kelas.tema
                kelas_pembicara.text = kelas.pembicara
                kelas_kuota.text = "10/20"

                itemView.setOnClickListener {
                    Toast.makeText(itemView.context, "pilih ${kelas.tema}, pembicara ${kelas.pembicara}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(itemView.context, KelasDetailsActivity::class.java)
                    itemView.context.startActivity(intent)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.layout_kelas_list, parent, false)
        return ViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // yang bener
        // holder.bind(data[position])

        // dummy dulu
        holder.dummybind(dummydata[position])
    }

    override fun getItemCount(): Int {

        // yang bener
        // return data.size

        // dummy dulu
        return dummydata.size
    }
}