package id.trydev.alumnifstku.ui.loker.detail

import android.app.Dialog
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.FragmentDetailLokerBinding
import id.trydev.alumnifstku.model.Loker
import id.trydev.alumnifstku.utils.GlideApp
import java.text.SimpleDateFormat
import java.util.*

class DetailFragmentLoker(private val item:Loker): BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDetailLokerBinding
    private lateinit var mBehavior: BottomSheetBehavior<View>


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        binding = FragmentDetailLokerBinding.inflate(layoutInflater)

        Log.d("LOKER", "$item")

        binding.toolbar.title = item.jabatan
        binding.toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_round_close_24)
        binding.toolbar.setNavigationOnClickListener {
            bottomSheetDialog.dismiss()
        }

        if (item.poster != null) {
            GlideApp.with(requireContext())
                    .asBitmap()
                    .centerInside()
                    .load(item.poster)
                    .into(binding.ivDetailLoker)
        } else {
            binding.ivDetailLoker.setImageResource(R.color.grey)
        }

        if (item.updatedAt != null) {
            val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale("in", "ID"))
            val strToDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("in", "ID"))
                    .parse(item.updatedAt.toString())
            binding.tvDetailLokerUpdate.text = formatter.format(strToDate)
        } else {
            binding.tvDetailLokerUpdate.visibility = View.GONE
        }

        binding.tvDetailLokerPosition.text = item.jabatan
        binding.tvDetailLokerCompany.text = item.perusahaan

        if (item.deskripsi != null) {
            binding.tvDetailDescription.text = item.deskripsi
        } else {
            binding.tvDetailDescription.visibility = View.GONE
        }

        if (item.cluster != null) {
            binding.tvDetailCluster.text = item.cluster
        } else {
            binding.tvDetailCluster.visibility = View.GONE
        }

        if (item.jurusan != null) {
            binding.tvDetailMajor.text = item.jurusan
        } else {
            binding.tvDetailMajor.visibility = View.GONE
        }

        if (item.link != null) {
            binding.tvDetailLink.text = item.link
        } else {
            binding.tvDetailLink.visibility = View.GONE
        }

        binding.tvDetailLink.setOnClickListener {
            val builder = CustomTabsIntent.Builder()
            val customTab = builder.build()
            customTab.launchUrl(requireContext(), Uri.parse(item.link))
        }

        bottomSheetDialog.setContentView(binding.root)
        mBehavior = BottomSheetBehavior.from(binding.root.parent as View)
        mBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(p0: View, p1: Float) {

            }

            override fun onStateChanged(p0: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

        })
        return bottomSheetDialog
    }

    override fun onStart() {
        super.onStart()
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        mBehavior.isHideable = false
    }

    private fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

}