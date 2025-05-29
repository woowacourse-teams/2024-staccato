package com.on.staccato.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    dismiss: Boolean = false,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    AlertDialog(
        modifier = modifier.fillMaxWidth(),
        onDismissRequest = {  },
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
        confirmButton = {
            DefaultTextButton(
                text = "확인",
                onClick = onConfirmation,
                backgroundColor = StaccatoBlue,
                textColor = White,
            )
        },
        dismissButton = {
            if(dismiss) {
                DefaultTextButton(
                    text = "취소",
                    onClick = onDismissRequest,
                    backgroundColor = Gray1,
                    textColor = Gray4,
                )
            }
        },
        shape = RoundedCornerShape(10.dp),
        containerColor = White,
        titleContentColor = StaccatoBlack,
        textContentColor = Gray3,
    )
}

@Composable
@Preview(
    showBackground = true,
    backgroundColor = 0xDDDDDDL
)
private fun AlertDialogPreview() {
    DefaultAlertDialog(
        title = "제목제목제목",
        description = "내용내용내용.\n내용내용, 내용내용내용??",
        dismiss = true,
        onDismissRequest = {},
        onConfirmation = {},
    )
}
