package id.trydev.alumnifstku.ui.pengaturan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.ActivityPengaturanBinding
import id.trydev.alumnifstku.ui.pengaturan.bottomdialog.PasswordFragmentBottom

class PengaturanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengaturanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengaturanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pengaturanJurusanDetails.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.list_jurusan,
            R.layout.spinner_tracefilter
        ).also {
            it.setDropDownViewResource(R.layout.spinner_layout)
        }

        binding.pengaturanGantipassword.setOnClickListener {
            val passwordFragmentBottom = PasswordFragmentBottom()
            passwordFragmentBottom.show(supportFragmentManager, passwordFragmentBottom.tag)
        }
    }
}