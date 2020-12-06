package id.trydev.alumnifstku.ui.kelas

import android.graphics.Color.parseColor
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.ActivityKelasListBinding
import id.trydev.alumnifstku.model.Kelas

class KelasListActivity : AppCompatActivity() {

    private lateinit var binding : ActivityKelasListBinding
    private lateinit var kelasList: ArrayList<Kelas>

    // data dummy
    private var dummyKelasList = ArrayList<DummyKelas>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKelasListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // gradient judul
        binding.titlePage.apply {
            setTextGradientColor(intArrayOf(
                parseColor("#599efa"),
                parseColor("#4275F3")
            ))
        }

        // set adapter untuk recycler view
        var adapter = KelasListAdapter()
        binding.rvKelaslist.setHasFixedSize(true)
        binding.rvKelaslist.layoutManager = LinearLayoutManager(this)
        binding.rvKelaslist.adapter = adapter

        // set dummy data ke adapter
        setDummyKelasList()
        adapter.setDummyData(dummyKelasList)

    }

    // function untuk mengatur warna gradasi sebuah Text
    fun TextView.setTextGradientColor(colors: IntArray) {
        val width = paint.measureText(text.toString())
        val textShader = LinearGradient(
            0f, 0f, width, textSize, colors, null, Shader.TileMode.CLAMP
        )
        setTextColor(colors[0])
        paint.shader = textShader
    }

    fun setDummyKelasList(){
        var listnama = resources.getStringArray(R.array.dummy_list_tracing_nama)
        var listtema = resources.getStringArray(R.array.dummy_list_temakelas)

        for (i in 0 until listnama.size) {
            var k = DummyKelas()
            k.tema = listtema[i]
            k.pembicara = listnama[i]
            dummyKelasList.add(k)
        }
    }

    class DummyKelas{
        var tema: String? = null
        var pembicara: String? = null
    }
}