package com.on.staccato.presentation.component

import androidx.activity.ComponentActivity
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.click
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pinch
import androidx.compose.ui.test.swipe
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

/**
 * PinchZoom 컴포저블의 제스처 인식을 Robolectric(JVM)에서 검증한다.
 *
 * 순수 상태 계산(배율·offset의 상하한·clamp)은 [PinchZoomStateTest]가 맡고, 이 클래스는
 * "실제 터치 시퀀스가 탭/드래그/핀치/더블탭으로 올바르게 분류되어 상태·콜백에 반영되는가"를 본다.
 * 상태는 hoisting한 [PinchZoomState]를 주입해 scale·offset을 직접 관찰한다.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PinchZoomGestureTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `한 손가락으로 탭하면 onTap이 호출된다`() {
        // given: 탭 콜백을 단 핀치줌
        var tappedAt: Offset? = null
        setPinchZoom(onTap = { tappedAt = it })

        // when: 한 번 탭하면
        composeRule.onNodeWithTag(PINCH_ZOOM).performTouchInput { click(center) }
        // 더블탭 대기 시간이 지나야 단일 탭으로 확정된다
        composeRule.mainClock.advanceTimeBy(DOUBLE_TAP_TIMEOUT)

        // then: onTap이 호출된다
        composeRule.runOnIdle { assertThat(tappedAt).isNotNull() }
    }

    @Test
    fun `더블탭하면 배율이 최대로 토글된다`() {
        // given: 원본 배율의 핀치줌
        val state = setPinchZoom()

        // when: 더블탭하면
        composeRule.onNodeWithTag(PINCH_ZOOM).performTouchInput { doubleClick(center) }

        // then: 최대 배율로 확대된다
        composeRule.runOnIdle { assertThat(state.scale).isEqualTo(state.maxScale) }
    }

    @Test
    fun `두 손가락을 벌리면 배율이 커진다`() {
        // given: 원본 배율의 핀치줌
        val state = setPinchZoom()

        // when: 두 손가락을 바깥으로 벌리면
        composeRule.onNodeWithTag(PINCH_ZOOM).performTouchInput {
            pinch(
                start0 = center,
                end0 = center - Offset(PINCH_SPREAD, 0f),
                start1 = center,
                end1 = center + Offset(PINCH_SPREAD, 0f),
            )
        }

        // then: 배율이 원본보다 커진다
        composeRule.runOnIdle { assertThat(state.scale).isGreaterThan(state.minScale) }
    }

    @Test
    fun `두 번의 탭 간격이 더블탭 시간을 넘으면 더블탭이 아니라 각각 단일 탭으로 처리된다`() {
        // given: 탭 횟수를 세는 핀치줌
        var tapCount = 0
        val state = setPinchZoom(onTap = { tapCount++ })

        // when: 더블탭 인정 시간보다 길게 벌려 두 번 탭한다 (clock을 수동으로 전진)
        composeRule.mainClock.autoAdvance = false
        composeRule.onNodeWithTag(PINCH_ZOOM).performTouchInput { click(center) }
        composeRule.mainClock.advanceTimeBy(DOUBLE_TAP_TIMEOUT)
        composeRule.onNodeWithTag(PINCH_ZOOM).performTouchInput { click(center) }
        composeRule.mainClock.advanceTimeBy(DOUBLE_TAP_TIMEOUT)
        composeRule.mainClock.autoAdvance = true

        // then: 확대되지 않고(더블탭 아님), 각각 단일 탭으로 두 번 처리된다
        composeRule.runOnIdle {
            assertThat(state.scale).isEqualTo(state.minScale)
            assertThat(tapCount).isEqualTo(2)
        }
    }

    @Test
    fun `핀치로 시작한 제스처는 한 손가락만 남아도 탭으로 처리되지 않는다`() {
        // given: 탭 콜백을 단 핀치줌
        var tapped = false
        val state = setPinchZoom(onTap = { tapped = true })

        // when: 두 손가락으로 확대한 뒤, 한 손가락을 떼고 남은 손가락으로 드래그한다
        composeRule.onNodeWithTag(PINCH_ZOOM).performTouchInput {
            down(0, center - Offset(50f, 0f))
            down(1, center + Offset(50f, 0f))
            moveTo(0, center - Offset(250f, 0f)) // 벌려서 확대(핀치로 확정)
            moveTo(1, center + Offset(250f, 0f))
            up(1) // 한 손가락 떼기
            moveTo(0, center - Offset(250f, 300f)) // 남은 손가락으로 드래그
            up(0)
        }

        // then: 먼저 인식된 핀치로 고정되어, 확대는 유지되고 탭으로 처리되지 않는다
        composeRule.runOnIdle {
            assertThat(state.scale).isGreaterThan(state.minScale)
            assertThat(tapped).isFalse()
        }
    }

    @Test
    fun `최소 배율에서 드래그하면 팬은 적용되지 않지만 shouldConsumeDrag는 호출된다`() {
        // given: 소비 콜백 호출 여부를 기록하는, 최소 배율의 핀치줌
        var consumeCalled = false
        val state = setPinchZoom(shouldConsumeDrag = { consumeCalled = true; false })

        // when: 확대하지 않은 상태에서 드래그하면
        composeRule.onNodeWithTag(PINCH_ZOOM).performTouchInput {
            swipe(center, center + Offset(DRAG_DISTANCE, 0f))
        }

        // then: 팬이 적용되지 않아 배율·위치는 그대로지만, 소비 여부를 정하는 콜백은 호출된다
        composeRule.runOnIdle {
            assertThat(state.scale).isEqualTo(state.minScale)
            assertThat(state.offset).isEqualTo(Offset.Zero)
            assertThat(consumeCalled).isTrue()
        }
    }

    @Test
    fun `드래그로 시작한 제스처는 도중에 손가락을 추가해도 확대로 넘어가지 않는다`() {
        // given: 원본 배율의 핀치줌
        val state = setPinchZoom()

        // when: 한 손가락 드래그(슬롭 초과)로 제스처를 연 뒤, 두 번째 손가락을 더해 벌린다
        composeRule.onNodeWithTag(PINCH_ZOOM).performTouchInput {
            down(0, center)
            moveTo(0, center + Offset(DRAG_DISTANCE, 0f)) // 드래그로 확정
            down(1, center + Offset(0f, 100f))
            moveTo(0, center + Offset(DRAG_DISTANCE + 200f, 0f))
            moveTo(1, center + Offset(0f, 300f))
            up(0)
            up(1)
        }

        // then: 먼저 인식된 드래그로 고정되어, 배율은 원본 그대로다(핀치로 넘어가지 않음)
        composeRule.runOnIdle { assertThat(state.scale).isEqualTo(state.minScale) }
    }

    @Test
    fun `shouldConsumeDrag가 true면 드래그가 소비되어 부모로 전파되지 않는다`() {
        // given: 자식이 드래그를 소비하도록 설정하고, 부모는 드래그 수신 여부를 기록한다
        var parentReceivedDrag = false
        composeRule.setContent {
            Box(
                Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { _, _ -> parentReceivedDrag = true }
                    },
            ) {
                PinchZoom(
                    state = rememberPinchZoomState(),
                    modifier = Modifier.testTag(PINCH_ZOOM),
                    shouldConsumeDrag = { true },
                ) {
                    Box(Modifier.fillMaxSize())
                }
            }
        }

        // when: 자식 위에서 드래그하면
        composeRule.onNodeWithTag(PINCH_ZOOM).performTouchInput {
            swipe(center, center + Offset(DRAG_DISTANCE, 0f))
        }

        // then: 자식이 이벤트를 소비해 부모는 드래그를 받지 못한다
        composeRule.runOnIdle { assertThat(parentReceivedDrag).isFalse() }
    }

    @Test
    fun `shouldConsumeDrag가 false면 드래그가 부모로 전파된다`() {
        // given: 자식이 드래그를 소비하지 않도록 설정한다
        var parentReceivedDrag = false
        composeRule.setContent {
            Box(
                Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { _, _ -> parentReceivedDrag = true }
                    },
            ) {
                PinchZoom(
                    state = rememberPinchZoomState(),
                    modifier = Modifier.testTag(PINCH_ZOOM),
                    shouldConsumeDrag = { false },
                ) {
                    Box(Modifier.fillMaxSize())
                }
            }
        }

        // when: 자식 위에서 드래그하면
        composeRule.onNodeWithTag(PINCH_ZOOM).performTouchInput {
            swipe(center, center + Offset(DRAG_DISTANCE, 0f))
        }

        // then: 소비하지 않으므로 부모가 드래그를 이어받는다
        composeRule.runOnIdle { assertThat(parentReceivedDrag).isTrue() }
    }

    /** hoisting한 상태를 주입한 PinchZoom을 렌더하고, 그 상태를 반환해 테스트에서 scale·offset을 관찰한다. */
    private fun setPinchZoom(
        onTap: ((Offset) -> Unit)? = null,
        shouldConsumeDrag: ((Offset) -> Boolean)? = null,
    ): PinchZoomState {
        lateinit var state: PinchZoomState
        composeRule.setContent {
            state = rememberPinchZoomState()
            PinchZoom(
                state = state,
                modifier = Modifier.testTag(PINCH_ZOOM),
                shouldConsumeDrag = shouldConsumeDrag,
                onTap = onTap,
            ) {
                Box(Modifier.fillMaxSize())
            }
        }
        return state
    }

    companion object {
        private const val PINCH_ZOOM = "pinchZoom"
        private const val DRAG_DISTANCE = 300f
        private const val PINCH_SPREAD = 200f
        private const val DOUBLE_TAP_TIMEOUT = 1_000L
    }
}
