package com.fiore.wazirxticker.ui.home.drawer.history.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.fiore.wazirxticker.R
import com.fiore.wazirxticker.data.database.entities.History

class HistorySwipeListener(
    private val context: Context,
    private val historyAdapter: HistoryAdapter,
    private val deleteHistory : (History) -> Unit
) : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val deleteIcon by lazy {
        ResourcesCompat.getDrawable(context.resources, R.drawable.ic_delete, null)!!
    }

    private val redBackground by lazy {
        ColorDrawable(Color.RED)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val historyItem : History = historyAdapter.currentList[viewHolder.adapterPosition]
        deleteHistory(historyItem)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView

        val margin = (itemView.height - deleteIcon.intrinsicHeight) / 2
        val iconTop = margin + itemView.top
        val iconBottom = iconTop + deleteIcon.intrinsicHeight

        when{
            //Swiping to right
            dX > 0 -> {
                val iconLeft = deleteIcon.intrinsicWidth + itemView.left
                val iconRight = iconLeft + deleteIcon.intrinsicWidth

                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                redBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
            }

            //Swiping to left
            dX < 0 -> {
                val iconRight = itemView.right - deleteIcon.intrinsicWidth
                val iconLeft = iconRight - deleteIcon.intrinsicWidth

                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                redBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right , itemView.bottom)
            }

            else -> {
                deleteIcon.setBounds(0,0,0,0)
                redBackground.setBounds(0,0,0,0)
            }
        }

        redBackground.draw(c)
        deleteIcon.draw(c)
    }
}