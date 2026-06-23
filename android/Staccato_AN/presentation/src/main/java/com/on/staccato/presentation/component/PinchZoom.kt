package com.on.staccato.presentation.component

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach

@Suppress("ktlint:standard:property-naming")
@Immutable
object PinchZoomDefaults {
    const val MinScale = 1f
    const val MaxScale = 2f
}

private const val SLOW_MOVEMENT_COEFFICIENT = 0.8f
private const val MIN_SCALE_TOLERANCE = 0.01f

/**
 * 핀치 줌 · 더블탭 줌 · 확대 상태에서의 드래그(팬)를 단일 Gesture Detector로 처리하는 컨테이너입니다.
 *
 * 탭 · 더블탭 · 드래그 · 핀치를 하나의 제스처 루프에서 분류하며,
 * 한 번의 연속 제스처 안에서는 먼저 인식된 동작으로 고정되어 팬과 줌이 섞이지 않습니다.
 *
 * 더블탭은 현재 배율에 따라 토글되며, 조금이라도 확대돼 있으면 최소 배율로 되돌리고, 최소 배율이면 최대 배율로 확대합니다.
 *
 * @param minScale 최소 배율입니다. 이 배율에서는 팬이 적용되지 않고 offset이 0으로 고정됩니다. (기본 값 1f, 원본 배율)
 * @param maxScale 최대 배율입니다. (기본 값 2f, 2배율)
 * @param onScaleChange 배율이 바뀔 때마다 현재 scale로 호출됩니다. (핀치 · 더블탭 공통)
 * @param shouldConsumeDrag 한 손가락 드래그가 일어날 때마다 그 변위(dragAmount)로 호출됩니다.
 *   **팬은 확대 상태(최소 배율 초과)에서만 내부적으로 적용**되며, 이 콜백의 반환값은 ***해당
 *   드래그 이벤트를 소비할지*** 만 결정합니다.
 *   - `true`  → 이벤트를 소비하여 부모(예: Pager · 스크롤)로 전파하지 않습니다.
 *   - `false` → 소비하지 않아 부모가 이어서 제스처를 처리할 수 있습니다.
 * @param onTap 한 손가락 탭 시 탭 위치로 호출됩니다.
 * @param content 확대 · 이동 변환이 적용될 콘텐츠입니다.
 */
