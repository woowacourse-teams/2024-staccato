package com.on.staccato.presentation.categorycreation.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R

@Composable
fun CategoryInputGuide(
    modifier: Modifier = Modifier,
    @StringRes titleId: Int,
    @StringRes hintId: Int,
    @StringRes warningId: Int,
) {
    Column(
        modifier = modifier.padding(start = 24.dp),
        horizontalAlignment = AbsoluteAlignment.Left,
    ) {
        CategoryInputTitle(id = titleId)
        Spacer(modifier = Modifier.padding(top = 8.dp))
        CategoryInputHint(hintId)
        Spacer(modifier = Modifier.padding(top = 5.dp))
        CategoryInputWarning(warningId)
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryInputGuidePreview() {
    CategoryInputGuide(
        titleId = R.string.category_creation_color,
        hintId = R.string.category_creation_color_hint,
        warningId = R.string.category_creation_share_warning,
    )
}
