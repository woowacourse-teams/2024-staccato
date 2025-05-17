package com.on.staccato.presentation.invitation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.component.clickableWithoutRipple
import com.on.staccato.presentation.invitation.model.InvitationSelectionMenuItems
import com.on.staccato.theme.Gray1
import com.on.staccato.theme.Gray3
import com.on.staccato.theme.StaccatoBlue
import com.on.staccato.theme.Title3
import com.on.staccato.theme.White

private val menus = InvitationSelectionMenuItems.entries

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationSelectionMenu(
    modifier: Modifier = Modifier,
    selectedMenu: InvitationSelectionMenuItems,
    onClick: (InvitationSelectionMenuItems) -> Unit,
) {
    PrimaryTabRow(
        selectedTabIndex = selectedMenu.menuId,
        modifier = modifier
            .background(
                color = Gray1,
                shape = RoundedCornerShape(7.dp),
            )
            .padding(5.dp),
        containerColor = Gray1,
        contentColor = StaccatoBlue,
        indicator = {},
        divider = {},
    ) {
        menus.forEach { menu ->
            MenuTab(
                menu = menu,
                selected = menu.menuId == selectedMenu.menuId,
                onClick = { onClick(menu) }
            )
        }
    }
}

@Composable
private fun MenuTab(
    menu: InvitationSelectionMenuItems,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor = if (selected) White else Gray1
    val contentColor = if (selected) StaccatoBlue else Gray3

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickableWithoutRipple(onClick)
            .background(color = backgroundColor, shape = RoundedCornerShape(5.dp))
            .padding(5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 0.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = menu.title,
                style = Title3,
                color = contentColor,
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Icon(
                imageVector = ImageVector.vectorResource(menu.iconResId),
                tint = contentColor,
                contentDescription = menu.iconContentDescription,
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0L,
)
@Composable
private fun CustomTapPreview() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        menus.forEach { menu ->
            MenuTab(
                menu = menu,
                selected = false,
            ) {
            }
        }
        menus.forEach { menu ->
            MenuTab(
                menu = menu,
                selected = true,
            ) {
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
)
@Composable
private fun InvitationSelectionMenuPreview() {
    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        InvitationSelectionMenu(
            selectedMenu = InvitationSelectionMenuItems.SENT_INVITATION,
        ) {}
        Box(modifier = Modifier.height(10.dp))
        InvitationSelectionMenu(
            selectedMenu = InvitationSelectionMenuItems.RECEIVED_INVITATION,
        ) {}
    }
}
