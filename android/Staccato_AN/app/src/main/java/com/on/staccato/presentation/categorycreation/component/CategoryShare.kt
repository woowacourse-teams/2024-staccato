package com.on.staccato.presentation.categorycreation.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.component.DefaultSwitch

@Composable
fun CategoryShare(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .padding(top = 24.dp, end = 20.dp)
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CategoryInputGuide(
            titleId = R.string.category_creation_share_title,
            hintId = R.string.category_creation_share_hint,
            warningId = R.string.category_creation_share_warning,
        )
        Spacer(modifier = Modifier.weight(1f))
        DefaultSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategorySharePreview(
    @PreviewParameter(CategorySharePreviewParameterProvider::class)
    checked: Boolean,
) {
    CategoryShare(
        checked = checked,
        onCheckedChange = {},
    )
}

class CategorySharePreviewParameterProvider : PreviewParameterProvider<Boolean> {
    override val values =
        sequenceOf(
            false,
            true,
        )
}
