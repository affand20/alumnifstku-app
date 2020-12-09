package id.trydev.alumnifstku.ui.pengaturan.biodata

import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.datepicker.MaterialDatePicker
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.ActivityPengaturanBiodataBinding
import id.trydev.alumnifstku.databinding.FragmentPage1Binding
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.ui.dashboard.DashboardActivity
import id.trydev.alumnifstku.utils.GlideApp
import id.trydev.alumnifstku.utils.RealPathUtil
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class PengaturanBiodataActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: ActivityPengaturanBiodataBinding
    private lateinit var pref: AppPreferences
    private lateinit var mv: PengaturanBiodataViewModel

    companion object {
        const val rcStorage = 101
        const val rcChoose = 100
    }

    private var filePath = ""

    private var query = hashMapOf<String, String>(
            "nama" to "",
            "alamat" to "",
            "umur" to "",
            "ttl" to "",
            "jenis kelamin" to "",
            "angkatan" to "",
            "jurusan" to "",
            "linkedin" to "",

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengaturanBiodataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = AppPreferences(this)

        mv = ViewModelProvider(this).get(PengaturanBiodataViewModel::class.java)

        val majors = resources.getStringArray(R.array.major)
        binding.edtMajor.setAdapter(
                ArrayAdapter(this, R.layout.simple_item_spinner, majors)
        )

        mv.getBiodata(pref.token.toString())

        /* Observe Request state changes */
        mv.state.observe({ lifecycle }, { state ->
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
        mv.response.observe({ lifecycle }, { response ->
            Log.d("RESPONSE", response.toString())
            if (response != null) {
                // check server response
                // if success, populate data
                // else, show error Toast
                if (response.success == true) {
                    // populate data
                    response.data?.let { biodata ->

                        binding.edtFullname.setText(biodata.nama.toString())
                        binding.edtAddress.setText(biodata.alamat.toString())
                        binding.edtOld.setText(biodata.umur.toString())
//                        binding.edtBirthPlace.setText(biodata.ttl.toString())
                        binding.edtMajor.setText(biodata.jurusan.toString())
                        binding.edtAngkatan.setText(biodata.angkatan.toString())
                        binding.edtLinkedin.setText(biodata.linkedin.toString())

                        if (biodata.jenisKelamin.toString() == "Perempuan"){
                            binding.radioGroup.check(R.id.rb_female)
                        }else{
                            binding.radioGroup.check(R.id.rb_male)
                        }

                        if (biodata.foto.toString() != null){
                            filePath = ""

                            GlideApp.with(this)
                                    .asBitmap()
                                    .transform(CenterCrop())
                                    .load(biodata.foto.toString())
                                    .into(binding.ivProfilePic)
                        }
                    }

                } else {
                    // binding.stateEmpty.visibility = View.VISIBLE
                    // binding.stateEmpty.text = response.message
                }
            }
        })

        /* Observe another Error possibility changes */
        mv.error.observe({ lifecycle }, { error ->
            if (error.isNotEmpty()) {
                // binding.stateEmpty.visibility = View.VISIBLE
                // binding.stateEmpty.text = error
            }
        })

        // submit update data
        binding.btnNext.setOnClickListener {
            Log.d("CHECK PATH", "path = $filePath")
            if(validate(binding)) {

                var jk = ""
                if (binding.radioGroup.checkedRadioButtonId == R.id.rb_male) {
                    jk = binding.rbMale.text.toString()
                } else if (binding.radioGroup.checkedRadioButtonId == R.id.rb_female) {
                    jk = binding.rbFemale.text.toString()
                }

                query["nama"] = binding.edtFullname.text.toString()
                query["alamat"] = binding.edtAddress.text.toString()
                query["umur"] = binding.edtOld.text.toString()
                query["jenis kelamin"] = jk
                query["ttl"] = "${binding.edtBirthPlace.text.toString()}, ${binding.edtBirthDate.text.toString()}"
                query["angkatan"] = binding.edtAngkatan.text.toString()
                query["jurusan"] = binding.edtMajor.text.toString()

                if (binding.edtLinkedin.text.toString().isNotEmpty()) {
                    query["linkedin"] = binding.edtLinkedin.text.toString()
                }


                mv.addBioAttr(query)
                mv.postBiodata(pref.token.toString())

                /* Observe Request state changes */
                mv.state.observe({ lifecycle }, { state ->
                    Log.d("OBSERVE", "state $state")
                    when(state) {
                        RequestState.REQUEST_START -> {
                            /* Show progress bar */
                            // binding.progressBar.visibility = View.VISIBLE
                            binding.errorMsg.visibility = View.GONE
                            // binding.btnSave.visibility = View.GONE
                        }
                        RequestState.REQUEST_END -> {
                            /* Hide progress bar and fetch response */
                            // binding.progressBar.visibility = View.GONE
                            // binding.btnSave.visibility = View.VISIBLE
                        }
                        RequestState.REQUEST_ERROR -> {
                            /* Something happen.. Hide progress bar and fetch errors */
                            // binding.progressBar.visibility = View.GONE
                            // binding.btnSave.visibility = View.VISIBLE
                        }
                        else -> { /* do nothing */ }
                    }
                })

                /* Observe Response changes */
                mv.submitresponse.observe({ lifecycle }, { response ->
                    Log.d("RESPONSE", response.toString())
                    if (response != null) {
                        // check server response
                        // if success, navigate to dashboard page
                        // else, show error Toast
                        if (response.success == true) {
                            // save preference to prevent checking status everytime
                            pref.hasFillBio = true
                            // send greeting message
                            finish()
                        } else {
                            binding.errorMsg.visibility = View.VISIBLE
                            binding.errorMsg.text = response.message
                        }
                    }
                })

                /* Observe another Error possibility changes */
                mv.error.observe({ lifecycle }, { error ->
                    if (error.isNotEmpty()) {
                        binding.errorMsg.visibility = View.VISIBLE
                        binding.errorMsg.text = error
                    }
                })
            }


        }

        /* Date Picker settings */
        val builder = MaterialDatePicker.Builder.datePicker()
        val picker = builder.build()
        // Date Picker trigger
        binding.edtBirthDate.setOnClickListener {
            picker.show(this.supportFragmentManager, picker.toString())
        }
        picker.addOnPositiveButtonClickListener {
            Log.d(
                    "DatePicker Activity",
                    "Date String = ${picker.headerText}::  Date epoch value = ${picker.selection}"
            )
            // assign to edittext
            binding.edtBirthDate.setText(picker.headerText)
        }

        /* Select Photo from gallery */
        binding.btnChangePhoto.setOnClickListener {
            storageTask()
        }


    }

    private fun validate(binding: ActivityPengaturanBiodataBinding): Boolean {
        var msg = mutableListOf<String>()
        if (binding.edtFullname.text.toString().isEmpty()) {
            binding.edtFullname.error = "Wajib diisi"
            return false
        }
        if (binding.edtAddress.text.toString().isEmpty()) {
            binding.edtAddress.error = "Wajib diisi"
            return false
        }
        if (binding.edtOld.text.toString().isEmpty()) {
            binding.edtOld.error = "Wajib diisi"
            return false
        }
        if (binding.edtBirthPlace.text.toString().isEmpty()) {
            binding.edtBirthPlace.error = "Wajib diisi"
            return false
        }
        if (binding.edtBirthDate.text.toString().isEmpty()) {
            binding.edtBirthDate.error = "Wajib diisi"
            return false
        }
        if (binding.radioGroup.checkedRadioButtonId == -1) {
            msg.add("Mohon isi bagian jenis kelamin")
            return false
        }
        if (File(filePath).length()/1024 > 2048) {
            msg.add("Mohon pilih foto dengan ukuran file yg lebih kecil")
            return false
        }

        return true
    }

    @AfterPermissionGranted(rcStorage)
    private fun storageTask() {
        if (hasStoragePermission()) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            startActivityForResult(Intent.createChooser(intent, "Pilih File"), rcChoose)
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "Permission",
                    rcStorage,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rcChoose && resultCode == Activity.RESULT_OK && data!=null && data.data!=null) {
            val uri = data.data     // ini hasil file pdf yang dipilih

            val path = RealPathUtil.getRealPath(this, uri)
            Log.d("PATH", path)

            filePath = path

            GlideApp.with(this)
                    .asBitmap()
                    .transform(CenterCrop())
                    .load(data.data)
                    .into(binding.ivProfilePic)

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun hasStoragePermission(): Boolean = EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d("PERMISSION_GRANTED", "$perms")
    }
}