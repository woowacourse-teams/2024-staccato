package com.woowacourse.staccato.presentation.visitupdate

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.ActivityVisitUpdateBinding
import com.woowacourse.staccato.presentation.base.BindingActivity
import com.woowacourse.staccato.presentation.common.PhotoAttachFragment
import com.woowacourse.staccato.presentation.util.showToast
import com.woowacourse.staccato.presentation.visitcreation.OnUrisSelectedListener
import com.woowacourse.staccato.presentation.visitcreation.VisitCreationActivity
import com.woowacourse.staccato.presentation.visitupdate.viewmodel.VisitUpdateViewModel
import com.woowacourse.staccato.presentation.visitupdate.viewmodel.VisitUpdateViewModelFactory
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class VisitUpdateActivity :
    BindingActivity<ActivityVisitUpdateBinding>(),
    OnUrisSelectedListener,
    VisitUpdateHandler {
    override val layoutResourceId = R.layout.activity_visit_update
    private val viewModel: VisitUpdateViewModel by viewModels { VisitUpdateViewModelFactory() }

    private val photoAttachFragment = PhotoAttachFragment()
    private val fragmentManager: FragmentManager = supportFragmentManager

    private var visitId by Delegates.notNull<Long>()
    private var travelId by Delegates.notNull<Long>()

    override fun initStartView(savedInstanceState: Bundle?) {
        visitId = intent.getLongExtra(EXTRA_VISIT_ID, 1L)
        travelId = intent.getLongExtra(EXTRA_TRAVEL_ID, 1L)

        initBinding()
        initToolbar()
        observeViewModelData()
        viewModel.fetchInitData(visitId, travelId)
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
            showToast("방문을 불러올 수 없어요!")
            finish()
        }
    }

    private fun handleUpdateComplete(isUpdateCompleted: Boolean) {
        if (isUpdateCompleted) {
            val resultIntent = Intent()
            resultIntent.putExtra(VisitCreationActivity.EXTRA_TRAVEL_ID, visitId)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun onUrisSelected(uris: List<Uri>) {
        viewModel.setImageUris(uris)
    }

    override fun onPhotoAttachClicked() {
        photoAttachFragment.show(fragmentManager, PhotoAttachFragment.TAG)
    }

    override fun onUpdateDoneClicked() {
        val resultIntent = Intent()
        setResult(RESULT_OK, resultIntent)
        lifecycleScope.launch {
            viewModel.updateVisit()
            finish() // 여기 있어도 됨?
        }
    }

    companion object {
        private const val EXTRA_VISIT_ID = "visitId"
        private const val EXTRA_TRAVEL_ID = "travelId"

        fun startWithResultLauncher(
            travelId: Long,
            visitId: Long,
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, VisitUpdateActivity::class.java).apply {
                putExtra(EXTRA_TRAVEL_ID, travelId)
                putExtra(EXTRA_VISIT_ID, visitId)
                activityLauncher.launch(this)
            }
        }
    }
}
