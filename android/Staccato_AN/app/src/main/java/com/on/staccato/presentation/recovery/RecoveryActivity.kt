package com.on.staccato.presentation.recovery

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import com.google.android.material.snackbar.Snackbar
import com.on.staccato.R
import com.on.staccato.databinding.ActivityRecoveryBinding
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.main.MainActivity
import com.on.staccato.presentation.recovery.viewmodel.RecoveryViewModel
import com.on.staccato.presentation.util.showSnackBarWithAction
import com.on.staccato.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecoveryActivity : BindingActivity<ActivityRecoveryBinding>() {
    override val layoutResourceId: Int = R.layout.activity_recovery

    private val recoveryViewModel: RecoveryViewModel by viewModels()

    override fun initStartView(savedInstanceState: Bundle?) {
        setBinding()
        observeViewModel()
        navigateToLogin()
    }

    private fun setBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = recoveryViewModel
        binding.handler = recoveryViewModel
    }

    private fun observeViewModel() {
        recoveryViewModel.isRecoverySuccess.observe(this, ::checkRecoverySuccess)
        recoveryViewModel.errorMessage.observe(this, ::showToast)
        recoveryViewModel.exception.observe(this) { state ->
            binding.root.showSnackBarWithAction(
                message = getString(state.messageId),
                actionLabel = R.string.all_retry,
                onAction = { recoveryViewModel.onRecoveryClicked() },
                length = Snackbar.LENGTH_INDEFINITE,
            )
        }
    }

    private fun checkRecoverySuccess(success: Boolean) {
        if (success) {
            showToast(RECOVERY_SUCCESS_MESSAGE)
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

    private fun navigateToLogin() {
        binding.toolbarRecovery.setNavigationOnClickListener {
            finish()
        }
    }

    companion object {
        private const val RECOVERY_SUCCESS_MESSAGE = "이전 데이터를 불러오는데 성공했어요!"
    }
}
