package id.trydev.alumnifstku.ui.biodata.pages

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
import androidx.lifecycle.ViewModelProvider
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.FragmentPage1Binding
import id.trydev.alumnifstku.databinding.FragmentPage2Binding
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.ui.biodata.BiodataActivity
import id.trydev.alumnifstku.ui.biodata.BiodataViewModel
import id.trydev.alumnifstku.ui.dashboard.DashboardActivity

class Page2Fragment : Fragment() {

    private val viewModel: BiodataViewModel by activityViewModels()     // need declared like this for shared viewmodel
    private lateinit var binding: FragmentPage2Binding
    private lateinit var prefs: AppPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPage2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = AppPreferences(requireContext())

        /* Populate spinner data */
        val majors = resources.getStringArray(R.array.major)
        val clusters = resources.getStringArray(R.array.cluster)
        // apply to adapter and spinner
        binding.edtMajor.setAdapter(
            ArrayAdapter(requireContext(), R.layout.simple_item_spinner, majors)
        )
        binding.edtCluster.setAdapter(
            ArrayAdapter(requireContext(), R.layout.simple_item_spinner, clusters)
        )

        /* Submit action */
        binding.btnSave.setOnClickListener {
            if (validate(binding)) {
                val map = hashMapOf(
                    "jurusan" to binding.edtMajor.text.toString(),
                    "angkatan" to binding.edtAngkatan.text.toString(),
                    "company" to binding.edtCompany.text.toString(),
                    "year_joined" to binding.edtYearJoined.text.toString(),
                    "cluster" to binding.edtCluster.text.toString(),
                    "position" to binding.edtPosition.text.toString(),
                )

                if (binding.edtLinkedin.text.toString().isNotEmpty()) {
                    map["linkedin"] = binding.edtLinkedin.text.toString()
                }

                Log.d("ON SUBMIT", "VALIDATED")
                viewModel.addBioAttr(map)
                viewModel.uploadBioWithTracing(prefs.token.toString())
            }
        }


        /* Observe Request state changes */
        viewModel.state.observe({ lifecycle }, { state ->
            Log.d("OBSERVE", "state $state")
            when(state) {
                RequestState.REQUEST_START -> {
                    /* Show progress bar */
                    binding.progressBar.visibility = View.VISIBLE
                    binding.errorMsg.visibility = View.GONE
                    binding.btnSave.visibility = View.GONE
                }
                RequestState.REQUEST_END -> {
                    /* Hide progress bar and fetch response */
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.visibility = View.VISIBLE
                }
                RequestState.REQUEST_ERROR -> {
                    /* Something happen.. Hide progress bar and fetch errors */
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.visibility = View.VISIBLE
                }
                else -> { /* do nothing */ }
            }
        })

        /* Observe Response changes */
        viewModel.response.observe({ lifecycle }, { response ->
            Log.d("RESPONSE", response.toString())
            if (response != null) {
                // check server response
                // if success, navigate to dashboard page
                // else, show error Toast
                if (response.success == true) {
                    // send greeting message
                    Toast.makeText(requireContext(), "Selamat datang, ${response.data?.nama}!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(requireContext(), DashboardActivity::class.java))
                    requireActivity().finish()
                } else {
                    binding.errorMsg.visibility = View.VISIBLE
                    binding.errorMsg.text = response.message
                }
            }
        })

        /* Observe another Error possibility changes */
        viewModel.error.observe({ lifecycle }, { error ->
            if (error.isNotEmpty()) {
                binding.errorMsg.visibility = View.VISIBLE
                binding.errorMsg.text = error
            }
        })

    }

    private fun validate(binding: FragmentPage2Binding): Boolean {
        if (binding.edtMajor.text.toString().isEmpty()) {
            binding.edtMajor.error = "Wajib diisi"
            return false
        }
        if (binding.edtAngkatan.text.toString().isEmpty()) {
            binding.edtAngkatan.error = "Wajib diisi"
            return false
        }
        if (binding.edtCompany.text.toString().isEmpty()) {
            binding.edtCompany.error = "Wajib diisi"
            return false
        }
        if (binding.edtCluster.text.toString().isEmpty()) {
            binding.edtCluster.error = "Wajib diisi"
            return false
        }
        if (binding.edtPosition.text.toString().isEmpty()) {
            binding.edtPosition.error = "Wajib diisi"
            return false
        }
        if (binding.edtYearJoined.text.toString().isEmpty()) {
            binding.edtYearJoined.error = "Wajib diisi"
            return false
        }

        return true
    }

}