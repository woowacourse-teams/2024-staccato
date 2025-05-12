package com.on.staccato.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.theme.Gray2
import com.on.staccato.theme.StaccatoBlue70
import com.on.staccato.theme.White

@Composable
fun CustomSwitchComponent(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    checkedTrackColor: Color = StaccatoBlue70,
    uncheckedTrackColor: Color = Gray2,
    thumbColor: Color = White,
    width: Dp = 43.dp,
    height: Dp = 23.dp,
    thumbSize: Dp = 19.dp,
    onCheckedChange: (Boolean) -> Unit,
) {
    val trackColor by animateColorAsState(
        if (checked) checkedTrackColor else uncheckedTrackColor,
        label = stringResource(id = R.string.label_custom_switch_track_color),
    )
    val padding = 2.dp
    val thumbPosition by animateDpAsState(
        if (checked) width - thumbSize - padding else padding,
        label = stringResource(id = R.string.label_custom_switch_thumb_position),
    )

    Box(
        modifier =
            modifier
                .size(width, height)
                .clip(RoundedCornerShape(50))
                .background(trackColor)
                .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier =
                Modifier
                    .padding(start = thumbPosition)
                    .size(thumbSize)
                    .background(thumbColor, CircleShape),
        )
    }
}

@Preview
@Composable
private fun CustomSwitchPreview(
    @PreviewParameter(SwitchPreviewParameterProvider::class)
    checked: Boolean,
) {
    CustomSwitchComponent(
        checked = checked,
    ) {}
}

class SwitchPreviewParameterProvider : PreviewParameterProvider<Boolean> {
    override val values =
        sequenceOf(
            false,
            true,
        )
}
