package com.on.staccato.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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

@Composable
fun DefaultAlertDialog(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = {
        DefaultTextButton(
            text = "취소",
            onClick = onDismissRequest,
            backgroundColor = Gray1,
            textColor = Gray4,
        )
    },
    properties: DialogProperties = DialogProperties(),
) {
    AlertDialog(
        modifier = modifier.fillMaxWidth(),
        title = {
            Text(
                text = title,
                style = Title2,
                color = StaccatoBlack,
            )
        },
        text = {
            Text(
                text = description,
                style = Body2,
                color = Gray3,
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        shape = RoundedCornerShape(10.dp),
        containerColor = White,
        properties = properties,
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xDDDDDDL)
private fun DefaultAlertDialogPreview() {
    DefaultAlertDialog(
        title = "제목제목제목",
        description = "내용내용내용.\n내용내용, 내용내용내용??",
        confirmButton = {
            DefaultTextButton(
                text = "확인",
                onClick = {},
                backgroundColor = StaccatoBlue,
                textColor = White,
            )
        },
        onDismissRequest = {},
    )
}
