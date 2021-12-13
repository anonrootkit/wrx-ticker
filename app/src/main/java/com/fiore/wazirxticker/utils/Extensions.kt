package com.fiore.wazirxticker.utils

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import timber.log.Timber
import java.math.BigDecimal
import java.math.BigInteger

fun Any.timberD(msg: String) {
    Timber.tag(this.javaClass.simpleName).d(msg)
}

fun Fragment.showToast(msg: String, long: Boolean = false) {
    Toast.makeText(requireContext(), msg, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT)
        .show()
}

fun Fragment.loader(msg: String) = getLoaderDialog(requireContext(), msg)

fun View.setText(string: String) {
    when (this) {
        is Button -> this.text = string
        is TextView -> this.text = string
        is EditText -> this.setText(string)
        else -> throw IllegalArgumentException("$this view cannot have text")
    }
}

fun NavController.safeNavigate(direction: NavDirections) {
    currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
}

fun Fragment.safeNavigate(direction: NavDirections) {
    findNavController().safeNavigate(direction)
}

fun String.isValid(): Boolean {
    return validatePrice(this)
}

fun String.bd() = BigDecimal(this)
fun String.bi() = BigInteger(this)

fun BigDecimal.bis() = toBigInteger().toString()
fun BigInteger.bds() = toBigDecimal().toPlainString()

fun Fragment.openUrl(url : String) {
    activity?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}
