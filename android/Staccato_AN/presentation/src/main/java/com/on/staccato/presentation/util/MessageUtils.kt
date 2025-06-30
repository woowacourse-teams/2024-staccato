package com.on.staccato.presentation.util

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun View.showSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun View.showSnackBarWithAction(
    message: String,
    @StringRes actionLabel: Int,
    onAction: () -> Unit,
    length: Int,
) {
    val snackBar = getSnackBarWithAction(message, actionLabel, onAction, length)
    snackBar.show()
}

fun View.getSnackBarWithAction(
    message: String,
    @StringRes actionLabel: Int,
    onAction: () -> Unit,
    length: Int,
): Snackbar =
    Snackbar.make(this, message, length).setAction(actionLabel) {
        onAction()
    }
