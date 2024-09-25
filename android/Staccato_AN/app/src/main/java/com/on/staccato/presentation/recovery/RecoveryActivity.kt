package com.on.staccato.presentation.recovery

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import com.on.staccato.R
import com.on.staccato.databinding.ActivityRecoveryBinding
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.main.MainActivity
import com.on.staccato.presentation.recovery.viewmodel.RecoveryViewModel
import com.on.staccato.presentation.recovery.viewmodel.RecoveryViewModelFactory
import com.on.staccato.presentation.util.showToast

class RecoveryActivity : BindingActivity<ActivityRecoveryBinding>() {
    override val layoutResourceId: Int = R.layout.activity_recovery

    private val recoveryViewModel: RecoveryViewModel by viewModels {
        RecoveryViewModelFactory()
    }

    override fun initStartView(savedInstanceState: Bundle?) {
        setBinding()
        observeViewModel()
    }

    private fun setBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = recoveryViewModel
        binding.handler = recoveryViewModel
    }

    private fun observeViewModel() {
        recoveryViewModel.isRecoverySuccess.observe(this, ::checkRecoverySuccess)
        recoveryViewModel.errorMessage.observe(this, ::showToast)
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

    companion object {
        private const val RECOVERY_SUCCESS_MESSAGE = "이전 데이터를 불러오는데 성공했어요!"
    }
}
