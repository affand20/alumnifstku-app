package id.trydev.alumnifstku.ui.pengaturan.bottomdialog

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.trydev.alumnifstku.R
import id.trydev.alumnifstku.databinding.FragmentPasswordBottomBinding


class PasswordFragmentBottom : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentPasswordBottomBinding
    private lateinit var mBehavior: BottomSheetBehavior<View>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        binding = FragmentPasswordBottomBinding.inflate(layoutInflater)

        // mengatur tulisan2 didalamnya dan action2 nya
        binding.toolbar.title = "Ganti Password"
        binding.toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_round_close_24)
        binding.toolbar.setNavigationOnClickListener {
            bottomSheetDialog.dismiss()
        }

        // tombol untuk menutup dialog
        binding.resetButtonGantiPassword.setOnClickListener {
            bottomSheetDialog.dismiss()
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