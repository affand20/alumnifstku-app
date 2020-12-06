package id.trydev.alumnifstku.ui.tracedetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.ActivityTraceDetailsBinding

class TraceDetailsActivity : AppCompatActivity() {

    companion object {
        val ARG_NAME = "user_name"
    }

    private lateinit var binding: ActivityTraceDetailsBinding

    private var usernama: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTraceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usernama = intent.getStringExtra(ARG_NAME)

        binding.traceNamaDetails.text = usernama
    }
}