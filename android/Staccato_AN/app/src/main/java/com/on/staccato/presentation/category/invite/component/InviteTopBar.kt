package com.on.staccato.presentation.category.invite.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.component.clickableWithoutRipple
import com.on.staccato.theme.Body2
import com.on.staccato.theme.Gray3
import com.on.staccato.theme.StaccatoBlue
import com.on.staccato.theme.Title2
import com.on.staccato.theme.Title3

@Composable
fun InviteTopBar(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    participantsNumber: Int,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 20.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon_close),
            contentDescription = stringResource(id = R.string.all_cancel),
            tint = Gray3,
            modifier =
                Modifier
                    .size(12.dp)
                    .clickableWithoutRipple(
                        onClick = onDismissRequest,
                    )
                    .align(Alignment.CenterStart),
        )
        Text(
            text = stringResource(R.string.invite_member_title),
            style = Title2,
            modifier = Modifier.align(Alignment.Center),
        )
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = participantsNumber.takeIf { 0 < it }?.toString() ?: "",
                style = Title3,
                color = StaccatoBlue,
                modifier = Modifier.padding(end = 4.dp),
            )
            Text(
                text = stringResource(R.string.all_confirm),
                style = Body2,
                modifier =
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismissRequest,
                    ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InviteZeroTopBarPreview() {
    InviteTopBar(
        Modifier,
        {},
        0,
    )
}

@Preview(showBackground = true)
@Composable
fun InviteTopBarPreview() {
    InviteTopBar(
        Modifier,
        {},
        3,
    )
}
