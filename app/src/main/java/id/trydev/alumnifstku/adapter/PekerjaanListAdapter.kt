package id.trydev.alumnifstku.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.trydev.alumnifstku.databinding.DialogOpsiEditpekerjaanBinding
import id.trydev.alumnifstku.databinding.DialogOpsiSettingBinding
import id.trydev.alumnifstku.databinding.LayoutRvPekerjaanlistBinding
import id.trydev.alumnifstku.model.Tracing
import id.trydev.alumnifstku.network.ApiFactory
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.network.Result
import id.trydev.alumnifstku.ui.pengaturan.biodata.PengaturanBiodataActivity
import id.trydev.alumnifstku.ui.pengaturan.pekerjaan.PengaturanPekerjaanActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PekerjaanListAdapter(private val context: Context, private val status: Boolean, private val token: String?): RecyclerView.Adapter<PekerjaanListAdapter.ViewHolder>() {

    private val listPekerjaan = mutableListOf<Tracing>()

    fun setPekerjaan(listPekerjaan: List<Tracing>){
        this.listPekerjaan.clear()
        this.listPekerjaan.addAll(listPekerjaan)
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: LayoutRvPekerjaanlistBinding, private val status: Boolean, private val token: String?) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var uid: String
        fun bind(item: Tracing) {
            binding.traceTahunmasukDetails.text = item.tahunMasuk
            binding.tracePerusahaanDetails.text = item.perusahaan
            binding.traceClusterDetails.text = item.cluster
            binding.tracePekerjaanDetails.text = item.jabatan

            if (status){
                binding.btnEditTrigger.visibility = View.VISIBLE
                binding.btnEditTrigger.setOnClickListener {

                    val bindDialog = DialogOpsiEditpekerjaanBinding.inflate(LayoutInflater.from(it.context))
                    val dialog = BottomSheetDialog(it.context)

                    bindDialog.keteranganKerjaan.text = "Mulai berkerja pada ${item.tahunMasuk} di ${item.perusahaan} yang bergerak pada bidang ${item.cluster} menempati Jabatan ${item.jabatan}"

                    bindDialog.btnEditRemove.setOnClickListener {
                        Toast.makeText(it.context, "REMOVE", Toast.LENGTH_SHORT).show()

                        /* Untuk asynchronous execution */
                         var job = Job()
                         val uiScope = CoroutineScope(job+ Dispatchers.Main)
                        uid = item.id.toString()

                        if (token != null){
                        uiScope.launch {

                            try {
                                when(val response = ApiFactory.removeTrace(
                                        token,
                                        uid.toInt()
                                )){
                                    is Result.Success -> {
                                        Toast.makeText(it.context, "SUCCESS REMOVE", Toast.LENGTH_LONG).show()
                                        dialog.dismiss()
                                    }

                                    is Result.Error -> {
                                        Toast.makeText(it.context, response.exception, Toast.LENGTH_LONG).show()
                                    }

                                }
                            } catch (t: Throwable) {
                                Toast.makeText(it.context, t.localizedMessage, Toast.LENGTH_LONG).show()
                            }
                        }
                        }

                        dialog.dismiss()

                    }

                    bindDialog.btnEditChange.setOnClickListener {
                        it.context.startActivity(
                                Intent(it.context, PengaturanPekerjaanActivity::class.java)
                                        .putExtra(PengaturanPekerjaanActivity.ARG_EDIT, true)
                                        .putExtra(PengaturanPekerjaanActivity.ARG_ID, item.id)
                                        .putExtra(PengaturanPekerjaanActivity.ARG_COMPANY, item.perusahaan)
                                        .putExtra(PengaturanPekerjaanActivity.ARG_POS, item.jabatan)
                                        .putExtra(PengaturanPekerjaanActivity.ARG_IN, item.tahunMasuk)
                                        .putExtra(PengaturanPekerjaanActivity.ARG_CLUSTER, item.cluster)

                        )
                        dialog.dismiss()
                    }

                    dialog.setContentView(bindDialog.root)
                    dialog.show()
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutRvPekerjaanlistBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding, status, token)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listPekerjaan[position])
    }

    override fun getItemCount(): Int {
        return  listPekerjaan.size
    }
}