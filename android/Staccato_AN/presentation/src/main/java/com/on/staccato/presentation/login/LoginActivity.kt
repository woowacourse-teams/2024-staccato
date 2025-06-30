package com.on.staccato.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.on.staccato.presentation.R
import com.on.staccato.presentation.common.MessageEvent
import com.on.staccato.presentation.databinding.ActivityLoginBinding
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
        setBindings()
        observeLoginState()
        observeMessageEvent()
    }

    override fun onStartClicked() {
        window.setFlags(FLAG_NOT_TOUCHABLE, FLAG_NOT_TOUCHABLE)
        loginViewModel.requestLogin()
    }

    override fun onScreenClicked() {
        hideKeyboardAndClearFocus()
    }

    override fun onRecoveryClicked() {
        navigateToRecoveryActivity()
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
        loginViewModel.registerCurrentFcmToken()
        MainActivity.launch(startContext = this)
        finish()
    }

    private fun setBindings() {
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
            showToast(getString(R.string.login_success))
            navigateToMainActivity()
            window.clearFlags(FLAG_NOT_TOUCHABLE)
        }
    }

    private fun observeMessageEvent() {
        loginViewModel.messageEvent.observe(this) { event ->
            when (event) {
                is MessageEvent.FromResource -> showToast(getString(event.messageId))
                is MessageEvent.Plain -> showToast(event.message)
            }
            window.clearFlags(FLAG_NOT_TOUCHABLE)
        }
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

    private fun navigateToRecoveryActivity() {
        val intent = Intent(this, RecoveryActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val SPLASH_SCREEN_DURATION = 1500L
    }
}
