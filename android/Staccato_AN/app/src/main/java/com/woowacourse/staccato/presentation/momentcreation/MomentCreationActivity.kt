package com.woowacourse.staccato.presentation.momentcreation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.ActivityVisitCreationBinding
import com.woowacourse.staccato.presentation.base.BindingActivity
import com.woowacourse.staccato.presentation.common.PhotoAttachFragment
import com.woowacourse.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.woowacourse.staccato.presentation.moment.MomentFragment.Companion.MOMENT_ID_KEY
import com.woowacourse.staccato.presentation.momentcreation.adapter.PhotoAttachAdapter
import com.woowacourse.staccato.presentation.momentcreation.viewmodel.MomentCreationViewModel
import com.woowacourse.staccato.presentation.momentcreation.viewmodel.MomentCreationViewModelFactory
import com.woowacourse.staccato.presentation.util.showToast

class MomentCreationActivity :
    BindingActivity<ActivityVisitCreationBinding>(),
    OnUrisSelectedListener,
    MomentCreationHandler {
    override val layoutResourceId = R.layout.activity_visit_creation
    private val viewModel: MomentCreationViewModel by viewModels { MomentCreationViewModelFactory() }

    private val photoAttachFragment = PhotoAttachFragment()
    private val fragmentManager: FragmentManager = supportFragmentManager
    private lateinit var adapter: PhotoAttachAdapter
    private val memoryId by lazy { intent.getLongExtra(MEMORY_ID_KEY, 0L) }
    private val memoryTitle by lazy { intent.getStringExtra(MEMORY_TITLE_KEY) ?: "" }

    override fun initStartView(savedInstanceState: Bundle?) {
        initMemoryInfo()
        initBinding()
        initAdapter()
        initToolbar()
        observeViewModelData()
        setHidingKeyboardAction()
    }

    private fun initMemoryInfo() {
        viewModel.initMemoryInfo(memoryId, memoryTitle)
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.visitCreationHandler = this
    }

    private fun initAdapter() {
        adapter = PhotoAttachAdapter(viewModel)
        binding.rvPhotoAttach.adapter = adapter
        viewModel.selectedImages.observe(this) { uris ->
            adapter.submitList(listOf(Uri.parse("TEMP_URI_STRING")).plus(uris.toList()))
        }
    }

    private fun initToolbar() {
        binding.toolbarVisitCreation.setNavigationOnClickListener {
            finish()
        }
    }

    private fun observeViewModelData() {
        viewModel.isAddPhotoClicked.observe(this) {
            if (it) photoAttachFragment.show(fragmentManager, PhotoAttachFragment.TAG)
        }
        viewModel.createdMomentId.observe(this) { createdMomentId ->
            val resultIntent =
                Intent()
                    .putExtra(MOMENT_ID_KEY, createdMomentId)
                    .putExtra(MEMORY_ID_KEY, memoryId)
                    .putExtra(MEMORY_TITLE_KEY, memoryTitle)
            setResult(RESULT_OK, resultIntent)
            window.clearFlags(FLAG_NOT_TOUCHABLE)
            finish()
        }
        viewModel.errorMessage.observe(this) {
            window.clearFlags(FLAG_NOT_TOUCHABLE)
            showToast(it)
        }
    }

    override fun onUrisSelected(vararg uris: Uri) {
        viewModel.updateSelectedImageUris(arrayOf(*uris))
    }

    override fun onCreateDoneClicked() {
        window.setFlags(FLAG_NOT_TOUCHABLE, FLAG_NOT_TOUCHABLE)
        showToast(getString(R.string.visit_creation_posting))
        viewModel.createMoment(memoryId, this)
    }

    private fun setHidingKeyboardAction() {
        binding.root.setOnClickListener {
            hideKeyboard()
        }
    }

    private fun hideKeyboard() {
        if (this.currentFocus != null) {
            val inputManager: InputMethodManager =
                this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                this.currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS,
            )
        }
    }

    companion object {
        const val MEMORY_TITLE_KEY = "memoryTitle"

        fun startWithResultLauncher(
            memoryId: Long,
            memoryTitle: String,
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, MomentCreationActivity::class.java).apply {
                putExtra(MEMORY_ID_KEY, memoryId)
                putExtra(MEMORY_TITLE_KEY, memoryTitle)
                activityLauncher.launch(this)
            }
        }
    }
}
