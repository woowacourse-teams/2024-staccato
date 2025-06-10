package com.on.staccato.presentation.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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
    @DrawableRes placeHolder: Int,
    @StringRes contentDescription: Int,
    modifier: Modifier = Modifier,
    url: String? = null,
    radiusDp: Dp = 0.dp,
    bitmapPixelSize: Int,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val painter =
        rememberAsyncImagePainter(
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .size(bitmapPixelSize)
                    .placeholder(placeHolder)
                    .fallback(placeHolder)
                    .error(placeHolder)
                    .build(),
        )

    Image(
        modifier = modifier.clip(RoundedCornerShape(radiusDp)),
        painter = painter,
        contentDescription = stringResource(contentDescription),
        contentScale = contentScale,
    )
}

@Preview
@Composable
private fun DefaultAsyncImagePreview() {
    DefaultAsyncImage(
        bitmapPixelSize = 150,
        url = "https://avatars.githubusercontent.com/u/103019852?v=4",
        placeHolder = R.drawable.icon_member,
        contentDescription = R.string.all_category_thumbnail_photo_description,
    )
}
