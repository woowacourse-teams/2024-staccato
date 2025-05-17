package com.on.staccato.presentation.categorycreation.component

import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.on.staccato.R
import com.on.staccato.theme.Body4
import com.on.staccato.theme.Gray3

@Composable
fun CategoryInputHint(
    @StringRes id: Int,
) {
    Text(
        text = stringResource(id = id),
        color = Gray3,
        style = Body4,
    )
}

@Preview(showBackground = true)
@Composable
private fun CategoryInputHintPreview() {
    CategoryInputTitle(R.string.category_creation_title_hint)
}
