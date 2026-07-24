package com.on.staccato.presentation.component

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * 핀치줌 화면의 상태(배율 · 위치)가 사용자 요구사항대로 동작하는지 검증한다.
 *
 * 시나리오는 구현이 아니라 "사용자가 화면에서 무엇을 하고, 그 결과 무엇을 보게 되는가"의 관점으로 작성한다.
 * - 배율(scale): 이미지가 얼마나 커 보이는가 (1f = 원본, 2f = 2배)
 * - 위치(offset): 이미지가 화면 중앙에서 얼마나 치우쳐 보이는가 (중앙 = 0, 0)
 *
 * 위치는 확대·감속 계산을 거치며 부동소수점 오차가 섞일 수 있고, `Offset`의 동등성은 비트 단위 비교이므로
 * "사용자가 보게 되는 위치(픽셀)"를 허용오차로 검증한다.
 */
class PinchZoomStateTest {
    /** 1000 x 1000 화면에 원본~2배 범위의 핀치줌 상태를 놓는다. */
    private fun pinchZoomOnScreen() =
        PinchZoomState(minScale = 1f, maxScale = 2f)
            .apply { containerSize = IntSize(SCREEN, SCREEN) }

    @Nested
    @DisplayName("처음 화면을 열었을 때")
    inner class WhenOpened {
        @Test
        fun `이미지는 원본 크기로 화면 정중앙에 보인다`() {
            val pinchZoomState = pinchZoomOnScreen()

            assertThat(pinchZoomState.scale).isEqualTo(1f)
            assertThatOffset(pinchZoomState.offset).isAt(x = 0f, y = 0f)
        }
    }

    @Nested
    @DisplayName("두 손가락으로 확대·축소할 때")
    inner class WhenPinching {
        @Test
        fun `두 손가락을 벌린 만큼 이미지가 확대된다`() {
            val pinchZoomState = pinchZoomOnScreen()

            pinchZoomState.zoom(zoomChange = 1.5f, panChange = Offset.Zero)

            assertThat(pinchZoomState.scale).isEqualTo(1.5f)
        }

        @Test
        fun `아무리 크게 벌려도 최대 2배까지만 확대된다`() {
            val pinchZoomState = pinchZoomOnScreen()

            pinchZoomState.zoom(zoomChange = 5f, panChange = Offset.Zero)
            pinchZoomState.zoom(zoomChange = 5f, panChange = Offset.Zero)

            assertThat(pinchZoomState.scale).isEqualTo(2f)
        }

        @Test
        fun `원본 크기에서 두 손가락을 오므려도 원본보다 작아지지 않는다`() {
            val pinchZoomState = pinchZoomOnScreen()

            pinchZoomState.zoom(zoomChange = 0.5f, panChange = Offset.Zero)

            assertThat(pinchZoomState.scale).isEqualTo(1f)
            assertThatOffset(pinchZoomState.offset).isAt(x = 0f, y = 0f)
        }

        @Test
        fun `확대하면서 손을 움직이면 이미지가 그 방향으로 함께 이동한다`() {
            val pinchZoomState = pinchZoomOnScreen()

            pinchZoomState.zoom(zoomChange = 2f, panChange = Offset(x = 100f, y = 0f))

            assertThat(pinchZoomState.scale).isEqualTo(2f)
            assertThatOffset(pinchZoomState.offset).isAt(x = 200f, y = 0f)
        }

        @Test
        fun `확대했던 이미지를 다시 원본으로 오므리면 화면이 정중앙으로 돌아온다`() {
            val pinchZoomState = pinchZoomOnScreen()
            pinchZoomState.zoom(zoomChange = 2f, panChange = Offset.Zero)
            pinchZoomState.pan(dragAmount = Offset(x = -100f, y = 0f)) // 확대 상태에서 한쪽으로 치우쳐 둔다

            pinchZoomState.zoom(zoomChange = 0.4f, panChange = Offset.Zero) // 원본 크기로 축소

            assertThat(pinchZoomState.scale).isEqualTo(1f)
            assertThatOffset(pinchZoomState.offset).isAt(x = 0f, y = 0f)
        }
    }

