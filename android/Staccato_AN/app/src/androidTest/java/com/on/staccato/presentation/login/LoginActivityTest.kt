package com.on.staccato.presentation.login

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.on.staccato.presentation.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.core.AllOf.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginActivityTest() {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    private val startButton: ViewInteraction = onView(withId(R.id.btn_login_start_staccato))
    private val nicknameInputLayout: ViewInteraction =
        onView(withId(R.id.text_input_login_user_nickname))
    private val nicknameInputEditText: ViewInteraction =
        onView(withId(R.id.edit_text_login_user_nickname))

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun `닉네임을_입력하지_않으면_시작하기_버튼이_비활성화_상태이다`() {
        startButton.check(matches(isNotEnabled()))
    }

    @Test
    fun `공백만으로_이루어진_문자열을_입력하면_닉네임_입력_란에서_에러_메세지를_보여준다`() {
        // when
        nicknameInputEditText.perform(replaceText(" "))

        // then
        nicknameInputLayout
            .check(
                matches(
                    allOf(
                        hasDescendant(
                            withText(R.string.login_nickname_error_message_blank_first),
                        ),
                        isDisplayed(),
                    ),
                ),
            )
    }

    @Test
    fun `공백으로_시작하는_문자열을_입력하면_닉네임_입력_란에서_에러_메세지를_보여준다`() {
        // when
        nicknameInputEditText.perform(replaceText("  nickname"))

        // then
        nicknameInputLayout
            .check(
                matches(
                    allOf(
                        hasDescendant(
                            withText(R.string.login_nickname_error_message_blank_first),
                        ),
                        isDisplayed(),
                    ),
                ),
            )
    }

    @Test
    fun `잘못된_형식의_닉네임을_입력하면_닉네임_입력_란에서_에러_메세지를_보여준다`() {
        // when
        nicknameInputEditText.perform(replaceText("!valid"))

        // then
        nicknameInputLayout
            .check(
                matches(
                    allOf(
                        hasDescendant(
                            withText(R.string.login_nickname_error_message_format),
                        ),
                        isDisplayed(),
                    ),
                ),
            )
    }

    @Test
    fun `올바른_형식의_닉네임을_입력하면_에러_메세지가_나타나지_않는다`() {
        // when
        nicknameInputEditText.perform(replaceText("valid"))

        // then
        nicknameInputLayout
            .check(
                matches(
                    allOf(
                        hasDescendant(
                            withText(""),
                        ),
                        isDisplayed(),
                    ),
                ),
            )
    }

    @Test
    fun `올바르지_않은_닉네임을_입력하면_시작하기_버튼이_비활성화_상태이다`() {
        // when
        nicknameInputEditText.perform(replaceText(" !valid"))

        // then
        startButton.check(matches(isNotEnabled()))
    }

    @Test
    fun `올바른_형식의_닉네임을_입력하면_시작하기_버튼이_활성화_상태이다`() {
        // when
        nicknameInputEditText.perform(replaceText("._스타카토 짱_."))

        // then
        startButton.check(matches(isEnabled()))
    }
}
