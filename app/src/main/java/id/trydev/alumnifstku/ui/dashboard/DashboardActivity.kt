package id.trydev.alumnifstku.ui.dashboard

import android.content.Intent
import id.trydev.alumnifstku.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import id.trydev.alumnifstku.databinding.ActivityDashboardBinding
import id.trydev.alumnifstku.databinding.ProgressDialogBinding
import id.trydev.alumnifstku.model.DefaultResponse
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.ui.biodata.BiodataActivity
import id.trydev.alumnifstku.ui.loker.LokerActivity
import id.trydev.alumnifstku.ui.kelas.KelasListActivity
import id.trydev.alumnifstku.ui.pengaturan.PengaturanActivity
import id.trydev.alumnifstku.ui.tracelist.TraceListActivity
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var viewModel: DashboardViewModel
    private lateinit var prefs: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences(this)

        val builder = AlertDialog.Builder(this, R.style.RoundedAlertDialog)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.progress_dialog, null)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        val scale = resources.displayMetrics.density
        alertDialog.window?.setLayout((300 * scale).toInt(), ConstraintLayout.LayoutParams.WRAP_CONTENT)


        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        if (!prefs.hasFillBio) {
            alertDialog.show()
            viewModel.isUserHasFillBiodata(prefs.token.toString())
        }
        /* Observe Request state changes */
        viewModel.state.observe({ lifecycle }, { state ->
            Log.d("OBSERVE", "state $state")
            when(state) {
                RequestState.REQUEST_START -> {
                    /* do nothing */
                }
                RequestState.REQUEST_END -> {
                    /* Hide progress bar and fetch response */
                    alertDialog.dismiss()
                }
                RequestState.REQUEST_ERROR -> {
                    /* Something happen.. Hide progress bar and fetch errors */
                    alertDialog.dismiss()
                }
                else -> { /* do nothing */ }
            }
        })

        /* Observe Response changes */
        viewModel.response.observe({ lifecycle }, { response ->
            Log.d("RESPONSE", response.toString())
            if (response != null) {
                // check server response
                // if false, navigate to biodata page
                if (response.success == false) {
                    panggang("Mohon isi biodata anda terlebih dahulu.")
                    startActivity(Intent(this, BiodataActivity::class.java))
                    finish()
                } else {
                    prefs.hasFillBio = true
                }
            }
        })

        /* Observe another Error possibility changes */
        viewModel.error.observe({ lifecycle }, { error ->
            if (error.isNotEmpty()) {
                panggangLong(error)
            }
        })

        binding.imgTracing.setOnClickListener(this)
        binding.imgLoker.setOnClickListener(this)
        binding.imgSharing.setOnClickListener(this)
        binding.imgKelas.setOnClickListener(this)
        binding.imgNews.setOnClickListener(this)
        binding.imgPengaturan.setOnClickListener(this)
    }

    override fun onClick(v: View) {

        when(v.id){

            // Tracing Alumni
            R.id.img_tracing -> {
                // panggang("Tracing Alumni")
                intent = Intent(this, TraceListActivity::class.java)
                startActivity(intent)
            }

            // Info Loker
            R.id.img_loker -> {
                intent = Intent(this, LokerActivity::class.java)
                startActivity(intent)
            }

            // Sharing Memory
            R.id.img_sharing -> {
                panggang("Sharing Memory")
            }

            // Kelas Alumni
            R.id.img_kelas -> {
                // panggang("Kelas Alumni")
                intent = Intent(this, KelasListActivity::class.java)
                startActivity(intent)
            }

            // FST NEWS
            R.id.img_news -> {
                panggang("FST News")
            }

            R.id.img_pengaturan -> {
                intent = Intent(this, PengaturanActivity::class.java)
                startActivity(intent)
            }
        }


    }

    fun panggang(string: String){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }

    fun panggangLong(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}