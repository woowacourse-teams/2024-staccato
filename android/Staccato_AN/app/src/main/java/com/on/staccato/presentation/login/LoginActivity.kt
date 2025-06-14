package com.on.staccato.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.on.staccato.R
import com.on.staccato.databinding.ActivityLoginBinding
import com.on.staccato.presentation.login.viewmodel.LoginViewModel
import com.on.staccato.presentation.main.MainActivity
import com.on.staccato.presentation.recovery.RecoveryActivity
import com.on.staccato.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), LoginHandler {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    private val inputManager: InputMethodManager by lazy {
        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        checkIfLoggedIn(splashScreen)
        setBinding()
        observeLoginState()
        observeErrorEvent()
    }

    override fun onStartClicked() {
        window.setFlags(FLAG_NOT_TOUCHABLE, FLAG_NOT_TOUCHABLE)
        loginViewModel.requestLogin()
    }

    override fun onScreenClicked() {
        hideKeyboardAndClearFocus()
    }

    private fun hideKeyboardAndClearFocus() {
        currentFocus?.let {
            inputManager.hideSoftInputFromWindow(
                it.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS,
            )
            it.clearFocus()
        }
    }

    override fun onRecoveryClicked() {
        navigateToRecoveryActivity()
    }

    private fun navigateToRecoveryActivity() {
        val intent = Intent(this, RecoveryActivity::class.java)
        startActivity(intent)
    }

    private fun checkIfLoggedIn(splashScreen: SplashScreen) {
        splashScreen.setKeepOnScreenCondition { true }
        loginViewModel.fetchToken()
        observeIsLoggedIn(splashScreen)
    }

    private fun observeIsLoggedIn(splashScreen: SplashScreen) {
        loginViewModel.isLoggedIn.observe(this) {
            lifecycleScope.launch {
                routeToNextScreenByCheckingToken(it)
                delay(SPLASH_SCREEN_DURATION)
                splashScreen.setKeepOnScreenCondition { false }
            }
        }
    }

    private fun routeToNextScreenByCheckingToken(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val options =
            ActivityOptionsCompat.makeCustomAnimation(
                this,
                R.anim.anim_fade_in,
                R.anim.anim_fade_out,
            )
        startActivity(intent, options.toBundle())
        finish()
    }

    private fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.viewModel = loginViewModel
        binding.handler = this
    }

    private fun observeLoginState() {
        loginViewModel.isLoginSuccess.observe(this, ::checkLoginSuccess)
    }

    private fun checkLoginSuccess(success: Boolean) {
        if (success) {
            showToast(LOGIN_SUCCESS_MESSAGE)
            navigateToMainActivity()
            window.clearFlags(FLAG_NOT_TOUCHABLE)
        }
    }

    private fun observeErrorEvent() {
        loginViewModel.errorMessage.observe(this) { errorMessage ->
            showToast(errorMessage)
            window.clearFlags(FLAG_NOT_TOUCHABLE)
        }
    }

    companion object {
        private const val LOGIN_SUCCESS_MESSAGE = "스타카토에 찾아오신걸 환영해요!"
        private const val SPLASH_SCREEN_DURATION = 2000L
    }
}
