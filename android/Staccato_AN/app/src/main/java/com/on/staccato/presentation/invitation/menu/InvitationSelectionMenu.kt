package com.on.staccato.presentation.invitation.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.invitation.model.InvitationSelectionMenuItems
import com.on.staccato.theme.Gray1
import com.on.staccato.theme.StaccatoBlue

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
        InvitationSelectionMenuItems.entries.forEach { menu ->
            MenuTab(
                menu = menu,
                selected = menu.menuId == selectedMenu.menuId,
                onClick = { onClick(menu) }
            )
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
