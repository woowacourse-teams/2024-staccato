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
import androidx.compose.ui.platform.LocalContext
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultBasicDialog(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    dismiss: Boolean = false,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    val displayMetrics = LocalContext.current.resources.displayMetrics
    val width = (displayMetrics.widthPixels * 0.9).dp

    BasicAlertDialog(
        modifier = modifier.width(width),
        onDismissRequest = {  },
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
                    text = title,
                    style = Title2,
                    color = StaccatoBlack,
                )
                Text(
                    text = description,
                    style = Body2,
                    color = Gray3,
                )
                Spacer(modifier = modifier.height(36.dp))
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    if (dismiss) {
                        DefaultTextButton(
                            text = "취소",
                            onClick = onDismissRequest,
                            backgroundColor = Gray1,
                            textColor = Gray4,
                        )
                    }
                    Spacer(modifier = modifier.width(6.dp))
                    DefaultTextButton(
                        text = "확인",
                        onClick = onConfirmation,
                        backgroundColor = StaccatoBlue,
                        textColor = White,
                    )
                }
            }

        }
    }
}

@Composable
@Preview(
    showBackground = true,
    backgroundColor = 0xDDDDDDL
)
private fun DefaultBasicDialogPreview() {
    DefaultBasicDialog(
        title = "제목제목제목",
        description = "내용내용내용.\n내용내용, 내용내용내용??",
        dismiss = true,
        onDismissRequest = {  },
        onConfirmation = {  },
    )
}