    @Nested
    @DisplayName("한 손가락으로 이미지를 드래그할 때")
    inner class WhenDragging {
        @Test
        fun `원본 크기에서는 드래그해도 이미지가 움직이지 않는다`() {
            val pinchZoomState = pinchZoomOnScreen()

            pinchZoomState.pan(dragAmount = Offset(x = 100f, y = 100f))

            assertThatOffset(pinchZoomState.offset).isAt(x = 0f, y = 0f)
        }

        @Test
        fun `확대된 상태에서 드래그하면 이미지가 그 방향으로 이동한다`() {
            val pinchZoomState = pinchZoomOnScreen()
            pinchZoomState.zoom(zoomChange = 2f, panChange = Offset.Zero)

            pinchZoomState.pan(dragAmount = Offset(x = -100f, y = 0f))

            // 왼쪽(음의 방향)으로 이동하고, 손가락 이동량(100)보다 크게 벗어나지 않는다(감속)
            assertThat(pinchZoomState.offset.x).isNegative()
            assertThatOffset(pinchZoomState.offset).isAt(x = -160f, y = 0f)
        }

        @Test
        fun `확대된 이미지를 아무리 밀어도 이미지 밖 빈 여백이 보이지 않는다`() {
            val pinchZoomState = pinchZoomOnScreen()
            pinchZoomState.zoom(zoomChange = 2f, panChange = Offset.Zero)

            pinchZoomState.pan(dragAmount = Offset(x = -1000f, y = 0f))
            pinchZoomState.pan(dragAmount = Offset(x = -1000f, y = 0f))

            // 2배 확대 시 좌우로 밀 수 있는 최대치 = 화면너비 * (2-1) / 2 = 500
            assertThatOffset(pinchZoomState.offset).isAt(x = -500f, y = 0f)
        }
    }

    @Nested
    @DisplayName("이미지를 더블탭할 때")
    inner class WhenDoubleTapping {
        @Test
        fun `원본 크기에서 중앙을 더블탭하면 중앙 기준으로 최대 배율까지 확대된다`() {
            val pinchZoomState = pinchZoomOnScreen()

            pinchZoomState.doubleTapZoom(tapOffset = Offset(x = 500f, y = 500f))

            assertThat(pinchZoomState.scale).isEqualTo(2f)
            assertThatOffset(pinchZoomState.offset).isAt(x = 0f, y = 0f)
        }

        @Test
        fun `원본 크기에서 모서리를 더블탭해도 이미지 밖 빈 여백이 보이지 않는다`() {
            val pinchZoomState = pinchZoomOnScreen()

            pinchZoomState.doubleTapZoom(tapOffset = Offset(x = 0f, y = 0f))

            assertThat(pinchZoomState.scale).isEqualTo(2f)
            assertThatOffset(pinchZoomState.offset).isAt(x = 500f, y = 500f)
        }

        @Test
        fun `확대된 이미지를 더블탭하면 원본 크기로 돌아오고 정중앙으로 정렬된다`() {
            val pinchZoomState = pinchZoomOnScreen()
            pinchZoomState.zoom(zoomChange = 2f, panChange = Offset(x = 150f, y = 0f))

            pinchZoomState.doubleTapZoom(tapOffset = Offset(x = 10f, y = 10f))

            assertThat(pinchZoomState.scale).isEqualTo(1f)
            assertThatOffset(pinchZoomState.offset).isAt(x = 0f, y = 0f)
        }

        @Test
        fun `거의 원본에 가깝게만 확대된 상태에서 더블탭하면 축소가 아니라 최대 배율로 확대된다`() {
            val pinchZoomState = pinchZoomOnScreen()
            pinchZoomState.zoom(zoomChange = 1.005f, panChange = Offset.Zero) // 사용자가 눈치채기 어려운 미세 확대
            assertThat(pinchZoomState.isZoomedIn).isFalse() // 아직 "확대한 상태"로 간주되지 않는다

            pinchZoomState.doubleTapZoom(tapOffset = Offset(x = 500f, y = 500f))

            assertThat(pinchZoomState.scale).isEqualTo(2f)
        }

        @Test
        fun `또렷하게 확대된 상태에서 더블탭하면 원본 크기로 돌아온다`() {
            val pinchZoomState = pinchZoomOnScreen()
            pinchZoomState.zoom(zoomChange = 1.05f, panChange = Offset.Zero)
            assertThat(pinchZoomState.isZoomedIn).isTrue() // "확대한 상태"로 간주된다

            pinchZoomState.doubleTapZoom(tapOffset = Offset(x = 500f, y = 500f))

            assertThat(pinchZoomState.scale).isEqualTo(1f)
        }

        @Test
        fun `화면 크기가 아직 측정되지 않았어도 더블탭하면 앱이 죽지 않고 최대 배율로 확대된다`() {
            val pinchZoomState = PinchZoomState(minScale = 1f, maxScale = 2f) // containerSize 미설정(Zero)

            pinchZoomState.doubleTapZoom(tapOffset = Offset(x = 100f, y = 100f))

            assertThat(pinchZoomState.scale).isEqualTo(2f)
            assertThatOffset(pinchZoomState.offset).isAt(x = 0f, y = 0f)
        }
    }

    /** "사용자가 보게 되는 위치(픽셀)"를 허용오차로 검증하기 위한 단언 헬퍼. */
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
