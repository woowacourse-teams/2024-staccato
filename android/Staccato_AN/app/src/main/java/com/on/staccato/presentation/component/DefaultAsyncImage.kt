package com.on.staccato.presentation.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.on.staccato.R
import com.on.staccato.presentation.util.dpToPx

@Composable
fun DefaultAsyncImage(
    modifier: Modifier = Modifier,
    bitmapPixelSize: Int,
    url: String? = null,
    @DrawableRes placeHolder: Int,
    @StringRes contentDescription: Int,
    contentScale: ContentScale = ContentScale.Crop,
    radius: Float = 0f,
) {
    val painter =
        rememberAsyncImagePainter(
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .transformations(RoundedCornersTransformation(radius.dpToPx(LocalContext.current)))
                    .size(bitmapPixelSize)
                    .placeholder(placeHolder)
                    .fallback(placeHolder)
                    .error(placeHolder)
                    .build(),
        )

    Image(
        modifier = modifier,
        painter = painter,
        contentDescription = stringResource(contentDescription),
        contentScale = contentScale,
    )
}

@Preview
@Composable
private fun ImageComponentPreview() {
    DefaultAsyncImage(
        bitmapPixelSize = 150,
        url = "https://avatars.githubusercontent.com/u/103019852?v=4",
        placeHolder = R.drawable.icon_member,
        contentDescription = R.string.all_category_thumbnail_photo_description,
    )
}
