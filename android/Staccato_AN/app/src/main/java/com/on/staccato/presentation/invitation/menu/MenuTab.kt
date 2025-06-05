package com.on.staccato.presentation.invitation.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.on.staccato.presentation.component.clickableWithoutRipple
import com.on.staccato.presentation.invitation.model.InvitationTabMenu
import com.on.staccato.theme.Gray1
import com.on.staccato.theme.Gray3
import com.on.staccato.theme.StaccatoBlue
import com.on.staccato.theme.Title3
import com.on.staccato.theme.White

@Composable
fun MenuTab(
    menu: InvitationTabMenu,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor = if (selected) White else Gray1
    val contentColor = if (selected) StaccatoBlue else Gray3

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickableWithoutRipple(onClick)
                .background(color = backgroundColor, shape = RoundedCornerShape(5.dp))
                .padding(5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(menu.titleId),
                style = Title3,
                color = contentColor,
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Icon(
                imageVector = ImageVector.vectorResource(menu.iconResId),
                tint = contentColor,
                contentDescription = stringResource(menu.iconContentDescriptionId),
            )
        }
    }
}

@Preview
@Composable
private fun MenuTapPreview() {
    Column(modifier = Modifier.fillMaxWidth()) {
        InvitationTabMenu.entries.forEach { menu ->
            MenuTab(
                menu = menu,
                selected = false,
                onClick = {},
            )
        }
        InvitationTabMenu.entries.forEach { menu ->
            MenuTab(
                menu = menu,
                selected = true,
                onClick = {},
            )
        }
    }
}
