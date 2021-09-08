package com.fiore.wazirxticker.utils

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

//
//fun NavController.safeNavigate(
//    @IdRes currentDestinationId: Int,
//    @IdRes id: Int,
//    args: Bundle? = null
//) {
//    if (currentDestinationId == currentDestination?.id) {
//        navigate(id, args)
//    }
//}
//
//fun NavController.safeNavigate(
//    @IdRes resId: Int,
//    args: Bundle? = null,
//    navOptions: NavOptions? = null,
//    navExtras: Navigator.Extras? = null
//) {
//    val action = currentDestination?.getAction(resId) ?: graph.getAction(resId)
//    if (action != null && currentDestination?.id != action.destinationId) {
//        navigate(resId, args, navOptions, navExtras)
//    }
//}

fun Fragment.safeNavigate(direction: NavDirections) {
    findNavController().safeNavigate(direction)
}