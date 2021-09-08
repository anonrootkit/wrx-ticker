package com.fiore.wazirxticker.utils

import android.view.View
import androidx.databinding.BindingAdapter


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