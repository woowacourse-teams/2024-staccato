package com.on.staccato.presentation.invitation.model

import androidx.annotation.DrawableRes
import com.on.staccato.R

enum class InvitationSelectionMenuItems(
    val menuId: Int,
    val title: String,
    @DrawableRes val iconResId: Int,
    val iconContentDescription: String,
) {
    RECEIVED_INVITATION(0, "받은 초대", R.drawable.icon_receive_box, "Received Icon"),
    SENT_INVITATION(1, "보낸 초대", R.drawable.icon_send, "Sent Icon"),
}
