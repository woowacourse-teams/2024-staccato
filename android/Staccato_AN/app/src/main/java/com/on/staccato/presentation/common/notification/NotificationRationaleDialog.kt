package com.on.staccato.presentation.common.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.on.staccato.R
import com.on.staccato.presentation.invitation.received.component.AcceptButton
import com.on.staccato.presentation.invitation.received.component.RejectButton
import com.on.staccato.theme.Body3
import com.on.staccato.theme.Gray4
import com.on.staccato.theme.Title2
import com.on.staccato.theme.White

@Composable
fun NotificationRationaleDialog(onAccept: () -> Unit) {
    val showDialog = rememberSaveable { mutableStateOf(true) }

    if (showDialog.value) {
        Dialog(
            onDismissRequest = { showDialog.value = false },
            properties =
                DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnClickOutside = false,
                ),
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = White,
                tonalElevation = 10.dp,
                modifier =
                    Modifier.fillMaxWidth(0.85f),
            ) {
                Column(
                    modifier =
                        Modifier
                            .wrapContentHeight()
                            .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(R.string.notification_permission_title),
                        style = Title2,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.notification_permission_rationale),
                        style = Body3,
                        color = Gray4,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.notification_permission_rationale_bullet),
                        style = Body3,
                        color = Gray4,
                    )
                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        RejectButton { showDialog.value = false }
                        Spacer(Modifier.width(10.dp))
                        AcceptButton {
                            onAccept()
                            showDialog.value = false
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF, showSystemUi = true)
@Composable
fun NotificationRationaleDialogPreview() {
    NotificationRationaleDialog {}
}
