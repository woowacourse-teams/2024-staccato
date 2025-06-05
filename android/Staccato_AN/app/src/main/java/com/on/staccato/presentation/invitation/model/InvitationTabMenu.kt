package com.on.staccato.presentation.invitation.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.on.staccato.R

enum class InvitationTabMenu(
    val menuId: Int,
    @StringRes val titleId: Int,
    @DrawableRes val iconResId: Int,
    @StringRes val iconContentDescriptionId: Int,
) {
    RECEIVED_INVITATION(
        menuId = 0,
        titleId = R.string.invitation_management_received,
        iconResId = R.drawable.icon_receive_box,
        iconContentDescriptionId = R.string.invitation_management_received_description,
    ),
    SENT_INVITATION(
        menuId = 1,
        titleId = R.string.invitation_management_sent,
        iconResId = R.drawable.icon_send,
        iconContentDescriptionId = R.string.invitation_management_sent_description,
    ),
}
