package com.on.staccato.presentation.categorycreation.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.theme.Body5
import com.on.staccato.theme.StaccatoBlue

@Composable
fun CategoryInputWarning(
    @StringRes id: Int,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.icon_error),
            contentDescription = stringResource(id = R.string.all_waring),
            tint = StaccatoBlue,
        )
        Text(
            modifier = Modifier.padding(start = 3.5.dp),
            text = stringResource(id = id),
            color = StaccatoBlue,
            style = Body5,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryInputWarningPreview() {
    CategoryInputWarning(id = R.string.category_creation_share_warning)
}
