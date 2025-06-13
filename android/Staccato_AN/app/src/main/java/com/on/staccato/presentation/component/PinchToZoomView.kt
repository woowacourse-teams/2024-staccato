package com.on.staccato.presentation.component

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

private const val DEFAULT_MIN_ZOOM_SCALE = 1f
private const val DEFAULT_MAX_ZOOM_SCALE = 2f

@Composable
fun PinchToZoomView(
    modifier: Modifier = Modifier,
    minScale: Float = DEFAULT_MIN_ZOOM_SCALE,
    maxScale: Float = DEFAULT_MAX_ZOOM_SCALE,
    content: @Composable () -> Unit,
) {
    // 크기, 오프셋 값 기억
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // 초기 오프셋 값 기억
    var initialOffset by remember { mutableStateOf(Offset(0f, 0f)) }

    // 느린 속도 계수
    val slowMovement = 0.5f

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    // detectTransformGestures: (panZoomLock: Boolean, onGesture(centroid, pan, zoom, rotation))
                    // centroid: 중심점(포인터들 사이의 중앙 위치, Offset)
                    // pan: View를 이동한 백터
                    // zoom: 확대 비율, 1f를 기준으로 크고 작아짐
                    // rotation: 회전 각도
                    detectTransformGestures { _, pan, zoom, _ ->
                        // zoom 값으로 새로운 scale 계산(coerceIn으로 최소, 최대 범위 제한)
                        val newScale = scale * zoom
                        scale = newScale.coerceIn(minScale, maxScale)

                        // zoom 크기과 pan 값으로 새 offset 계산
                        // 여기서 size는 PointerInputScope의 프로퍼티
                        // 사용자가 핀치를 하는 위치에 따라 콘텐츠 중심이 바뀔 수 있기 때문에
                        // 확대한 만큼의 이동 보정이 필요
                        val centerX = size.width / 2
                        val centerY = size.height / 2
                        val offsetXChange = (centerX - offsetX) * (newScale / scale - 1)
                        val offsetYChange = (centerY - offsetY) * (newScale / scale - 1)

                        // offset 허용 범위 계산 -> 콘텐츠가 화면 밖으로 너무 나가지 않게 제한
                        // 화면 크기와 스케일을 기반으로 최대 이동 범위를 계산
                        // ex) scale이 2라면 전체 콘텐츠의 절반까지 이동 가능
                        val maxOffsetX = (size.width / 2) * (scale - 1)
                        val minOffsetX = -maxOffsetX
                        val maxOffsetY = (size.height / 2) * (scale - 1)
                        val minOffsetY = -maxOffsetY

                        // 실제 offset 업데이트
                        // 팬(pan.x, pan.y): 사용자의 손가락 이동 방향과 거리
                        // scale * slowMovement: 확대된 상태일수록 더 많이 이동, slowMovement로 속도 조절
                        // 최종적으로 오프셋이 이동 제한 범위(min~max) 안에 들어오도록 coerceIn
                        // ifc(scale * zoom <= maxScale) : 더 이상 확대 못할 때 pan도 제한
                        if (scale * zoom <= maxScale) {
                            offsetX = (offsetX + pan.x * scale * slowMovement + offsetXChange)
                                .coerceIn(minOffsetX, maxOffsetX)
                            offsetY = (offsetY + pan.y * scale * slowMovement + offsetYChange)
                                .coerceIn(minOffsetY, maxOffsetY)
                        }

                        // 첫 팬 동작 시 초기 위치 저장
                        // 팬이 시작되었고 (pan != 0) 이전에 저장된 값이 없다면
                        // 현재 offset 값을 initialOffset으로 저장
                        // 나중에 더블탭 등의 리셋 동작에서 사용할 수 있음
                        if (pan != Offset(0f, 0f) && initialOffset == Offset(0f, 0f)) {
                            initialOffset = Offset(offsetX, offsetY)
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            // 더블탭 동작 시 scale과 offset 초기화
                            // 확대한 상태라면 scale을 1f로 변경(원본 크기)
                            if (scale != 1f) {
                                scale = 1f
                                offsetX = initialOffset.x
                                offsetY = initialOffset.y
                            } else {
                                scale = 2f
                            }
                        }
                    )
                }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                    translationY = offsetY
                }
    ) {
        content()
    }
}
