package id.trydev.alumnifstku.ui.pengaturan.pekerjaan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.trydev.alumnifstku.databinding.ActivityPengaturanPekerjaanBinding

class PengaturanPekerjaanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengaturanPekerjaanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengaturanPekerjaanBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}