package id.trydev.alumnifstku.ui.kelasdetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.ActivityKelasDetailsBinding
import kotlinx.android.synthetic.main.layout_bottom_sheet_kelasalumni.view.*

class KelasDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKelasDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKelasDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBookingsekarang.setOnClickListener {

            val view = layoutInflater.inflate(R.layout.layout_bottom_sheet_kelasalumni, null)

            val dialog = BottomSheetDialog(this)

            view.btn_sheet_bookingsekarang.setOnClickListener {
                var nama = view.et_kelasalumni_nama.text.toString()
                var email = view.et_kelasalumni_email.text.toString()
                var nowa = view.et_kelasalumni_whatsapp.text.toString()
                var str = "Nama ${nama}, Email ${email}, No WA ${nowa}"

                Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            dialog.setContentView(view)
            dialog.show()
        }
    }
}