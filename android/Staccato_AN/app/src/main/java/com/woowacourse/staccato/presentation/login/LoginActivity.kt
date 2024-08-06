package com.woowacourse.staccato.presentation.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import com.woowacourse.staccato.R
import com.woowacourse.staccato.StaccatoApplication
import com.woowacourse.staccato.databinding.ActivityLoginBinding
import com.woowacourse.staccato.presentation.login.viewmodel.LoginViewModel
import com.woowacourse.staccato.presentation.login.viewmodel.LoginViewModelFactory
import com.woowacourse.staccato.presentation.main.MainActivity
import com.woowacourse.staccato.presentation.util.showToast

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory()
    }
    private var isLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        setSplashScreenContinuation(splashScreen)
        routeToNextScreenByCheckingToken()
        setBinding()
        observeLoginState()
        observeErrorEvent()
    }

    private fun setSplashScreenContinuation(splashScreen: SplashScreen) {
        splashScreen.setKeepOnScreenCondition {
            val token = StaccatoApplication.userInfoPrefsManager.getToken()
            isLoggedIn = !token.isNullOrEmpty()
            false
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
        startActivity(intent)
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
    }
}
