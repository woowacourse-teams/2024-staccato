package com.on.staccato.presentation.category.component

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.on.staccato.presentation.category.model.CategoryDialogState.Exit
import com.on.staccato.presentation.category.viewmodel.CategoryViewModel

@Composable
fun ExitDialogScreen(viewModel: CategoryViewModel = hiltViewModel()) {
    when (val dialogState = viewModel.dialogState.value) {
        is Exit -> {
            ExitDialog(
                onDismiss = { viewModel.dismissDialog() },
                onConfirm = dialogState.onConfirm,
            )
        }

        else -> Unit
    }
}
