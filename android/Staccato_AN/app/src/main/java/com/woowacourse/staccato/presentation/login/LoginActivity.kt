package com.woowacourse.staccato.presentation.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.woowacourse.staccato.R
import com.woowacourse.staccato.StaccatoApplication.Companion.userInfoPrefsManager
import com.woowacourse.staccato.databinding.ActivityLoginBinding
import com.woowacourse.staccato.presentation.login.viewmodel.LoginViewModel
import com.woowacourse.staccato.presentation.login.viewmodel.LoginViewModelFactory
import com.woowacourse.staccato.presentation.main.MainActivity
import com.woowacourse.staccato.presentation.util.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory()
    }
    private var isLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        checkIfLoggedIn(splashScreen)
        setBinding()
        observeLoginState()
        observeErrorEvent()
    }

    private fun checkIfLoggedIn(splashScreen: SplashScreen) {
        splashScreen.setKeepOnScreenCondition { true }
        lifecycleScope.launch {
            val token = userInfoPrefsManager.getToken()
            isLoggedIn = !token.isNullOrEmpty()
            routeToNextScreenByCheckingToken()
            delay(SPLASH_SCREEN_DURATION)
            splashScreen.setKeepOnScreenCondition { false }
        }
    }

    private fun routeToNextScreenByCheckingToken() {
        if (isLoggedIn) {
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
        startActivity(intent, options.toBundle())
        finish()
    }

    private fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.viewModel = loginViewModel
        binding.handler = loginViewModel
    }

    private fun observeErrorEvent() {
        loginViewModel.errorMessage.observe(this, ::showToast)
    }

    private fun observeLoginState() {
        loginViewModel.isLoginSuccess.observe(this, ::checkLoginSuccess)
    }

    private fun checkLoginSuccess(success: Boolean) {
        if (success) {
            showToast(LOGIN_SUCCESS_MESSAGE)
            navigateToMainActivity()
        }
    }

    companion object {
        private const val LOGIN_SUCCESS_MESSAGE = "스타카토에 찾아오신걸 환영해요!"
        private const val SPLASH_SCREEN_DURATION = 1500L
    }
}
