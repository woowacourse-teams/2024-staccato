package com.on.staccato.presentation.component

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * PinchZoomState의 상태 전이 계약을 검증한다.
 *
 * 이 클래스는 화면·제스처가 아니라 "확대/축소/드래그/더블탭 연산이 scale·offset에 올바르게 반영되는가"
 * (상·하한 클램프, 최소 배율에서의 팬 차단, offset clamp, 더블탭 토글 등)를 본다.
 * 실제 손가락 제스처 인식과 "확대된 이미지 바깥에 빈 여백이 보이는가" 같은 렌더링·UX 검증은
 * PinchZoom 컴포저블의 UI 테스트 몫이다.
 *
 * - scale: 배율 (minScale = 원본, maxScale = 최대 확대). 배율은 리터럴이 아니라 설정된 min/max 기준으로 검증한다.
 * - offset: 중앙 기준 이동량 (0, 0 = 정중앙). 확대·감속 계산의 부동소수점 오차와 `Offset`의 비트 단위
 *   동등성 때문에 위치는 픽셀 허용오차로 검증한다.
 */
class PinchZoomStateTest {
    /** SCREEN x SCREEN 화면에 [minScale]~[maxScale] 범위의 핀치줌 상태를 놓는다. */
    private fun pinchZoomOnScreen(
        minScale: Float = 1f,
        maxScale: Float = 2f,
    ) = PinchZoomState(minScale = minScale, maxScale = maxScale)
        .apply { containerSize = IntSize(SCREEN, SCREEN) }

    private fun center() = Offset(x = SCREEN / 2f, y = SCREEN / 2f)

    @Nested
    @DisplayName("처음 화면을 열었을 때")
    inner class WhenOpened {
        @Test
        fun `원본 배율로 화면 정중앙에 놓인다`() {
            // given & when: 핀치줌 화면을 처음 연다
            val pinchZoomState = pinchZoomOnScreen()

            // then: 원본 배율로 치우침 없이 정중앙에 놓인다
            assertThat(pinchZoomState.scale).isEqualTo(pinchZoomState.minScale)
            assertThatOffset(pinchZoomState.offset).isAt(x = 0f, y = 0f)
        }
    }

    @Nested
    @DisplayName("확대·축소할 때")
    inner class WhenPinching {
        @Test
        fun `확대하면 그 비율만큼 배율이 커진다`() {
            // given: 원본 배율의 핀치줌
            val pinchZoomState = pinchZoomOnScreen()

            // when: 1.5배로 확대하면
            pinchZoomState.zoom(zoomChange = 1.5f, panChange = Offset.Zero)

            // then: 배율이 그 비율만큼 커진다
            assertThat(pinchZoomState.scale).isEqualTo(pinchZoomState.minScale * 1.5f)
        }

        @ParameterizedTest(name = "최대 배율 {0}배로 설정하면 그 이상 확대되지 않는다")
        @ValueSource(floats = [1.5f, 2f, 3f, 10f])
        fun `설정된 최대 배율을 넘어 확대되지 않는다`(maxScale: Float) {
            // given: 최대 배율이 maxScale로 설정된 핀치줌
            val pinchZoomState = pinchZoomOnScreen(maxScale = maxScale)

            // when: 아무리 크게 벌려도
            pinchZoomState.zoom(zoomChange = 100f, panChange = Offset.Zero)

            // then: 설정된 최대 배율에서 멈춘다
            assertThat(pinchZoomState.scale).isEqualTo(maxScale)
        }

        @ParameterizedTest(name = "최소 배율 {0}배로 설정하면 그 이하로 축소되지 않는다")
        @ValueSource(floats = [0.5f, 1f, 1.5f])
        fun `설정된 최소 배율 아래로 축소되지 않는다`(minScale: Float) {
            // given: 최소 배율이 minScale로 설정된 핀치줌
            val pinchZoomState = pinchZoomOnScreen(minScale = minScale, maxScale = minScale + 2f)

            // when: 아무리 오므려도
            pinchZoomState.zoom(zoomChange = 0.1f, panChange = Offset.Zero)

            // then: 설정된 최소 배율에서 멈춘다
            assertThat(pinchZoomState.scale).isEqualTo(minScale)
        }

        @Test
        fun `확대 중에 위치를 옮기면 그 방향으로 함께 이동한다`() {
            // given: 원본 배율의 핀치줌
            val pinchZoomState = pinchZoomOnScreen()

            // when: 확대와 동시에 오른쪽으로 이동시키면
            pinchZoomState.zoom(zoomChange = 2f, panChange = Offset(x = 100f, y = 0f))

            // then: 그 방향(오른쪽, 양의 방향)으로 함께 이동한다
            assertThat(pinchZoomState.offset.x).isPositive()
        }

        @Test
        fun `원본 배율로 되돌아오면 치우침이 사라진다`() {
            // given: 확대 후 한쪽으로 치우쳐 둔 핀치줌
            val pinchZoomState = pinchZoomOnScreen()
            pinchZoomState.zoom(zoomChange = 2f, panChange = Offset.Zero)
            pinchZoomState.pan(dragAmount = Offset(x = -100f, y = 0f))

            // when: 다시 원본 배율로 축소하면
            pinchZoomState.zoom(zoomChange = 0.4f, panChange = Offset.Zero)

            // then: 원본 배율로 돌아오고 치우침이 사라진다
            assertThat(pinchZoomState.scale).isEqualTo(pinchZoomState.minScale)
            assertThatOffset(pinchZoomState.offset).isAt(x = 0f, y = 0f)
        }
    }

