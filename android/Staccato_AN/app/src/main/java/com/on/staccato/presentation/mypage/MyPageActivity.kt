package com.on.staccato.presentation.mypage

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import com.on.staccato.R
import com.on.staccato.databinding.ActivityMypageBinding
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.mypage.viewmodel.MyPageViewModel
import com.on.staccato.presentation.util.showToast
import com.on.staccato.presentation.webview.WebViewActivity
import com.on.staccato.presentation.webview.WebViewActivity.Companion.EXTRA_TOOLBAR_TITLE
import com.on.staccato.presentation.webview.WebViewActivity.Companion.EXTRA_URL
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageActivity :
    BindingActivity<ActivityMypageBinding>(),
    MyPageMenuHandler {
    override val layoutResourceId: Int = R.layout.activity_mypage
    private val myPageViewModel: MyPageViewModel by viewModels()
    private val clipboardManager: ClipboardManager by lazy {
        getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    }

    override fun initStartView(savedInstanceState: Bundle?) {
        initToolbar()
        initBindings()
        loadMemberProfile()
        observeCopyingUuidCode()
        observeErrorMessage()
    }

    override fun onPrivacyPolicyClicked() {
        val intent =
            Intent(this, WebViewActivity::class.java)
                .putExtra(EXTRA_URL, PRIVACY_POLICY_URL)
                .putExtra(EXTRA_TOOLBAR_TITLE, getString(R.string.mypage_privacy_policy))
        startActivity(intent)
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

    private fun initToolbar() {
        binding.toolbarMypage.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initBindings() {
        binding.lifecycleOwner = this
        binding.menuHandler = this
        binding.viewModel = myPageViewModel
        binding.memberProfileHandler = myPageViewModel
    }

    private fun loadMemberProfile() {
        myPageViewModel.fetchMemberProfile()
    }

    private fun observeCopyingUuidCode() {
        myPageViewModel.uuidCode.observe(this) { code ->
            copyUuidCodeOnClipBoard(code)
        }
    }

    private fun copyUuidCodeOnClipBoard(code: String) {
        val clipData: ClipData = ClipData.newPlainText(UUID_CODE_LABEL, code)
        clipboardManager.setPrimaryClip(clipData)
        showCopySuccessMessageBySdkVersion()
    }

    private fun showCopySuccessMessageBySdkVersion() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            showToast(getString(R.string.all_clipboard_copy))
        }
    }

    private fun observeErrorMessage() {
        myPageViewModel.errorMessage.observe(this) { errorMessage ->
            showToast(errorMessage)
        }
    }

    companion object {
        private const val UUID_CODE_LABEL = "uuidCode"
        private const val PRIVACY_POLICY_URL =
            "https://app.websitepolicies.com/policies/view/7jel2uwv"
        private const val FEEDBACK_GOOGLE_FORM_URL =
            "https://forms.gle/fuxgta7HxDNY5KvSA"
        private const val STACCATO_INSTAGRAM_URL =
            "https://www.instagram.com/staccato_team/profilecard/?igsh=Y241bHoybnZmZjA5"
    }
}
