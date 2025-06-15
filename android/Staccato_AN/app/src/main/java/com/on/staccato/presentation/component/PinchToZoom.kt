package com.on.staccato.presentation.component

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach

const val DEFAULT_MIN_ZOOM_SCALE = 1f
private const val DEFAULT_MAX_ZOOM_SCALE = 2f

@Composable
fun PinchToZoom(
    modifier: Modifier = Modifier,
    minScale: Float = DEFAULT_MIN_ZOOM_SCALE,
    maxScale: Float = DEFAULT_MAX_ZOOM_SCALE,
    onScaleChange: ((scale: Float) -> Unit)? = null,
    onDrag: ((Offset) -> Boolean)? = null,
    onTap: ((Offset) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    var scale by remember { mutableFloatStateOf(minScale) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    val slowMovement = 0.8f

    LaunchedEffect(scale) { onScaleChange?.invoke(scale) }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .onSizeChanged { containerSize = it }
                .pointerInput(Unit) {
                    awaitEachGesture {
                        var isZoomGesture = false
                        var pastTouchSlop = false
                        val touchSlop = viewConfiguration.touchSlop
                        awaitFirstDown(requireUnconsumed = false)

                        do {
                            val event = awaitPointerEvent()
                            if (event.changes.size > 1) {
                                isZoomGesture = true
                                val zoomChange = event.calculateZoom()
                                val panChange = event.calculatePan()

                                val newScale = (scale * zoomChange).coerceIn(minScale, maxScale)
                                scale = newScale
                                if (newScale > minScale) {
                                    offset += panChange * newScale
                                    offset = clampOffset(offset, newScale, containerSize)
                                } else {
                                    offset = Offset.Zero
                                }
                                event.changes.fastForEach { it.consume() }
                            } else if (!isZoomGesture) {
                                val dragChange = event.changes.first()
                                val dragAmount = dragChange.positionChange()

                                if (scale > minScale) {
                                    if (!pastTouchSlop && dragAmount.getDistance() > touchSlop) {
                                        pastTouchSlop = true
                                    }

                                    if (pastTouchSlop) {
                                        offset += dragAmount * scale * slowMovement
                                        offset = clampOffset(offset, scale, containerSize)
                                        val shouldConsume = onDrag?.invoke(dragAmount) ?: false
                                        if (shouldConsume) dragChange.consume()
                                    }
                                }
                            }
                        } while (event.changes.fastAny { it.pressed })
                    }
                }.pointerInput(Unit) {
                    detectTapGestures(
                        onTap = onTap,
                        onDoubleTap = { tapOffset ->
                            if (scale == minScale) {
                                scale = maxScale
                                val center = Offset(size.width / 2f, size.height / 2f)
                                val pan = (center - tapOffset) * scale
                                offset = clampOffset(pan, scale, containerSize)
                            } else {
                                scale = minScale
                                offset = Offset.Zero
                            }
                        },
                    )
                }.graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                ),
    ) {
        content()
    }
}

private fun clampOffset(
    offset: Offset,
    scale: Float,
    size: IntSize,
): Offset {
    val maxX = (size.width * (scale - 1)) / 2
    val maxY = (size.height * (scale - 1)) / 2
    return Offset(
        x = offset.x.coerceIn(-maxX, maxX),
        y = offset.y.coerceIn(-maxY, maxY),
    )
}
