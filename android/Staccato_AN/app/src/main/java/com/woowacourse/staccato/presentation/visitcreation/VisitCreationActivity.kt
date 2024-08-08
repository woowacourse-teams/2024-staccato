package com.woowacourse.staccato.presentation.visitcreation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.ActivityVisitCreationBinding
import com.woowacourse.staccato.presentation.base.BindingActivity
import com.woowacourse.staccato.presentation.common.PhotoAttachFragment
import com.woowacourse.staccato.presentation.util.showToast
import com.woowacourse.staccato.presentation.visit.VisitFragment.Companion.VISIT_ID_KEY
import com.woowacourse.staccato.presentation.visitcreation.viewmodel.VisitCreationViewModel
import com.woowacourse.staccato.presentation.visitcreation.viewmodel.VisitCreationViewModelFactory

class VisitCreationActivity :
    BindingActivity<ActivityVisitCreationBinding>(),
    OnUrisSelectedListener,
    VisitCreationHandler {
    override val layoutResourceId = R.layout.activity_visit_creation
    private val viewModel: VisitCreationViewModel by viewModels { VisitCreationViewModelFactory() }

    private val photoAttachFragment = PhotoAttachFragment()
    private val fragmentManager: FragmentManager = supportFragmentManager

    private val travelId by lazy { intent.getLongExtra(TRAVEL_ID_KEY, 0L) }
    private val travelTitle by lazy { intent.getStringExtra(TRAVEL_TITLE_KEY) ?: "" }

    override fun initStartView(savedInstanceState: Bundle?) {
        initTravelInfo()
        initBinding()
        initToolbar()
        observeViewModelData()
    }

    private fun initTravelInfo() {
        viewModel.initTravelInfo(travelId, travelTitle)
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.visitCreationHandler = this
    }

    private fun initToolbar() {
        binding.toolbarVisitCreation.setNavigationOnClickListener {
            finish()
        }
    }

    private fun observeViewModelData() {
        viewModel.createdVisitId.observe(this) { createdVisitId ->
            val resultIntent =
                Intent()
                    .putExtra(VISIT_ID_KEY, createdVisitId)
                    .putExtra(TRAVEL_ID_KEY, travelId)
                    .putExtra(TRAVEL_TITLE_KEY, travelTitle)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        viewModel.errorMessage.observe(this) {
            showToast(it)
        }
    }

    override fun onUrisSelected(vararg uris: Uri) {
        viewModel.setImageUris(uris)
    }

    override fun onPhotoAttachClicked() {
        photoAttachFragment.show(fragmentManager, PhotoAttachFragment.TAG)
    }

    override fun onCreateDoneClicked() {
        viewModel.createVisit(travelId, this)
    }

    companion object {
        const val TRAVEL_ID_KEY = "travelId"
        const val TRAVEL_TITLE_KEY = "travelTitle"

        fun startWithResultLauncher(
            travelId: Long,
            travelTitle: String,
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, VisitCreationActivity::class.java).apply {
                putExtra(TRAVEL_ID_KEY, travelId)
                putExtra(TRAVEL_TITLE_KEY, travelTitle)
                activityLauncher.launch(this)
            }
        }
    }
}
