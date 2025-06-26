package com.on.staccato.presentation.mypage

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.on.staccato.R
import com.on.staccato.databinding.ActivityMypageBinding
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.common.clipboard.ClipboardHelper
import com.on.staccato.presentation.common.photo.PhotoAttachFragment
import com.on.staccato.presentation.component.DefaultDivider
import com.on.staccato.presentation.invitation.InvitationManagementActivity
import com.on.staccato.presentation.invitation.InvitationManagementActivity.Companion.RESULT_INVITATION_ACCEPTED
import com.on.staccato.presentation.mypage.component.MyPageMenuButton
import com.on.staccato.presentation.mypage.viewmodel.MyPageViewModel
import com.on.staccato.presentation.staccatocreation.OnUrisSelectedListener
import com.on.staccato.presentation.util.IMAGE_FORM_DATA_NAME
import com.on.staccato.presentation.util.convertMyPageUriToFile
import com.on.staccato.presentation.util.showToast
import com.on.staccato.presentation.webview.WebViewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyPageActivity :
    BindingActivity<ActivityMypageBinding>(),
    MyPageHandler,
    OnUrisSelectedListener {
    override val layoutResourceId: Int = R.layout.activity_mypage
    private val myPageViewModel: MyPageViewModel by viewModels()
    private val photoAttachFragment by lazy { PhotoAttachFragment() }
    private val fragmentManager: FragmentManager = supportFragmentManager
    private val invitationManagementLauncher = handleInvitationManagementResult()

    @Inject
    lateinit var clipboardHelper: ClipboardHelper

    override fun initStartView(savedInstanceState: Bundle?) {
        setContents()
        initToolbar()
        initBindings()
        finishOnBackPressed()
        loadMemberProfile()
        observeCopyingUuidCode()
        observeErrorMessage()
        observeException()
        fetchNotifications()
    }

    override fun onProfileImageChangeClicked() {
        if (!photoAttachFragment.isAdded) {
            photoAttachFragment.show(fragmentManager, PhotoAttachFragment.TAG)
        }
    }

    override fun onUrisSelected(vararg uris: Uri) {
        val imageFile = convertMyPageUriToFile(this, uris.first(), IMAGE_FORM_DATA_NAME)
        myPageViewModel.changeProfileImage(imageFile)
    }

    override fun onPrivacyPolicyClicked() {
        WebViewActivity.launch(this, PRIVACY_POLICY_URL, getString(R.string.mypage_privacy_policy))
    }

    override fun onFeedbackClicked() {
        val intent = Intent(ACTION_VIEW).apply { data = Uri.parse(FEEDBACK_GOOGLE_FORM_URL) }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            showToast(getString(R.string.mypage_error_not_found_browser))
        }
    }

    override fun onInstagramClicked() {
        val intent = Intent(ACTION_VIEW).apply { data = Uri.parse(STACCATO_INSTAGRAM_URL) }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            showToast(getString(R.string.mypage_error_can_not_open_instagram_page))
        }
    }

    private fun setContents() {
        setCategoryInvitationManagementButtonContent()
        setDividerContent()
    }

    private fun setCategoryInvitationManagementButtonContent() {
        binding.btnMypageMenuCategoryInvitationManagement.setContent {
            val hasNotification by myPageViewModel.hasNotification.collectAsStateWithLifecycle()

            MyPageMenuButton(
                menuTitle = getString(R.string.mypage_invitation_management),
                onClick = {
                    InvitationManagementActivity.startWithResultLauncher(
                        context = this,
                        activityLauncher = invitationManagementLauncher,
                    )
                },
                hasNotification = hasNotification,
            )
        }
    }

    private fun setDividerContent() {
        binding.dividerMypageMiddle.setContent {
            DefaultDivider(thickness = 10.dp)
        }
    }

    private fun initToolbar() {
        binding.toolbarMypage.setNavigationOnClickListener {
            finishWithResult()
        }
    }

    private fun initBindings() {
        binding.lifecycleOwner = this
        binding.myPageHandler = this
        binding.viewModel = myPageViewModel
        binding.memberProfileHandler = myPageViewModel
    }

    private fun finishOnBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
            finishWithResult()
        }
    }

    private fun loadMemberProfile() {
        myPageViewModel.fetchMemberProfile()
    }

    private fun observeCopyingUuidCode() {
        myPageViewModel.uuidCode.observe(this) { code ->
            clipboardHelper.copyText(
                label = UUID_CODE_LABEL,
                text = code,
                context = this,
            )
        }
    }

    private fun observeErrorMessage() {
        myPageViewModel.errorMessage.observe(this) { errorMessage ->
            showToast(errorMessage)
        }
    }

    private fun observeException() {
        myPageViewModel.exceptionState.observe(this) { state ->
            showToast(getString(state.messageId))
        }
    }

    private fun fetchNotifications() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                myPageViewModel.fetchNotificationExistence()
            }
        }
    }

    private fun handleInvitationManagementResult() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_INVITATION_ACCEPTED) {
                myPageViewModel.updateHasTimelineUpdated()
            }
        }

    private fun finishWithResult() {
        setMyPageResult()
        finish()
    }

    private fun setMyPageResult() {
        val hasTimelineUpdated = myPageViewModel.hasTimelineUpdated
        val hasProfileUpdated = myPageViewModel.hasProfileUpdated
        val intent =
            Intent()
                .putExtra(UPDATED_TIMELINE_KEY, hasTimelineUpdated)
                .putExtra(UPDATED_PROFILE_KEY, hasProfileUpdated)
        setResult(RESULT_OK, intent)
    }

    companion object {
        const val UPDATED_TIMELINE_KEY = "hasTimelineUpdated"
        const val UPDATED_PROFILE_KEY = "hasProfileUpdated"
        private const val UUID_CODE_LABEL = "uuidCode"
        private const val PRIVACY_POLICY_URL =
            "https://app.websitepolicies.com/policies/view/7jel2uwv"
        private const val FEEDBACK_GOOGLE_FORM_URL =
            "https://forms.gle/fuxgta7HxDNY5KvSA"
        private const val STACCATO_INSTAGRAM_URL =
            "https://www.instagram.com/staccato_team/profilecard/?igsh=Y241bHoybnZmZjA5"

        fun startWithResultLauncher(
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, MyPageActivity::class.java).apply {
                activityLauncher.launch(this)
            }
        }
    }
}
