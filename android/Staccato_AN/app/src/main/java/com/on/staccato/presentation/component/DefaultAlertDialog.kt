package com.on.staccato.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.on.staccato.theme.Body2
import com.on.staccato.theme.Gray1
import com.on.staccato.theme.Gray3
import com.on.staccato.theme.Gray4
import com.on.staccato.theme.StaccatoBlack
import com.on.staccato.theme.StaccatoBlue
import com.on.staccato.theme.Title2
import com.on.staccato.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultAlertDialog(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    properties: DialogProperties = DialogProperties(),
) {
    BasicAlertDialog(
        modifier = modifier.fillMaxWidth(),
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(10.dp),
            color = White,
        ) {
            Column(
                modifier = modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = title,
                    style = Title2,
                    color = StaccatoBlack,
                )
                if (description != null) {
                    Text(
                        text = description,
                        style = Body2,
                        color = Gray3,
                    )
                }
                Spacer(modifier = modifier.height(36.dp))
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    dismissButton?.invoke()
                    Spacer(modifier = modifier.width(6.dp))
                    confirmButton()
                }
            }
        }
    }
}

@Composable
@Preview
private fun DefaultAlertDialogPreview() {
    DefaultAlertDialog(
        title = "제목제목제목",
        description = "내용내용내용.\n내용내용, 내용내용내용??",
        onDismissRequest = {},
        confirmButton = {
            DefaultTextButton(
                text = "확인",
                onClick = {},
                backgroundColor = StaccatoBlue,
                textColor = White,
            )
        },
        dismissButton = {
            DefaultTextButton(
                text = "취소",
                onClick = {},
                backgroundColor = Gray1,
                textColor = Gray4,
            )
        },
    )
}
