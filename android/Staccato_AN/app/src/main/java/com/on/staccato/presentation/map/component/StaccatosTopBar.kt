package com.on.staccato.presentation.map.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.on.staccato.presentation.component.clickableWithoutRipple
import com.on.staccato.theme.Gray3
import com.on.staccato.theme.Title2

@Composable
fun StaccatosTopBar(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 16.dp, vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.icon_close),
            contentDescription = stringResource(id = R.string.all_cancel),
            modifier =
                Modifier.size(12.dp)
                    .align(Alignment.CenterStart)
                    .clickableWithoutRipple(onClick = onDismissRequest),
            tint = Gray3,
        )
        Text(text = stringResource(id = R.string.category_staccatos), style = Title2)
    }
}

@Preview(showBackground = true)
@Composable
private fun StaccatosDialogTopBarPreview() {
    StaccatosTopBar(onDismissRequest = {})
}
