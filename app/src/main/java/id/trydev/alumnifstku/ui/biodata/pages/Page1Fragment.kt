package id.trydev.alumnifstku.ui.biodata.pages

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.datepicker.MaterialDatePicker
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.FragmentPage1Binding
import id.trydev.alumnifstku.ui.biodata.BiodataViewModel
import id.trydev.alumnifstku.utils.GlideApp
import id.trydev.alumnifstku.utils.GlideModule
import id.trydev.alumnifstku.utils.RealPathUtil
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class Page1Fragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private val viewModel: BiodataViewModel by activityViewModels()
    private lateinit var binding: FragmentPage1Binding

    private var filePath = ""

    companion object {
        const val rcStorage = 101
        const val rcChoose = 100
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPage1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* Navigate to page2 */
        binding.btnNext.setOnClickListener {
            binding.errorMsg.visibility = View.GONE
            Log.d("CHECK PATH", "path = $filePath")
            if (validate(binding)) {

                var jk = ""
                if (binding.radioGroup.checkedRadioButtonId == R.id.rb_male) {
                    jk = binding.rbMale.text.toString()
                } else if (binding.radioGroup.checkedRadioButtonId == R.id.rb_female) {
                    jk = binding.rbFemale.text.toString()
                }

                val map = hashMapOf(
                    "nama" to binding.edtFullname.text.toString(),
                    "domisili" to binding.edtDomisili.text.toString(),
                    "alamat" to binding.edtAddress.text.toString(),
                    "umur" to binding.edtOld.text.toString(),
                    "jenis_kelamin" to jk,
                    "ttl" to "${binding.edtBirthPlace.text.toString()}, ${binding.edtBirthDate.text.toString()}",
                )

                if (filePath != "") {
                    map["foto"] = filePath
                }

                viewModel.addBioAttr(map)
                findNavController().navigate(R.id.action_page1Fragment_to_page2Fragment)
            } else {
                Log.d("VALIDATE", "FAILED")
            }
        }

        /* populate string array city */
        val cities = resources.getStringArray(R.array.cities)
        // apply to adapter and spinner
        binding.edtDomisili.setAdapter(
                ArrayAdapter(requireContext(), R.layout.simple_item_spinner, cities)
        )

        /* Date Picker settings */
        val builder = MaterialDatePicker.Builder.datePicker()
        val picker = builder.build()
        // Date Picker trigger
        binding.edtBirthDate.setOnClickListener {
            picker.show(requireActivity().supportFragmentManager, picker.toString())
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

    private fun validate(binding: FragmentPage1Binding): Boolean {
        val msg = mutableListOf<String>()
        if (binding.edtFullname.text.toString().isEmpty()) {
            binding.edtFullname.error = "Wajib diisi"
            return false
        }
        if (binding.edtDomisili.text.toString().isEmpty()) {
            binding.edtDomisili.error = "Wajib diisi"
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
            binding.errorMsg.visibility = View.VISIBLE
            binding.errorMsg.text = msg.joinToString("\n")
            return false
        }
        if (File(filePath).length()/1024 > 2048) {
            msg.add("Mohon pilih foto dengan ukuran file yg lebih kecil")
            binding.errorMsg.visibility = View.VISIBLE
            binding.errorMsg.text = msg.joinToString("\n")
            return false
        }
        Log.d("DOMISILI", binding.edtDomisili.text.toString())
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rcChoose && resultCode == Activity.RESULT_OK && data!=null && data.data!=null) {
            val uri = data.data     // ini hasil file pdf yang dipilih

            val path = RealPathUtil.getRealPath(requireContext(), uri)

            Log.d("PATH", path)

            filePath = path

            GlideApp.with(this)
                .asBitmap()
                .transform(CenterCrop())
                .load(data.data)
                .into(binding.ivProfilePic)

        }
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

    private fun hasStoragePermission(): Boolean = EasyPermissions.hasPermissions(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d("PERMISSION_GRANTED", "$perms")
    }

}