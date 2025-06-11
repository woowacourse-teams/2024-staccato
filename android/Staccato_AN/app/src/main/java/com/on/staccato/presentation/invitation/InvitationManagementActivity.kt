package com.on.staccato.presentation.invitation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.on.staccato.presentation.invitation.viewmodel.InvitationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InvitationManagementActivity : ComponentActivity() {
    private val invitationViewModel by viewModels<InvitationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showInvitationManagementScreen()
        collectHasInvitationAccepted()
        finishOnBackPressed()
    }

    private fun showInvitationManagementScreen() {
        setContent {
            InvitationManagementScreen(
                onNavigationClick = { finish() },
            )
        }
    }

    private fun collectHasInvitationAccepted() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                invitationViewModel.hasInvitationAccepted.collect { hasInvitationAccepted ->
                    if (hasInvitationAccepted) setResult(RESULT_INVITATION_ACCEPTED)
                }
            }
        }
    }

    private fun finishOnBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }
    }

    companion object {
        const val RESULT_INVITATION_ACCEPTED = 100

        fun startWithResultLauncher(
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, InvitationManagementActivity::class.java).apply {
                activityLauncher.launch(this)
            }
        }
    }
}