    @Nested
    @DisplayName("이미지를 드래그(팬)할 때")
    inner class WhenDragging {
        @Test
        fun `원본 배율에서는 드래그해도 위치가 고정된다`() {
            // given: 원본 배율의 핀치줌
            val pinchZoomState = pinchZoomOnScreen()

            // when: 드래그해도
            pinchZoomState.pan(dragAmount = Offset(x = 100f, y = 100f))

            // then: 위치가 정중앙에 고정된다
            assertThatOffset(pinchZoomState.offset).isAt(x = 0f, y = 0f)
        }

        @Test
        fun `확대된 상태에서 드래그하면 그 방향으로 이동한다`() {
            // given: 2배로 확대된 핀치줌
            val pinchZoomState = pinchZoomOnScreen()
            pinchZoomState.zoom(zoomChange = 2f, panChange = Offset.Zero)

            // when: 왼쪽으로 드래그하면
            pinchZoomState.pan(dragAmount = Offset(x = -100f, y = 0f))

            // then: 손가락 방향(왼쪽, 음의 방향)으로 이동한다
            assertThat(pinchZoomState.offset.x).isNegative()
        }

        @Test
        fun `확대 상태에서 한계를 넘겨 드래그하면 offset이 더 이상 커지지 않고 멈춘다`() {
            // given: 2배로 확대한 뒤 한계 이상으로 한 번 민 핀치줌
            val pinchZoomState = pinchZoomOnScreen()
            pinchZoomState.zoom(zoomChange = 2f, panChange = Offset.Zero)
            pinchZoomState.pan(dragAmount = Offset(x = -1000f, y = 0f))
            val clampedOffset = pinchZoomState.offset

            // when: 같은 방향으로 더 민다
            pinchZoomState.pan(dragAmount = Offset(x = -1000f, y = 0f))

            // then: 이동은 일어났지만(음의 방향), offset이 더 커지지 않고 이전 한계 지점에 그대로 멈춰 있다(clamp)
            assertThat(clampedOffset.x).isNegative()
            assertThatOffset(pinchZoomState.offset).isAt(x = clampedOffset.x, y = clampedOffset.y)
        }
    }

