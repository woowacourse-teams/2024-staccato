package com.on.staccato.presentation.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.on.staccato.R
import com.on.staccato.theme.StaccatoBlue
import com.on.staccato.theme.White

@Composable
fun DefaultFloatingActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    shape: Shape = CircleShape,
    containerColor: Color,
    contentColor: Color,
    @DrawableRes iconId: Int,
    @StringRes iconContentDescriptionId: Int,
) {
    SmallFloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        containerColor = containerColor,
        shape = shape,
        contentColor = contentColor,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = iconId),
            contentDescription = stringResource(id = iconContentDescriptionId),
        )
    }
}

@Preview
@Composable
fun DefaultFloatingActionButtonPreview() {
    DefaultFloatingActionButton(
        onClick = {},
        containerColor = StaccatoBlue,
        contentColor = White,
        iconId = R.drawable.icon_plus,
        iconContentDescriptionId = R.string.staccato_add,
    )
}
