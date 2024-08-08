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
import com.woowacourse.staccato.presentation.visit.VisitFragment.Companion.VISIT_ID_KEY
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

    private val visitId by lazy { intent.getLongExtra(VISIT_ID_KEY, 0L) }
    private val travelId by lazy { intent.getLongExtra(TRAVEL_ID_KEY, 0L) }
    private val travelTitle by lazy { intent.getStringExtra(TRAVEL_TITLE_KEY) ?: "" }

    override fun initStartView(savedInstanceState: Bundle?) {
        initBinding()
        initToolbar()
        observeViewModelData()
        viewModel.initViewModelData(visitId, travelId, travelTitle)
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
            val intent =
                Intent()
                    .putExtra(VISIT_ID_KEY, visitId)
                    .putExtra(TRAVEL_ID_KEY, travelId)
                    .putExtra(TRAVEL_TITLE_KEY, travelTitle)
            setResult(RESULT_OK, intent)
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
        val resultIntent = Intent()
        setResult(RESULT_OK, resultIntent)
        lifecycleScope.launch {
            viewModel.updateVisit(this@VisitUpdateActivity)
        }
    }

    companion object {
        private const val TRAVEL_ID_KEY = "travelId"
        private const val TRAVEL_TITLE_KEY = "travelTitle"

        fun startWithResultLauncher(
            visitId: Long,
            travelId: Long,
            travelTitle: String,
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, VisitUpdateActivity::class.java).apply {
                putExtra(VISIT_ID_KEY, visitId)
                putExtra(TRAVEL_ID_KEY, travelId)
                putExtra(TRAVEL_TITLE_KEY, travelTitle)
                activityLauncher.launch(this)
            }
        }
    }
}