    @Nested
    @DisplayName("이미지를 더블탭할 때")
    inner class WhenDoubleTapping {
        @Test
        fun `원본 배율에서 중앙을 더블탭하면 최대 배율로 확대된다`() {
            // given: 원본 배율의 핀치줌
            val pinchZoomState = pinchZoomOnScreen()

            // when: 화면 중앙을 더블탭하면
            pinchZoomState.doubleTapZoom(tapOffset = center())

            // then: 최대 배율로 확대되고, 중앙 기준이라 치우침이 없다
            assertThat(pinchZoomState.scale).isEqualTo(pinchZoomState.maxScale)
            assertThatOffset(pinchZoomState.offset).isAt(x = 0f, y = 0f)
        }

        @Test
        fun `더블탭 지점이 화면을 벗어나도 offset이 허용 범위를 넘지 않는다`() {
            // given: 같은 조건(원본 배율)의 두 핀치줌
            val cornerTapped = pinchZoomOnScreen()
            val beyondCornerTapped = pinchZoomOnScreen()

            // when: 하나는 화면 모서리를, 다른 하나는 화면 밖 더 먼 지점을 더블탭해 확대한다
            cornerTapped.doubleTapZoom(tapOffset = Offset(x = 0f, y = 0f))
            beyondCornerTapped.doubleTapZoom(tapOffset = Offset(x = -1000f, y = -1000f))

            // then: 탭 지점 방향으로 이동하되, 더 극단적인 지점이어도 같은 한계에서 멈춘다(clamp)
            assertThat(cornerTapped.offset.x).isPositive()
            assertThatOffset(beyondCornerTapped.offset).isAt(x = cornerTapped.offset.x, y = cornerTapped.offset.y)
        }

        @Test
        fun `확대된 상태에서 더블탭하면 원본 배율로 돌아오고 치우침이 사라진다`() {
            // given: 확대하며 한쪽으로 치우쳐 둔 핀치줌
            val pinchZoomState = pinchZoomOnScreen()
            pinchZoomState.zoom(zoomChange = 2f, panChange = Offset(x = 150f, y = 0f))

            // when: 더블탭하면
            pinchZoomState.doubleTapZoom(tapOffset = Offset(x = 10f, y = 10f))

            // then: 원본 배율로 돌아오고 치우침이 사라진다
            assertThat(pinchZoomState.scale).isEqualTo(pinchZoomState.minScale)
            assertThatOffset(pinchZoomState.offset).isAt(x = 0f, y = 0f)
        }

        @Test
        fun `확대량이 허용오차 이내면 더블탭은 원본이 아니라 최대 배율로 확대한다`() {
            // given: 최소 배율 + 허용오차 = "확대되지 않은 것으로 간주"되는 경계까지만 확대한 핀치줌
            val pinchZoomState = pinchZoomOnScreen()
            pinchZoomState.zoom(zoomChange = 1f + PinchZoomDefaults.ZoomTolerance, panChange = Offset.Zero)
            assertThat(pinchZoomState.isZoomedIn).isFalse()

            // when: 더블탭하면
            pinchZoomState.doubleTapZoom(tapOffset = center())

            // then: 원본이 아니라 최대 배율로 확대된다
            assertThat(pinchZoomState.scale).isEqualTo(pinchZoomState.maxScale)
        }

        @Test
        fun `확대량이 허용오차를 넘으면 더블탭은 원본 배율로 되돌린다`() {
            // given: 허용오차를 넘겨 "확대된 상태"로 간주되는 핀치줌
            val pinchZoomState = pinchZoomOnScreen()
            pinchZoomState.zoom(zoomChange = 1f + PinchZoomDefaults.ZoomTolerance * 2f, panChange = Offset.Zero)
            assertThat(pinchZoomState.isZoomedIn).isTrue()

            // when: 더블탭하면
            pinchZoomState.doubleTapZoom(tapOffset = center())

            // then: 원본 배율로 되돌린다
            assertThat(pinchZoomState.scale).isEqualTo(pinchZoomState.minScale)
        }

        @Test
        fun `화면 크기가 측정되기 전에 더블탭해도 크래시 없이 최대 배율로 확대된다`() {
            // given: containerSize가 아직 설정되지 않은(Zero) 핀치줌
            val pinchZoomState = PinchZoomState(minScale = 1f, maxScale = 2f)

            // when: 더블탭하면
            pinchZoomState.doubleTapZoom(tapOffset = Offset(x = 100f, y = 100f))

            // then: 크래시 없이 최대 배율로 확대되고 치우침이 없다
            assertThat(pinchZoomState.scale).isEqualTo(pinchZoomState.maxScale)
            assertThatOffset(pinchZoomState.offset).isAt(x = 0f, y = 0f)
        }
    }

    /** 확대·감속 계산의 부동소수점 오차와 `Offset`의 비트 동등성을 피해, 화면상 위치(픽셀)를 허용오차로 검증한다. */
    private fun assertThatOffset(actual: Offset) =
        object {
            fun isAt(
                x: Float,
                y: Float,
            ) {
                assertThat(actual.x).isCloseTo(x, within(POSITION_TOLERANCE))
                assertThat(actual.y).isCloseTo(y, within(POSITION_TOLERANCE))
            }
        }

    companion object {
        private const val SCREEN = 1000
        private const val POSITION_TOLERANCE = 0.001f
    }
}
