package id.trydev.alumnifstku.ui.trace

import android.graphics.Color.parseColor
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import id.trydev.alumnifstku.R
import kotlinx.android.synthetic.main.activity_tracing.*

class TracingActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var jurusan: String
    private lateinit var nama: String
    private lateinit var angkatan: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracing)

        // atur gradasi untuk judul
        tv_tracing_title.apply {
            setTextGradientColor(intArrayOf(
                parseColor("#599efa"),
                parseColor("#4275F3")
            ))
        }

        // atur adapter untuk spinner
        ArrayAdapter.createFromResource(
            this, R.array.list_jurusan, R.layout.spinner_layout
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_layout)
            spinner_tracing.adapter = adapter
        }

        // spinner item selected listener
        spinner_tracing.onItemSelectedListener = this

        btn_search_tracing.setOnClickListener {
            Toast.makeText(this, "ABRACADABRA", Toast.LENGTH_SHORT).show()
        }

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

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        val items = parent.selectedItem as String
        Log.d("SPINNER ITEM SELECTED : ", items)
        Toast.makeText(this, items, Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}