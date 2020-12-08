package id.trydev.alumnifstku.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecorationPost(private val space: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = space
            }
            bottom = if (parent.getChildAdapterPosition(view) == state.itemCount-1) {
                64
            } else {
                space
            }
        }
    }
}