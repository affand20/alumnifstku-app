package id.trydev.alumnifstku.ui.memory.mypost

import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.adapter.SharingMemoryAdapter
import id.trydev.alumnifstku.databinding.ActivityMyPostBinding
import id.trydev.alumnifstku.network.RequestState
import id.trydev.alumnifstku.prefs.AppPreferences
import id.trydev.alumnifstku.ui.biodata.pages.Page1Fragment
import id.trydev.alumnifstku.ui.memory.bottomdialog.create.CreatePostFragment
import id.trydev.alumnifstku.ui.memory.mypost.MyPostViewModel
import id.trydev.alumnifstku.ui.memory.bottomdialog.detail.DetailFragmentPost
import id.trydev.alumnifstku.utils.GlideApp
import id.trydev.alumnifstku.utils.ItemDecorationPost
import id.trydev.alumnifstku.utils.RealPathUtil
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class MyPostActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: ActivityMyPostBinding
    private lateinit var prefs: AppPreferences
    private lateinit var adapter: SharingMemoryAdapter
    private lateinit var viewModel: MyPostViewModel

    companion object {
        const val rcStorage = 101
        const val rcChoose = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_post)

        binding = ActivityMyPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences(this)

        adapter = SharingMemoryAdapter(this) { post ->
            /* Navigate to Detail Loker activity */
            val detailFragment = DetailFragmentPost(post.id.toString().toInt())
            Log.d("POST ID", post.id.toString())
            detailFragment.show(supportFragmentManager, detailFragment.tag)
        }

        binding.rvPost.layoutManager = LinearLayoutManager(this)
        binding.rvPost.adapter = adapter
        binding.rvPost.addItemDecoration(ItemDecorationPost(32))

        viewModel = ViewModelProvider(this).get(MyPostViewModel::class.java)

        viewModel.getMyPosts(prefs.token.toString())
        /* Observe Request state changes */
        viewModel.state.observe({ lifecycle }, { state ->
            Log.d("OBSERVE", "state $state")
            when(state) {
                RequestState.REQUEST_START -> {
                    /* Show progress bar */
                    binding.swipeRefresh.isRefreshing = true
                    binding.stateEmpty.visibility = View.GONE
                }
                RequestState.REQUEST_END -> {
                    /* Hide progress bar and fetch response */
                    binding.swipeRefresh.isRefreshing = false
                }
                RequestState.REQUEST_ERROR -> {
                    /* Something happen.. Hide progress bar and fetch errors */
                    binding.swipeRefresh.isRefreshing = false
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
                    // populate data
                    response.data?.let { adapter.populateData(it) }
                } else {
                    binding.stateEmpty.visibility = View.VISIBLE
                    binding.stateEmpty.text = response.message
                }
            }
        })

        /* Observe another Error possibility changes */
        viewModel.error.observe({ lifecycle }, { error ->
            if (error.isNotEmpty()) {
                binding.stateEmpty.visibility = View.VISIBLE
                binding.stateEmpty.text = error
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getMyPosts(prefs.token.toString())
        }

        binding.fabPost.setOnClickListener{
            storageTask()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Page1Fragment.rcChoose && resultCode == Activity.RESULT_OK && data!=null && data.data!=null) {
            val uri = data.data     // ini hasil file pdf yang dipilih
            val file = File(RealPathUtil.getRealPath(this, uri))
            if (file.length()/1024 < 5000) {
                /* Navigate to Create Post activity */
                uri?.let {
                    val detailFragment = CreatePostFragment(it)
                    detailFragment.show(supportFragmentManager, detailFragment.tag)
                }
            } else {
                Toast.makeText(this, "Ukuran file terlalu besar, mohon pilih file dibawah 5MB", Toast.LENGTH_LONG).show()
            }

//            GlideApp.with(this)
//                .asBitmap()
//                .transform(CenterCrop())
//                .load(data.data)
//                .into(binding.ivProfilePic)

        }
    }

    @AfterPermissionGranted(rcStorage)
    private fun storageTask() {
        if (hasStoragePermission()) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            startActivityForResult(
                Intent.createChooser(intent, "Pilih File"),
                Page1Fragment.rcChoose
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Permission",
                Page1Fragment.rcStorage,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
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

    override fun onPause() {
        super.onPause()
        Log.d("ONPAUSE", "Triggered")
    }

    override fun onResume() {
        super.onResume()
        Log.d("ONRESUME", "Triggered")
        viewModel.getMyPosts(prefs.token.toString())
    }
}