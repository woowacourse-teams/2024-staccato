package com.on.staccato.presentation.component

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
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
import kotlin.math.sign

@Suppress("ktlint:standard:property-naming")
@Immutable
object PinchZoomDefaults {
    const val MinScale = 1f
    const val MaxScale = 2f

    /** 확대 여부를 판단하는 허용 오차입니다. 배율과 최소 배율의 차이가 이 값보다 작으면 확대되지 않은 것으로 봅니다. */
    const val ZoomTolerance = 0.01f
}

private const val SLOW_MOVEMENT_COEFFICIENT = 0.8f

/**
 * 콘텐츠에 핀치 줌과 더블탭 줌, 확대 상태에서의 드래그(팬)를 더하는 컨테이너입니다.
 *
 * 탭·드래그·핀치·더블탭을 하나의 제스처 루프에서 구분합니다. 한 번의 연속된 제스처는 처음 인식된 동작으로
 * 고정되므로, 팬과 줌이 섞이지 않습니다.
 *
 * 더블탭하면 배율이 토글됩니다. 조금이라도 확대돼 있으면 최소 배율로 되돌리고, 최소 배율이면 최대 배율로 확대합니다.
 *
 * 상태를 내부에서 만드는 간편한 오버로드입니다. 배율이나 확대 여부를 바깥에서 관찰하거나 직접 제어해야 하면
 * (예: 페이지가 바뀔 때 줌 해제) [PinchZoomState]를 직접 받는 오버로드를 사용하세요.
 *
 * @param minScale 최소 배율입니다. 이 배율에서는 팬이 동작하지 않고 위치가 가운데에 고정됩니다. (기본값 1f, 원본 크기)
 * @param maxScale 최대 배율입니다. (기본값 2f)
 * @param zoomTolerance 확대로 간주할 최소 배율 차이입니다. 이 값 이하의 미세한 확대는 확대되지 않은 것으로 봅니다. (기본값 0.01f)
 * @param shouldConsumeDrag 확대 상태의 한 손가락 드래그를 소비할지 결정합니다. (선택)
 *   지정하지 않으면 팬이 동작하는 동안(최소 배율보다 크게 확대된 상태) 드래그를 소비해 부모(Pager 등)로 넘기지 않습니다.
 *   확대된 상태에서도 이미지 경계에 닿으면 부모가 스크롤을 이어받게 하는 등, 소비 조건을 바꾸고 싶을 때만 지정하세요.
 *   `dragDirection`은 각 축의 부호(`-1f`, `0f`, `+1f`)로 드래그 방향만 알려줍니다. (이동 거리는 포함하지 않습니다.)
 *   `true`면 드래그를 소비해 부모로 넘기지 않고, `false`면 부모가 이어서 처리합니다.
 * @param onTap 한 손가락으로 탭하면 그 위치와 함께 호출됩니다.
 * @param content 확대와 이동이 적용될 콘텐츠입니다.
 */