@Composable
fun PinchZoom(
    modifier: Modifier = Modifier,
    minScale: Float = PinchZoomDefaults.MinScale,
    maxScale: Float = PinchZoomDefaults.MaxScale,
    onScaleChange: ((scale: Float) -> Unit)? = null,
    shouldConsumeDrag: ((dragAmount: Offset) -> Boolean)? = null,
    onTap: ((Offset) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val state = rememberPinchZoomState(minScale, maxScale)

    val currentShouldConsumeDrag by rememberUpdatedState(shouldConsumeDrag)
    val currentOnTap by rememberUpdatedState(onTap)
    val currentOnScaleChange by rememberUpdatedState(onScaleChange)

    LaunchedEffect(state) {
        snapshotFlow { state.scale }.collect { currentOnScaleChange?.invoke(it) }
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .onSizeChanged { state.containerSize = it }
                .pinchZoomGesture(
                    state = state,
                    shouldConsumeDrag = { currentShouldConsumeDrag?.invoke(it) ?: false },
                    onTap = { currentOnTap?.invoke(it) },
                )
                .graphicsLayer {
                    scaleX = state.scale
                    scaleY = state.scale
                    translationX = state.offset.x
                    translationY = state.offset.y
                },
    ) {
        content()
    }
}

@Stable
class PinchZoomState(
    val minScale: Float = PinchZoomDefaults.MinScale,
    val maxScale: Float = PinchZoomDefaults.MaxScale,
) {
    var scale by mutableFloatStateOf(minScale)
        private set
    var offset by mutableStateOf(Offset.Zero)
        private set

    var containerSize by mutableStateOf(IntSize.Zero)

    fun zoom(
        zoomChange: Float,
        panChange: Offset,
    ) {
        scale = (scale * zoomChange).coerceIn(minScale, maxScale)
        offset =
            if (scale > minScale) {
                clampOffset(offset + panChange * scale, scale, containerSize)
            } else {
                Offset.Zero
            }
    }

    fun pan(dragAmount: Offset) {
        if (scale <= minScale) return
        offset =
            clampOffset(
                offset + dragAmount * scale * SLOW_MOVEMENT_COEFFICIENT,
                scale,
                containerSize,
            )
    }

    fun doubleTapZoom(tapOffset: Offset) {
        val isZoomedIn = scale - minScale > MIN_SCALE_TOLERANCE
        if (isZoomedIn) {
            scale = minScale
            offset = Offset.Zero
        } else {
            scale = maxScale
            val center = Offset(containerSize.width / 2f, containerSize.height / 2f)
            offset = clampOffset((center - tapOffset) * scale, scale, containerSize)
        }
    }
}

@Composable
fun rememberPinchZoomState(
    minScale: Float = PinchZoomDefaults.MinScale,
    maxScale: Float = PinchZoomDefaults.MaxScale,
): PinchZoomState = remember(minScale, maxScale) { PinchZoomState(minScale, maxScale) }

private fun Modifier.pinchZoomGesture(
    state: PinchZoomState,
    shouldConsumeDrag: (Offset) -> Boolean,
    onTap: (Offset) -> Unit,
): Modifier =
    pointerInput(state.minScale, state.maxScale) {
        val touchSlop = viewConfiguration.touchSlop

        awaitEachGesture {
            val firstDown = awaitFirstDown(requireUnconsumed = false)

            val firstUp =
                awaitPanZoomOrTap(state, shouldConsumeDrag, touchSlop, firstDown)
                    ?: return@awaitEachGesture

            val secondDown = awaitSecondDown(firstUp)
            if (secondDown == null) {
                onTap(firstUp.position)
                return@awaitEachGesture
            }

            if (awaitTapUp(touchSlop, secondDown)) {
                state.doubleTapZoom(secondDown.position)
            } else {
                onTap(firstUp.position)
            }
        }
    }

/** 첫 터치 시퀀스로, 팬/줌을 적용합니다. 팬(드래그)·줌이면 null을, 단순 탭이면 탭 업의 변화를 반환합니다. */
private suspend fun AwaitPointerEventScope.awaitPanZoomOrTap(
    state: PinchZoomState,
    shouldConsumeDrag: (Offset) -> Boolean,
    touchSlop: Float,
    down: PointerInputChange,
): PointerInputChange? {
    var isDrag = false
    var isZoom = false
    var upChange: PointerInputChange? = null
    do {
        val event = awaitPointerEvent()
        val pressedCount = event.changes.count { it.pressed }
        when {
            pressedCount == 2 && !isDrag -> {
                isZoom = true
                state.zoom(event.calculateZoom(), event.calculatePan())
                event.changes.fastForEach { it.consume() }
            }

            pressedCount == 1 && !isZoom -> {
                val drag = event.changes.first { it.pressed }
                if (!isDrag && (drag.position - down.position).getDistance() > touchSlop) {
                    isDrag = true
                }
                if (isDrag) {
                    if (state.scale > state.minScale) state.pan(drag.positionChange())
                    if (shouldConsumeDrag(drag.positionChange())) drag.consume()
                }
            }
        }
        event.changes.fastForEach { if (it.changedToUp()) upChange = it }
    } while (event.changes.fastAny { it.pressed })

    return if (isDrag || isZoom) null else upChange
}

/** 두 번째 터치 시퀀스로, 더블탭의 두 번째 탭 다운을 대기합니다. 더블 탭 시간을 초과하면 null을 반환합니다.
 * (Compose detectTapGestures의 내부와 동일한 방식) */
private suspend fun AwaitPointerEventScope.awaitSecondDown(firstUp: PointerInputChange): PointerInputChange? =
    withTimeoutOrNull(viewConfiguration.doubleTapTimeoutMillis) {
        val minUptime = firstUp.uptimeMillis + viewConfiguration.doubleTapMinTimeMillis
        var change: PointerInputChange
        do {
            change = awaitFirstDown()
        } while (change.uptimeMillis < minUptime)
        change
    }

/** 두 번째 시퀀스가 slop 안에서 up 되면 true(=탭), slop을 넘으면 false를 반환합니다. */
private suspend fun AwaitPointerEventScope.awaitTapUp(
    touchSlop: Float,
    down: PointerInputChange,
): Boolean {
    do {
        val event = awaitPointerEvent()
        val change = event.changes.firstOrNull { it.id == down.id } ?: return false
        if ((change.position - down.position).getDistance() > touchSlop) return false
        if (change.changedToUp()) {
            change.consume()
            return true
        }
    } while (event.changes.fastAny { it.pressed })
    return false
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
