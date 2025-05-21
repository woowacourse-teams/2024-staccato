package com.on.staccato.presentation.timeline.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.theme.Body4
import com.on.staccato.theme.Gray3

@Composable
fun StaccatoCount(
    modifier: Modifier = Modifier,
    count: Int = 0,
) {
    Row(
        modifier = modifier.padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.icon_marker),
            contentDescription = "Marker Icon",
            tint = Color.Unspecified,
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = count.toString(),
            color = Gray3,
            style = Body4,
        )
    }
}

@Preview
@Composable
private fun StaccatoCountPreview(
    @PreviewParameter(StaccatoCountPreviewParameterProvider::class)
    count: Int,
) {
    StaccatoCount(count = count)
}

private class StaccatoCountPreviewParameterProvider() : PreviewParameterProvider<Int> {
    override val values: Sequence<Int>
        get() =
            sequenceOf(
                0,
                21,
                500,
                1000,
            )
}
