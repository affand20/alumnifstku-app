package id.trydev.alumnifstku.ui.pengaturan

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.adapter.PekerjaanListAdapter
import id.trydev.alumnifstku.databinding.ActivityPengaturanBinding
import id.trydev.alumnifstku.databinding.DialogOpsiSettingBinding
import id.trydev.alumnifstku.databinding.FragmentPasswordBottomBinding
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.ui.pengaturan.biodata.PengaturanBiodataActivity
import id.trydev.alumnifstku.ui.pengaturan.pekerjaan.PengaturanPekerjaanActivity
import id.trydev.alumnifstku.ui.tracedetails.TraceDetailsViewModel

class PengaturanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengaturanBinding
    private lateinit var prefs: AppPreferences
    private lateinit var adapter: PekerjaanListAdapter
    private lateinit var viewModel: TraceDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengaturanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences(this)

        adapter = PekerjaanListAdapter(this, true, prefs.token.toString())

        viewModel = ViewModelProvider(this).get(TraceDetailsViewModel::class.java)

        viewModel.getAlumni(prefs.token.toString(), prefs.userId?.toInt())

        binding.rvPekerjaanlist.layoutManager = LinearLayoutManager(this)
        binding.rvPekerjaanlist.adapter = adapter


        /* Observe Request state changes */
        viewModel.state.observe({ lifecycle }, { state ->
            Log.d("OBSERVE", "state $state")
            when(state) {
                RequestState.REQUEST_START -> {
                    /* Show progress bar */
                    // binding.swipeRefresh.isRefreshing = true
                    // binding.stateEmpty.visibility = View.GONE
                }
                RequestState.REQUEST_END -> {
                    /* Hide progress bar and fetch response */
                    // binding.swipeRefresh.isRefreshing = false
                }
                RequestState.REQUEST_ERROR -> {
                    /* Something happen.. Hide progress bar and fetch errors */
                    // binding.swipeRefresh.isRefreshing = false
                }
                else -> { /* do nothing */ }
            }
        })

        /* Observe Response changes */
        viewModel.response.observe({ lifecycle }, { response ->
            Log.d("RESPONSE", response.toString())
            if (response != null) {
                // check server response
                // if success, populate data
                // else, show error Toast
                if (response.success == true) {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    // populate data
                    response.data?.biodata?.let { alumni ->

                        if (alumni.foto != null) {
                            Glide.with(this)
                                    .asBitmap()
                                    .centerCrop()
                                    .load(alumni.foto)
                                    .into(binding.traceImgDetails)
                        }

                        binding.traceNamaDetails.text = alumni.nama
                        binding.traceJurusanDetails.text = alumni.jurusan
                        binding.traceAngkatanDetails.text = alumni.angkatan
                        binding.traceLinkedinDetails.text = alumni.linkedin
                        binding.traceAlamatDetails.text = alumni.alamat
                    }

                    response.data?.tracing?.let {
                        /* do nothing */
                        adapter.setPekerjaan(it)
                        // belum bikin recycler nya bozz
                    }
                } else {
                    // binding.stateEmpty.visibility = View.VISIBLE
                    // binding.stateEmpty.text = response.message
                }
            }
        })

        /* Observe another Error possibility changes */
        viewModel.error.observe({ lifecycle }, { error ->
            if (error.isNotEmpty()) {
                // binding.stateEmpty.visibility = View.VISIBLE
                // binding.stateEmpty.text = error
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        })

        binding.btnSetting.setOnClickListener {
            // munculkan dialog bottom untuk memilih action

            val bindOpsiSetting = DialogOpsiSettingBinding.inflate(LayoutInflater.from(this))

            val dialog = BottomSheetDialog(this)

            bindOpsiSetting.btnChangeBiodata.setOnClickListener {
                // ganti biodata
                startActivity(Intent(this, PengaturanBiodataActivity::class.java))
            }

            bindOpsiSetting.btnChangePekerjaan.setOnClickListener {
                // ganti pekerjaan
                startActivity(Intent(this, PengaturanPekerjaanActivity::class.java))

            }

            bindOpsiSetting.btnChangePassword.setOnClickListener {
                // ganti password
                dialog.dismiss()
                showPasswordDialog()
            }

            dialog.setContentView(bindOpsiSetting.root)
            dialog.show()
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getAlumni(prefs.token.toString(), prefs.userId?.toInt())

        /* Observe Response changes */
        viewModel.response.observe({ lifecycle }, { response ->
            Log.d("RESPONSE", response.toString())
            if (response != null) {
                // check server response
                // if success, populate data
                // else, show error Toast
                if (response.success == true) {
                    // populate data
                    response.data?.biodata?.let { alumni ->

                        if (alumni.foto != null) {
                            Glide.with(this)
                                    .asBitmap()
                                    .centerCrop()
                                    .load(alumni.foto)
                                    .into(binding.traceImgDetails)
                        }

                        binding.traceNamaDetails.text = alumni.nama
                        binding.traceJurusanDetails.text = alumni.jurusan
                        binding.traceAngkatanDetails.text = alumni.angkatan
                        binding.traceLinkedinDetails.text = alumni.linkedin
                        binding.traceAlamatDetails.text = alumni.alamat
                    }

                    response.data?.tracing?.let {
                        /* do nothing */
                        adapter.setPekerjaan(it)
                        // belum bikin recycler nya bozz
                    }
                } else {
                    // binding.stateEmpty.visibility = View.VISIBLE
                    // binding.stateEmpty.text = response.message
                }
            }
        })


    }

    private fun showPasswordDialog(){
        val bindPasswordBinding = FragmentPasswordBottomBinding.inflate(LayoutInflater.from(this))

        val dialog = BottomSheetDialog(this)

        bindPasswordBinding.resetButtonGantiPassword.setOnClickListener {
            dialog.dismiss()
        }
        // mengatur tulisan2 didalamnya dan action2 nya
        bindPasswordBinding.toolbar.title = "Ganti Password"
        bindPasswordBinding.toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_round_close_24)
        bindPasswordBinding.toolbar.setNavigationOnClickListener {
            dialog.dismiss()
        }

        // tombol untuk menutup dialog
        bindPasswordBinding.resetButtonGantiPassword.setOnClickListener {
//            dialog.dismiss()
            if (validate(bindPasswordBinding)) {
                viewModel.changePassword(
                        prefs.token.toString(),
                        bindPasswordBinding.resetPasswordLama.text.toString(),
                        bindPasswordBinding.resetPasswordBaru.text.toString()
                )
                dialog.dismiss()
            }
        }


        dialog.setContentView(bindPasswordBinding.root)
        dialog.show()
    }

    private fun validate(binding: FragmentPasswordBottomBinding): Boolean {
        if (binding.resetPasswordLama.text.isEmpty()) {
            binding.resetPasswordLama.error = "Wajib diisi"
            binding.resetPasswordLama.requestFocus()
            return false
        }
        if (binding.resetPasswordBaru.text.isEmpty()) {
            binding.resetPasswordBaru.error = "Wajib diisi"
            binding.resetPasswordBaru.requestFocus()
            return false
        }
        if (binding.resetPasswordBaruUlang.text.isEmpty()) {
            binding.resetPasswordBaruUlang.error = "Wajib diisi"
            binding.resetPasswordBaruUlang.requestFocus()
            return false
        }
        if (binding.resetPasswordBaru.text.toString() != binding.resetPasswordBaruUlang.text.toString()) {
            binding.resetPasswordBaruUlang.error = "Password tidak cocok."
            binding.resetPasswordBaruUlang.requestFocus()
            return false
        }
        return true
    }

}