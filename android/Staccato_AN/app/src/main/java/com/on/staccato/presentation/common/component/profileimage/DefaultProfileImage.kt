package com.on.staccato.presentation.common.component.profileimage

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.theme.Gray2

@Composable
fun DefaultProfileImage(
    size: Dp,
    borderWidth: Dp,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id = R.drawable.icon_member),
        contentDescription = stringResource(id = R.string.default_profile_image_description),
        modifier =
            modifier
                .size(size)
                .border(
                    color = Gray2,
                    shape = CircleShape,
                    width = borderWidth,
                ),
    )
}

@Composable
@Preview(showBackground = true)
fun DefaultProfilePreview_30dp() {
    DefaultProfileImage(30.dp, 0.2.dp)
}

@Composable
@Preview(showBackground = true)
fun DefaultProfilePreview_60dp() {
    DefaultProfileImage(60.dp, 0.3.dp)
}
