package com.on.staccato.presentation.categorycreation.component

import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.on.staccato.R
import com.on.staccato.theme.StaccatoBlack
import com.on.staccato.theme.Title2

@Composable
fun CategoryInputTitle(
    @StringRes id: Int,
) {
    Text(
        text = stringResource(id),
        color = StaccatoBlack,
        style = Title2,
    )
}

@Preview(showBackground = true)
@Composable
private fun CategoryInputTitlePreview() {
    CategoryInputTitle(R.string.category_creation_title)
}
