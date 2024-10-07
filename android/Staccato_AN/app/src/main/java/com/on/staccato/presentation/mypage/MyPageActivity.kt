package com.on.staccato.presentation.mypage

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
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
        loadMyProfile()
        observeCopyingUuidCode()
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
        binding.myPageHandler = myPageViewModel
    }

    private fun loadMyProfile() {
        myPageViewModel.fetchMyProfile()
    }

    private fun observeCopyingUuidCode() {
        myPageViewModel.uuidCode.observe(this) { code ->
            copyUuidCodeOnClipBoard(code)
            showToast(getString(R.string.all_clipboard_copy))
        }
    }

    private fun copyUuidCodeOnClipBoard(code: String) {
        val clipData: ClipData = ClipData.newPlainText(UUID_CODE_LABEL, code)
        clipboardManager.setPrimaryClip(clipData)
    }

    override fun onPrivacyPolicyClicked() {
        val url = getString(R.string.mypage_privacy_policy_url)
        val intent =
            Intent(this, WebViewActivity::class.java)
                .putExtra(EXTRA_URL, url)
                .putExtra(EXTRA_TOOLBAR_TITLE, getString(R.string.mypage_privacy_policy))
        startActivity(intent)
    }

    companion object {
        private const val UUID_CODE_LABEL = "uuidCode"
    }
}
