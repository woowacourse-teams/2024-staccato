package com.on.staccato.presentation.common.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale

@Composable
fun CoilImage(
    imageUrl: String,
    bitmapPixelSize: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    @DrawableRes placeHolder: Int? = null,
) {
    Image(
        painter =
            rememberAsyncImagePainter(
                model =
                    ImageRequest.Builder(context = LocalContext.current)
                        .data(imageUrl)
                        .apply {
                            placeHolder?.let {
                                placeholder(placeHolder)
                                error(placeHolder)
                            }
                        }
                        .scale(Scale.FILL)
                        .size(bitmapPixelSize)
                        .build(),
            ),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop,
    )
}
