package com.on.staccato.presentation.invitation.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.on.staccato.R

enum class InvitationSelectionMenuUiModel(
    val menuId: Int,
    @StringRes val titleId: Int,
    @DrawableRes val iconResId: Int,
    @StringRes val iconContentDescriptionId: Int,
) {
    RECEIVED_INVITATION(0, R.string.invitation_management_received, R.drawable.icon_receive_box, R.string.invitation_management_received_description),
    SENT_INVITATION(1, R.string.invitation_management_sent, R.drawable.icon_send, R.string.invitation_management_sent_description),
}