@Composable
fun PinchZoom(
    modifier: Modifier = Modifier,
    minScale: Float = PinchZoomDefaults.MinScale,
    maxScale: Float = PinchZoomDefaults.MaxScale,
    zoomTolerance: Float = PinchZoomDefaults.ZoomTolerance,
    shouldConsumeDrag: ((dragDirection: Offset) -> Boolean)? = null,
    onTap: ((Offset) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    PinchZoom(
        state = rememberPinchZoomState(minScale, maxScale, zoomTolerance),
        modifier = modifier,
        shouldConsumeDrag = shouldConsumeDrag,
        onTap = onTap,
        content = content,
    )
}

/**
 * 상태([PinchZoomState])를 바깥에서 소유(hoist)하는 오버로드입니다.
 *
 * 배율이나 확대 여부를 바깥에서 관찰하거나 [PinchZoomState.reset]으로 직접 제어할 수 있어,
 * 부모(예: Pager)와 확대 상태를 공유해야 할 때 사용합니다.
 *
 * @param state 확대·이동 상태입니다. `rememberPinchZoomState()`로 만들어 바깥에서 보관하세요.
 * @param shouldConsumeDrag 확대 상태의 한 손가락 드래그를 소비할지 결정합니다. (선택)
 *   지정하지 않으면 팬이 동작하는 동안 드래그를 소비합니다. `dragDirection`은 각 축의 부호로 드래그 방향을 알려주므로,
 *   "이미지가 경계에 닿았을 때만 부모가 페이지를 넘기게" 같은 정책을 표현할 수 있습니다.
 * @param onTap 한 손가락으로 탭하면 그 위치와 함께 호출됩니다.
 * @param content 확대와 이동이 적용될 콘텐츠입니다.
 */
@Composable
fun PinchZoom(
    state: PinchZoomState,
    modifier: Modifier = Modifier,
    shouldConsumeDrag: ((dragDirection: Offset) -> Boolean)? = null,
    onTap: ((Offset) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val currentShouldConsumeDrag by rememberUpdatedState(shouldConsumeDrag)
    val currentOnTap by rememberUpdatedState(onTap)

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .onSizeChanged { state.containerSize = it }
                .pinchZoomGesture(
                    state = state,
                    shouldConsumeDrag = { dragDirection, isPanning ->
                        currentShouldConsumeDrag?.invoke(dragDirection) ?: isPanning
                    },
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

/**
 * [PinchZoom]의 확대·이동 상태를 담는 홀더입니다.
 *
 * [rememberPinchZoomState]로 만들어 [PinchZoom]에 넘기면, 배율([scale])과 위치([offset]), 확대 여부([isZoomedIn])를
 * 바깥에서 관찰하거나 [reset]으로 직접 제어할 수 있습니다. 예를 들어 Pager는 [isZoomedIn]을 보고 확대 중일 때
 * 페이지 스와이프를 막을 수 있습니다.
 *
 * @param minScale 최소 배율입니다. (기본값 1f, 원본 크기)
 * @param maxScale 최대 배율입니다. (기본값 2f)
 * @param zoomTolerance 확대로 간주할 최소 배율 차이입니다. 이 값 이하의 미세한 확대는 확대되지 않은 것으로 봅니다. (기본값 0.01f)
 */
@Stable
class PinchZoomState(
    val minScale: Float = PinchZoomDefaults.MinScale,
    val maxScale: Float = PinchZoomDefaults.MaxScale,
    private val zoomTolerance: Float = PinchZoomDefaults.ZoomTolerance,
) {
    /** 현재 배율입니다. 항상 [minScale]과 [maxScale] 사이입니다. */
    var scale by mutableFloatStateOf(minScale)
        private set

    /** 콘텐츠가 중앙에서 이동한 위치입니다. 중앙에 있으면 [Offset.Zero]입니다. */
    var offset by mutableStateOf(Offset.Zero)
        private set

    /** 콘텐츠 영역의 크기입니다. 팬·줌이 경계를 벗어나지 않도록 제한하는 데 쓰이며, 보통 [PinchZoom]이 측정해 채웁니다. */
    var containerSize by mutableStateOf(IntSize.Zero)

    /** 확대되어 있는지 여부입니다. 배율이 최소 배율보다 허용 오차(`zoomTolerance`)를 넘어 크면 true입니다. */
    val isZoomedIn: Boolean by derivedStateOf { scale - minScale > zoomTolerance }

    /**
     * 핀치 제스처에 맞춰 배율과 위치를 갱신합니다.
     *
     * @param zoomChange 이전 배율에 곱해지는 배율 변화량입니다. 계산 결과 값은 [minScale]~[maxScale]로 제한됩니다.
     * @param panChange 두 손가락이 함께 움직인 거리입니다.
     */
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

    /**
     * 확대된 상태에서 드래그한 만큼 위치를 옮깁니다. 최소 배율에서는 아무 일도 하지 않습니다.
     *
     * @param dragAmount 손가락이 움직인 거리입니다.
     */
    fun pan(dragAmount: Offset) {
        if (scale <= minScale) return
        offset =
            clampOffset(
                offset + dragAmount * scale * SLOW_MOVEMENT_COEFFICIENT,
                scale,
                containerSize,
            )
    }

    /**
     * 더블탭으로 배율을 토글합니다. 확대되어 있으면 최소 배율로 되돌리고, 그렇지 않으면 최대 배율로 확대합니다.
     *
     * @param tapOffset 더블탭한 지점입니다. 확대할 때 이 지점이 화면 안에 남도록 위치를 맞춥니다.
     */
    fun doubleTapZoom(tapOffset: Offset) {
        if (isZoomedIn) {
            scale = minScale
            offset = Offset.Zero
        } else {
            scale = maxScale
            val center = Offset(containerSize.width / 2f, containerSize.height / 2f)
            offset = clampOffset((center - tapOffset) * scale, scale, containerSize)
        }
    }

    /** 배율과 위치를 처음 상태(최소 배율, 정중앙)로 되돌립니다. 예를 들어 페이지가 바뀔 때 이전 사진의 확대를 풉니다. */
    fun reset() {
        scale = minScale
        offset = Offset.Zero
    }
}

/**
 * 리컴포지션이 일어나도 유지되는 [PinchZoomState]를 만들어 반환합니다.
 *
 * 만든 상태를 [PinchZoom]에 넘기면, 배율이나 확대 여부를 바깥에서 관찰하거나 제어할 수 있습니다.
 * [minScale], [maxScale], [zoomTolerance] 중 하나라도 바뀌면 상태를 새로 만듭니다.
 *
 * @param minScale 최소 배율입니다. (기본값 1f, 원본 크기)
 * @param maxScale 최대 배율입니다. (기본값 2f)
 * @param zoomTolerance 확대로 간주할 최소 배율 차이입니다. (기본값 0.01f)
 */
@Composable
fun rememberPinchZoomState(
    minScale: Float = PinchZoomDefaults.MinScale,
    maxScale: Float = PinchZoomDefaults.MaxScale,
    zoomTolerance: Float = PinchZoomDefaults.ZoomTolerance,
): PinchZoomState = remember(minScale, maxScale, zoomTolerance) { PinchZoomState(minScale, maxScale, zoomTolerance) }

/**
 * 지정한 요소의 터치 제스처(핀치 줌·팬·탭·더블탭)를 읽어 [state]를 갱신하는 Modifier입니다.
 *
 * 상태만 갱신할 뿐, 확대·이동을 화면에 그리지는 않습니다. 눈에 보이는 변환은 같은 [state]를 읽는
 * `graphicsLayer`(배율·위치)와 함께 적용해야 하며, [PinchZoom]이 이 둘을 묶어 씁니다.
 *
 * 한 번의 제스처(첫 손가락이 내려와 모두 떨어질 때까지)를 다음 순서로 판정합니다.
 * 1. [awaitPanZoomOrTap]으로 첫 시퀀스를 처리합니다. 두 손가락이면 줌, 한 손가락이 움직이면 팬이며 곧바로
 *    [state]에 반영하고 제스처를 끝냅니다. 움직임이 없는 단순 탭이면 다음 단계로 넘어갑니다.
 * 2. [awaitSecondDown]으로 더블탭 시간 안에 두 번째 탭이 오는지 기다립니다. 오지 않으면 [onTap]을 호출합니다.
 * 3. 두 번째 탭이 오면 [awaitTapUp]으로 그것이 탭인지 확인해, 탭이면 [PinchZoomState.doubleTapZoom]을,
 *    움직였다면 단순 탭으로 보고 [onTap]을 호출합니다.
 *
 * [state]가 바뀌면 제스처 처리 코루틴을 새로 시작합니다.
 *
 * @param state 제스처가 갱신할 확대·이동 상태입니다.
 * @param shouldConsumeDrag 팬 드래그를 소비할지 결정합니다. 드래그 방향(dragDirection)과 현재 팬 중인지 여부(isPanning)를
 *   받아 `true`를 반환하면 이벤트를 소비해 부모로 넘기지 않습니다.
 * @param onTap 단순 탭으로 확정됐을 때 그 위치로 호출됩니다.
 */
private fun Modifier.pinchZoomGesture(
    state: PinchZoomState,
    shouldConsumeDrag: (dragDirection: Offset, isPanning: Boolean) -> Boolean,
    onTap: (Offset) -> Unit,
): Modifier =
    pointerInput(state) {
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

/**
 * 첫 손가락이 내려온 뒤부터 모든 손가락이 떨어질 때까지의 터치 시퀀스를 처리합니다.
 *
 * 눌린 손가락 수로 동작을 구분합니다. 두 손가락이면 핀치(줌), 한 손가락이 [touchSlop]을 넘어 움직이면 드래그(팬)입니다.
 * 한 번 줌이나 드래그로 확정되면 그 동작으로 고정되어, 시퀀스 도중 손가락 수가 바뀌어도 다른 동작으로 넘어가지 않습니다.
 * (예: 드래그 중 손가락을 하나 더 올려도 줌으로 바뀌지 않습니다.)
 *
 * 줌이면 [PinchZoomState.zoom]으로 배율과 위치를 갱신하며 이벤트를 소비하고, 드래그면 확대된 상태에서만
 * [PinchZoomState.pan]으로 위치를 옮긴 뒤 [shouldConsumeDrag] 결과에 따라 이벤트를 소비합니다.
 *
 * @param down 시퀀스를 시작한 첫 다운입니다. [touchSlop]을 넘었는지 재는 기준점으로 씁니다.
 * @return 드래그나 줌으로 처리했으면 `null`, 움직임이 없는 단순 탭이면 손을 뗀 변화(up)를 반환합니다.
 *   호출부는 이 값으로 탭·더블탭을 이어서 판정합니다.
 */
private suspend fun AwaitPointerEventScope.awaitPanZoomOrTap(
    state: PinchZoomState,
    shouldConsumeDrag: (dragDirection: Offset, isPanning: Boolean) -> Boolean,
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
                    val dragChange = drag.positionChange()
                    val isPanning = state.scale > state.minScale
                    if (isPanning) state.pan(dragChange)
                    val dragDirection = Offset(dragChange.x.sign, dragChange.y.sign)
                    if (shouldConsumeDrag(dragDirection, isPanning)) drag.consume()
                }
            }
        }
        event.changes.fastForEach { if (it.changedToUp()) upChange = it }
    } while (event.changes.fastAny { it.pressed })

    return if (isDrag || isZoom) null else upChange
}

/**
 * 첫 탭이 끝난 뒤, 더블탭의 두 번째 탭이 내려오기를 기다립니다.
 *
 * 더블탭 제한 시간(`doubleTapTimeoutMillis`) 안에 새 다운이 오면 그 변화를, 시간을 넘기면 `null`을 반환합니다.
 * 첫 탭 직후 최소 간격(`doubleTapMinTimeMillis`)보다 이르게 들어온 다운은 건너뛰어, 손가락이 튕기는 등의
 * 오입력을 더블탭으로 오인하지 않습니다. (Compose detectTapGestures의 내부 구현과 같은 방식)
 *
 * @param firstUp 첫 탭에서 손을 뗀 변화입니다. 두 번째 다운이 유효한지 재는 시각 기준으로 씁니다.
 */
private suspend fun AwaitPointerEventScope.awaitSecondDown(firstUp: PointerInputChange): PointerInputChange? =
    withTimeoutOrNull(viewConfiguration.doubleTapTimeoutMillis) {
        val minUptime = firstUp.uptimeMillis + viewConfiguration.doubleTapMinTimeMillis
        var change: PointerInputChange
        do {
            change = awaitFirstDown()
        } while (change.uptimeMillis < minUptime)
        change
    }

/**
 * 더블탭 후보인 두 번째 터치가 탭인지 드래그인지 가려냅니다.
 *
 * [down]과 같은 포인터를 추적하다가, [touchSlop] 안에서 손을 떼면 탭으로 보고 이벤트를 소비한 뒤 `true`를 반환합니다.
 * 손가락이 [touchSlop]을 벗어나 움직이거나 추적하던 포인터가 사라지면, 탭이 아니라고 보고 `false`를 반환합니다.
 *
 * @param down 판정 대상인 두 번째 탭의 다운입니다. 이 포인터의 id로 추적하고, 이동 거리의 기준점으로 씁니다.
 * @return 탭이면 `true`, 드래그면 `false`.
 */
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

/**
 * 확대된 콘텐츠 밖으로 빈 여백이 보이지 않도록 [offset]을 이동 가능한 범위로 제한합니다.
 *
 * 축마다 콘텐츠가 [size]보다 커진 양의 절반(`size * (scale - 1) / 2`)까지만 이동할 수 있습니다.
 */
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
