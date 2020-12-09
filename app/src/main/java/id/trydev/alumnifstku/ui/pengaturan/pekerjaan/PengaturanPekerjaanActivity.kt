package id.trydev.alumnifstku.ui.pengaturan.pekerjaan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.ActivityPengaturanPekerjaanBinding
import id.trydev.alumnifstku.prefs.AppPreferences

class PengaturanPekerjaanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengaturanPekerjaanBinding
    private lateinit var prefs: AppPreferences
    private lateinit var viewModel: PengaturanPekerjaanViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengaturanPekerjaanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val clusters = resources.getStringArray(R.array.cluster)

        binding.edtCluster.setAdapter(
                ArrayAdapter(this, R.layout.simple_item_spinner, clusters)
        )

        prefs = AppPreferences(this)

        viewModel = ViewModelProvider(this).get(PengaturanPekerjaanViewModel::class.java)

        binding.btnNext.setOnClickListener {

            val company = binding.edtCompany.text.toString()
            val cluster = binding.edtCluster.text.toString()
            val jabatan = binding.edtPosition.text.toString()
            val tahunmasuk = binding.edtYearJoined.text.toString()

        }
    }
}