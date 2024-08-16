package com.woowacourse.staccato.presentation.visitupdate

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.ActivityVisitUpdateBinding
import com.woowacourse.staccato.presentation.base.BindingActivity
import com.woowacourse.staccato.presentation.common.PhotoAttachFragment
import com.woowacourse.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.woowacourse.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_TITLE_KEY
import com.woowacourse.staccato.presentation.moment.VisitFragment.Companion.MOMENT_ID_KEY
import com.woowacourse.staccato.presentation.util.showToast
import com.woowacourse.staccato.presentation.visitcreation.OnUrisSelectedListener
import com.woowacourse.staccato.presentation.visitupdate.viewmodel.VisitUpdateViewModel
import com.woowacourse.staccato.presentation.visitupdate.viewmodel.VisitUpdateViewModelFactory
import kotlinx.coroutines.launch

class VisitUpdateActivity :
    BindingActivity<ActivityVisitUpdateBinding>(),
    OnUrisSelectedListener,
    VisitUpdateHandler {
    override val layoutResourceId = R.layout.activity_visit_update
    private val viewModel: VisitUpdateViewModel by viewModels { VisitUpdateViewModelFactory() }

    private val photoAttachFragment = PhotoAttachFragment()
    private val fragmentManager: FragmentManager = supportFragmentManager

    private val visitId by lazy { intent.getLongExtra(MOMENT_ID_KEY, 0L) }
    private val memoryId by lazy { intent.getLongExtra(MEMORY_ID_KEY, 0L) }
    private val memoryTitle by lazy { intent.getStringExtra(MEMORY_TITLE_KEY) ?: "" }

    override fun initStartView(savedInstanceState: Bundle?) {
        initBinding()
        initToolbar()
        observeViewModelData()
        viewModel.initViewModelData(visitId, memoryId, memoryTitle)
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.visitUpdateHandler = this
    }

    private fun initToolbar() {
        binding.toolbarVisitUpdate.setNavigationOnClickListener {
            finish()
        }
    }

    private fun observeViewModelData() {
        viewModel.isError.observe(this) { isError ->
            handleError(isError)
        }
        viewModel.isUpdateCompleted.observe(this) { isUpdateCompleted ->
            handleUpdateComplete(isUpdateCompleted)
        }
    }

    private fun handleError(isError: Boolean) {
        if (isError) {
            window.clearFlags(FLAG_NOT_TOUCHABLE)
            showToast("방문을 수정할 수 없어요!")
            finish()
        }
    }

    private fun handleUpdateComplete(isUpdateCompleted: Boolean) {
        if (isUpdateCompleted) {
            val intent =
                Intent()
                    .putExtra(MOMENT_ID_KEY, visitId)
                    .putExtra(MEMORY_ID_KEY, memoryId)
                    .putExtra(MEMORY_TITLE_KEY, memoryTitle)
            setResult(RESULT_OK, intent)
            window.clearFlags(FLAG_NOT_TOUCHABLE)
            finish()
        }
    }

    override fun onUrisSelected(vararg uris: Uri) {
        viewModel.setImageUris(arrayOf(*uris))
    }

    override fun onPhotoAttachClicked() {
        photoAttachFragment.show(fragmentManager, PhotoAttachFragment.TAG)
    }

    override fun onUpdateDoneClicked() {
        window.setFlags(FLAG_NOT_TOUCHABLE, FLAG_NOT_TOUCHABLE)
        lifecycleScope.launch {
            showToast(getString(R.string.visit_update_posting))
            viewModel.updateVisit(this@VisitUpdateActivity)
        }
    }

    companion object {
        fun startWithResultLauncher(
            visitId: Long,
            memoryId: Long,
            memoryTitle: String,
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, VisitUpdateActivity::class.java).apply {
                putExtra(MOMENT_ID_KEY, visitId)
                putExtra(MEMORY_ID_KEY, memoryId)
                putExtra(MEMORY_TITLE_KEY, memoryTitle)
                activityLauncher.launch(this)
            }
        }
    }
}
