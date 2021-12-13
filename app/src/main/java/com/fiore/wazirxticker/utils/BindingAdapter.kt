package com.fiore.wazirxticker.utils

import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.fiore.wazirxticker.R
import com.fiore.wazirxticker.data.database.entities.Investment


@BindingAdapter("unformattedText", "textFormat")
fun formattedString(view: View, text: Any?, format: String?) {
    text?.let {
        when (text) {
            is String -> {
                if (format != null)
                    view.setText(String.format(format, text))
                else
                    view.setText(text)
            }
            is Int, is Long -> {
                if (format != null)
                    view.setText(String.format(format, text))
                else
                    view.setText(text.toString())
            }
            else -> throw IllegalArgumentException("Invalid unformatted text")
        }
    }
}

@BindingAdapter("profit_color")
fun investmentProfitColor(view: TextView, investment: Investment?) {
    investment?.let {
        val hasProfit = investment.profitAmount.toBigInteger() >= investment.buyAmount.toBigInteger()
        val color = ResourcesCompat.getColor(
            view.resources,
            if (hasProfit) R.color.md_green_500 else R.color.md_red_500,
            null
        )
        view.setTextColor(color)
    }
}