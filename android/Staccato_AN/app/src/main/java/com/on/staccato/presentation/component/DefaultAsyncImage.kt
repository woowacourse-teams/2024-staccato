package com.on.staccato.presentation.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.on.staccato.R

@Composable
fun DefaultAsyncImage(
    @StringRes contentDescription: Int,
    modifier: Modifier = Modifier,
    url: String? = null,
    radiusDp: Dp = 0.dp,
    bitmapPixelSize: Int? = null,
    @DrawableRes placeHolder: Int? = null,
    @DrawableRes errorImageRes: Int? = null,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val context = LocalContext.current
    val imageRequest =
        remember(url, bitmapPixelSize, placeHolder, errorImageRes) {
            ImageRequest.Builder(context)
                .data(url)
                .apply {
                    bitmapPixelSize?.let {
                        size(it)
                    }
                    placeHolder?.let {
                        placeholder(it)
                    }
                    errorImageRes?.let {
                        fallback(it)
                        error(it)
                    }
                }.build()
        }
    val painter = rememberAsyncImagePainter(model = imageRequest)

    Image(
        modifier = modifier.clip(RoundedCornerShape(radiusDp)),
        painter = painter,
        contentDescription = stringResource(contentDescription),
        contentScale = contentScale,
    )
}

@Preview
@Composable
private fun ValidUrlAsyncImagePreview() {
    PreviewAsyncImage(url = "https://avatars.githubusercontent.com/u/103019852?v=4")
}

@Preview
@Composable
private fun NullUrlAsyncImagePreview() {
    PreviewAsyncImage(url = null)
}

@Preview
@Composable
private fun EmptyUrlAsyncImagePreview() {
    PreviewAsyncImage(url = "")
}

@Composable
private fun PreviewAsyncImage(url: String?) {
    DefaultAsyncImage(
        url = url,
        bitmapPixelSize = 150,
        placeHolder = R.drawable.icon_member,
        errorImageRes = R.drawable.icon_member,
        contentDescription = R.string.all_category_thumbnail_photo_description,
    )
}
