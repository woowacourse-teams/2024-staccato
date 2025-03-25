package com.on.staccato.presentation.mapper

import android.content.Context
import com.on.staccato.R
import com.on.staccato.domain.model.NicknameState
import com.on.staccato.presentation.common.InputState

fun NicknameState.toInputState(context: Context): InputState {
    return when (this) {
        NicknameState.Empty -> InputState.Empty

        NicknameState.BlankFirst -> InputState.Invalid(context.getString(R.string.login_nickname_error_message_blank_first))

        NicknameState.InvalidFormat -> InputState.Invalid(context.getString(R.string.login_nickname_error_message_format))

        is NicknameState.InvalidLength ->
            InputState.Invalid(
                context.getString(R.string.login_nickname_error_message_length).format(min, max),
            )

        is NicknameState.Valid -> InputState.Valid(context.getString(R.string.login_nickname_valid_message))
    }
}
