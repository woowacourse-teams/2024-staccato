package com.on.staccato.presentation.category.invite.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.dummyMember
import com.on.staccato.presentation.category.invite.model.InviteState
import com.on.staccato.presentation.category.invite.model.MemberUiModel
import com.on.staccato.presentation.category.invite.model.participatingMemberUiModel
import com.on.staccato.presentation.category.invite.model.selectedMemberUiModel
import com.on.staccato.presentation.category.invite.model.toUiModel
import com.on.staccato.presentation.component.clickableWithoutRipple

@Composable
fun InviteStateComposable(
    item: MemberUiModel,
    onDeselect: (Member) -> Unit,
    onSelect: (Member) -> Unit,
) {
    when (item.inviteState) {
        InviteState.UNSELECTED -> {
            Image(
                painter = painterResource(id = R.drawable.icon_plus_circle),
                contentDescription = stringResource(id = R.string.invite_member_unselected_description),
                modifier =
                    Modifier
                        .size(27.dp)
                        .clickableWithoutRipple { onSelect(item.member) },
            )
        }

        InviteState.SELECTED -> {
            Image(
                painter = painterResource(id = R.drawable.icon_v_circle),
                contentDescription = stringResource(id = R.string.invite_member_selected_description),
                modifier =
                    Modifier
                        .size(27.dp)
                        .clickableWithoutRipple { onDeselect(item.member) },
            )
        }

        InviteState.PARTICIPATING -> {
            Text(text = stringResource(R.string.invite_member_participating_description))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UnselectedComposablePreview() {
    InviteStateComposable(
        item = dummyMember.toUiModel(),
        onDeselect = {},
        onSelect = {},
    )
}

@Preview(showBackground = true)
@Composable
fun SelectedComposablePreview() {
    InviteStateComposable(
        item = selectedMemberUiModel,
        onDeselect = {},
        onSelect = {},
    )
}

@Preview(showBackground = true)
@Composable
fun ParticipatingComposablePreview() {
    InviteStateComposable(
        item = participatingMemberUiModel,
        onDeselect = {},
        onSelect = {},
    )
}
