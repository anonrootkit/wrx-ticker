package com.fiore.wazirxticker.ui.home.investments.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.fiore.wazirxticker.R
import com.fiore.wazirxticker.data.database.entities.History
import com.fiore.wazirxticker.data.database.entities.Investment

class InvestmentSwipeListener(
    private val context: Context,
    private val investmentsAdapter: InvestmentsAdapter,
    private val deleteInvestment : (Investment) -> Unit,
    private val moveInvestmentToHistory : (Investment) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val deleteIcon : Drawable by lazy {
        ContextCompat.getDrawable(context, R.drawable.ic_delete)!!
    }

    private val sellIcon : Drawable by lazy {
        ContextCompat.getDrawable(context, R.drawable.ic_sell)!!
    }

    private val redBackground by lazy {
        ColorDrawable(Color.RED)
    }

    private val yellowBackground by lazy {
        ColorDrawable(ResourcesCompat.getColor(context.resources, R.color.md_yellow_A700, null))
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val investment = investmentsAdapter.currentList[viewHolder.adapterPosition]
        if (direction == ItemTouchHelper.LEFT) {
            moveInvestmentToHistory(investment)
        }else{
            deleteInvestment(investment)
        }
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
        val backgroundCornerOffset = 20


        when {
            dX > 0 -> { // Swiping to the right
                val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
                val iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
                val iconBottom = iconTop + deleteIcon.intrinsicHeight

                val iconLeft = itemView.left + iconMargin
                val iconRight = itemView.left + iconMargin + deleteIcon.intrinsicWidth
                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                redBackground.setBounds(
                    itemView.left, itemView.top,
                    itemView.left + dX.toInt() + backgroundCornerOffset,
                    itemView.bottom
                )
            }
            dX < 0 -> { // Swiping to the left
                val iconMargin = (itemView.height - sellIcon.intrinsicHeight) / 2
                val iconTop = itemView.top + (itemView.height - sellIcon.intrinsicHeight) / 2
                val iconBottom = iconTop + sellIcon.intrinsicHeight

                val iconLeft = itemView.right - iconMargin - sellIcon.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                sellIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                yellowBackground.setBounds(
                    itemView.right + dX.toInt() - backgroundCornerOffset,
                    itemView.top, itemView.right, itemView.bottom
                )
            }
            else -> { // view is unSwiped
                redBackground.setBounds(0, 0, 0, 0)
                yellowBackground.setBounds(0, 0, 0, 0)
                deleteIcon.setBounds(0, 0,0 ,0)
                sellIcon.setBounds(0, 0,0 ,0)
            }
        }

        redBackground.draw(c)
        deleteIcon.draw(c)
        yellowBackground.draw(c)
        sellIcon.draw(c)
    }


}
