package com.on.staccato.presentation.map.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.component.DefaultFloatingActionButton
import com.on.staccato.theme.StaccatoBlue
import com.on.staccato.theme.White

@Composable
fun MyLocationFloatingActionButton(onClick: () -> Unit) {
    DefaultFloatingActionButton(
        modifier =
            Modifier
                .padding(10.dp)
                .size(36.dp),
        onClick = onClick,
        containerColor = White,
        contentColor = StaccatoBlue,
        iconId = R.drawable.icon_my_location,
        iconContentDescriptionId = R.string.maps_my_location_btn_content_description,
    )
}

@Preview
@Composable
fun MyLocationFloatingActionButtonPreview() {
    MyLocationFloatingActionButton(
        onClick = {},
    )
}
