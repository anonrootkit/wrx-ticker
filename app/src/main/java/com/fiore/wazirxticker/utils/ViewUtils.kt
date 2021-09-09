package com.fiore.wazirxticker.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.fiore.wazirxticker.databinding.LayoutLoaderDialogBinding

fun getLoaderDialog(context: Context, msg : String) : AlertDialog {
    val loaderView = LayoutLoaderDialogBinding.inflate(LayoutInflater.from(context))
    loaderView.dialogLoadingMessage.text = msg
    val dialog = AlertDialog.Builder(context).create()
    dialog.setView(loaderView.root)
    dialog.setCancelable(false)
    return dialog
}