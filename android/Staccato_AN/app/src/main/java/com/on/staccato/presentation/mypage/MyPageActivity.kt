package com.on.staccato.presentation.mypage

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import com.on.staccato.R
import com.on.staccato.databinding.ActivityMypageBinding
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.common.PhotoAttachFragment
import com.on.staccato.presentation.mypage.viewmodel.MyPageViewModel
import com.on.staccato.presentation.staccatocreation.OnUrisSelectedListener
import com.on.staccato.presentation.util.IMAGE_FORM_DATA_NAME
import com.on.staccato.presentation.util.convertMyPageUriToFile
import com.on.staccato.presentation.util.showToast
import com.on.staccato.presentation.webview.WebViewActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageActivity :
    BindingActivity<ActivityMypageBinding>(),
    MyPageHandler,
    OnUrisSelectedListener {
    override val layoutResourceId: Int = R.layout.activity_mypage
    private val myPageViewModel: MyPageViewModel by viewModels()
    private val photoAttachFragment by lazy { PhotoAttachFragment() }
    private val fragmentManager: FragmentManager = supportFragmentManager
    private val clipboardManager: ClipboardManager by lazy {
        getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    }

    override fun initStartView(savedInstanceState: Bundle?) {
        setContents()
        initToolbar()
        initBindings()
        loadMemberProfile()
        observeMemberProfile()
        observeCopyingUuidCode()
        observeErrorMessage()
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
        setDividerContent()
    }

    private fun setDividerContent() {
        binding.dividerMypageMiddle.setContent {
            MiddleDivider()
        }
    }

    private fun initToolbar() {
        binding.toolbarMypage.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initBindings() {
        binding.lifecycleOwner = this
        binding.myPageHandler = this
        binding.viewModel = myPageViewModel
        binding.memberProfileHandler = myPageViewModel
    }

    private fun loadMemberProfile() {
        myPageViewModel.fetchMemberProfile()
    }

    private fun observeMemberProfile() {
        myPageViewModel.memberProfile.observe(this) {
            setResult(RESULT_OK)
        }
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
            finish()
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
