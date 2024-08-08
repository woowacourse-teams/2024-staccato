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
import com.woowacourse.staccato.presentation.visitcreation.viewmodel.VisitCreationViewModel
import com.woowacourse.staccato.presentation.visitcreation.viewmodel.VisitCreationViewModelFactory
import kotlin.properties.Delegates

class VisitCreationActivity :
    BindingActivity<ActivityVisitCreationBinding>(),
    OnUrisSelectedListener,
    VisitCreationHandler {
    override val layoutResourceId = R.layout.activity_visit_creation
    private val viewModel: VisitCreationViewModel by viewModels { VisitCreationViewModelFactory() }

    private val photoAttachFragment = PhotoAttachFragment()
    private val fragmentManager: FragmentManager = supportFragmentManager

    private var travelId by Delegates.notNull<Long>()
    private lateinit var travelTitle: String

    override fun initStartView(savedInstanceState: Bundle?) {
        initTravelInfo()
        initBinding()
        initToolbar()
        observeViewModelData()
    }

    private fun initTravelInfo() {
        travelId = intent.getLongExtra(EXTRA_TRAVEL_ID, 0L)
        travelTitle = intent.getStringExtra(EXTRA_TRAVEL_TITLE) ?: return
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
            val resultIntent = Intent()
            resultIntent.putExtra(EXTRA_CREATED_VISIT_ID, createdVisitId)
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

    override fun onCreateDoneClicked() {
        viewModel.createVisit(travelId, this)
    }

    companion object {
        const val EXTRA_TRAVEL_ID = "travelId"
        const val EXTRA_TRAVEL_TITLE = "travelTitle"
        const val EXTRA_CREATED_VISIT_ID = "visitId"

        fun startWithResultLauncher(
            travelId: Long,
            travelTitle: String,
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, VisitCreationActivity::class.java).apply {
                putExtra(EXTRA_TRAVEL_ID, travelId)
                putExtra(EXTRA_TRAVEL_TITLE, travelTitle)
                activityLauncher.launch(this)
            }
        }
    }
}
