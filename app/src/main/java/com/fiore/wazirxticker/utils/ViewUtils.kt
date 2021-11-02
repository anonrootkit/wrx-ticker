@file:Suppress("SpellCheckingInspection")

package com.fiore.wazirxticker.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.fiore.wazirxticker.databinding.LayoutLoaderDialogBinding
import com.google.android.material.snackbar.Snackbar

fun getLoaderDialog(context: Context, msg : String) : AlertDialog {
    val loaderView = LayoutLoaderDialogBinding.inflate(LayoutInflater.from(context))
    loaderView.dialogLoadingMessage.text = msg
    val dialog = AlertDialog.Builder(context).create()
    dialog.setView(loaderView.root)
    dialog.setCancelable(false)
    return dialog
}

fun changeAppTheme(theme: String) {

    AppCompatDelegate.setDefaultNightMode(
        when (theme) {
            ThemeConstants.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeConstants.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> throw IllegalArgumentException("Invalid theme selected $theme")
        }
    )
}

data class SnackbarAction(
    @StringRes val actionTitle: Int,
    val actionToPerform: () -> Unit
)
