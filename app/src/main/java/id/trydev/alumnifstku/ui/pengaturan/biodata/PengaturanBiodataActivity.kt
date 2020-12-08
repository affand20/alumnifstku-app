package id.trydev.alumnifstku.ui.pengaturan.biodata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.trydev.alumnifstku.databinding.ActivityPengaturanBiodataBinding

class PengaturanBiodataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengaturanBiodataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengaturanBiodataBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}