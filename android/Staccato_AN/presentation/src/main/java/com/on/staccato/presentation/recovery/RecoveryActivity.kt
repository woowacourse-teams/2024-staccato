package com.on.staccato.presentation.recovery

import android.os.Bundle
import androidx.activity.viewModels
import com.on.staccato.presentation.R
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.common.MessageEvent
import com.on.staccato.presentation.databinding.ActivityRecoveryBinding
import com.on.staccato.presentation.main.MainActivity
import com.on.staccato.presentation.recovery.viewmodel.RecoveryViewModel
import com.on.staccato.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecoveryActivity : BindingActivity<ActivityRecoveryBinding>() {
    override val layoutResourceId: Int = R.layout.activity_recovery

    private val recoveryViewModel: RecoveryViewModel by viewModels()

    override fun initStartView(savedInstanceState: Bundle?) {
        setBindings()
        observeRecoverySuccess()
        observeMessageEvent()
    }

    private fun setBindings() {
        binding.lifecycleOwner = this
        binding.viewModel = recoveryViewModel
        binding.handler = recoveryViewModel
        binding.toolbarRecovery.setNavigationOnClickListener { finish() }
    }

    private fun observeRecoverySuccess() {
        recoveryViewModel.isRecoverySuccess.observe(this, ::checkRecoverySuccess)
    }

    private fun checkRecoverySuccess(success: Boolean) {
        if (success) {
            showToast(getString(R.string.recovery_success))
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        recoveryViewModel.registerCurrentFcmToken()
        MainActivity.launch(startContext = this)
        finish()
    }

    private fun observeMessageEvent() {
        recoveryViewModel.messageEvent.observe(this) { event ->
            when (event) {
                is MessageEvent.ResId -> showToast(getString(event.messageId))
                is MessageEvent.Text -> showToast(event.message)
            }
        }
    }
}
