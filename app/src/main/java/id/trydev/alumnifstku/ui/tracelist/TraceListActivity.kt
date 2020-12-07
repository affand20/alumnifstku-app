package id.trydev.alumnifstku.ui.tracelist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.ActivityTraceListBinding
import id.trydev.alumnifstku.model.Biodata
import kotlinx.android.synthetic.main.layout_bottom_sheet_trace.view.*

class TraceListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTraceListBinding

    private lateinit var rv: RecyclerView
    private lateinit var vAdapter: TraceListAdapter
    private lateinit var vManager: RecyclerView.LayoutManager

    private lateinit var cariOrang: CariOrang

    private var filterjurusan: String? = null
    private var filtercluster: String? = null
    private var filterwaktu: String? = null

    private var listData = ArrayList<Biodata>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTraceListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cariOrang = CariOrang()

        vManager = LinearLayoutManager(this)
        vAdapter = TraceListAdapter()
        rv = binding.rvTracelist.apply {
            setHasFixedSize(true)

            layoutManager = vManager

            adapter = vAdapter
        }

        setDummyData()
        vAdapter.setData(listData)

        binding.spinnerTraceJurusan.adapter = ArrayAdapter.createFromResource(this, R.array.list_jurusan, R.layout.spinner_tracefilter).also {
            it.setDropDownViewResource(R.layout.spinner_tracefilter)
        }

        binding.spinnerTraceJurusan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                filterjurusan = parent.selectedItem as String
                Toast.makeText(view.context, "Filter : ${filterjurusan}", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        binding.btnCariAlumni.setOnClickListener {

            val view = layoutInflater.inflate(R.layout.layout_bottom_sheet_trace, null)

            val dialog = BottomSheetDialog(this)

            ArrayAdapter.createFromResource(
                    this, R.array.list_jurusan, R.layout.spinner_layout
            ).also { adapter ->
                adapter.setDropDownViewResource(R.layout.spinner_layout)
                view.spinner_tracing.adapter = adapter
            }

            view.spinner_tracing.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    cariOrang.jurusan = parent.selectedItem as String
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }
            view.btn_search_tracing.setOnClickListener {

                cariOrang.nama = view.et_tracing_nama.text.toString()
                cariOrang.angkatan = view.et_tracing_angkatan.text.toString()

                var str = "Nama : ${cariOrang.nama}, Angkatan : ${cariOrang.angkatan}, Jurusan ${cariOrang.jurusan}"

                Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            dialog.setContentView(view)
            dialog.show()

        }


        // gunakan viewModel untuk dapatkan data yang direquest
        // kemudian parsing data ke recyclerView dengan
        
        // (vAdapter as TraceListAdapter).setData(listData)


    }

    fun setDummyData(){
        var stringlist = resources.getStringArray(R.array.dummy_list_tracing_nama)
        for (i in 0 until stringlist.size) {
            var b = Biodata()
            b.nama = stringlist[i]
            listData.add(b)
        }
    }

    class CariOrang {
        var nama: String? = null
        var angkatan: String? = null
        var jurusan: String? = null
    }


}