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
import com.woowacourse.staccato.presentation.visitcreation.dialog.TravelSelectionFragment
import com.woowacourse.staccato.presentation.visitcreation.dialog.VisitedAtSelectionFragment
import com.woowacourse.staccato.presentation.visitcreation.viewmodel.VisitCreationViewModel
import com.woowacourse.staccato.presentation.visitcreation.viewmodel.VisitCreationViewModelFactory

class VisitCreationActivity :
    BindingActivity<ActivityVisitCreationBinding>(),
    OnUrisSelectedListener,
    VisitCreationHandler {
    override val layoutResourceId = R.layout.activity_visit_creation
    private val viewModel: VisitCreationViewModel by viewModels { VisitCreationViewModelFactory() }

    private val travelSelectionFragment = TravelSelectionFragment()
    private val visitedAtSelectionFragment = VisitedAtSelectionFragment()
    private val photoAttachFragment = PhotoAttachFragment()
    private val fragmentManager: FragmentManager = supportFragmentManager

    private val pinId: Long = 1L

    override fun initStartView(savedInstanceState: Bundle?) {
        initBinding()
        initDialogHandler()
        initVisitCreateDoneButton()
        initToolbar()
        observeViewModelData()
        viewModel.fetchInitData(pinId = pinId)
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.visitCreationHandler = this
    }

    private fun initDialogHandler() {
        travelSelectionFragment.setOnTravelSelected { selectedTravel ->
            viewModel.updateSelectedTravel(selectedTravel)
        }
        visitedAtSelectionFragment.setOnVisitedAtSelected { selectedVisitedAt ->
            viewModel.updateSelectedVisitedAt(selectedVisitedAt)
        }
    }

    private fun initToolbar() {
        binding.toolbarVisitCreation.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initVisitCreateDoneButton() {
        binding.btnVisitCreateDone.setOnClickListener {
            viewModel.createVisit(pinId)
        }
    }

    private fun observeViewModelData() {
        viewModel.travels.observe(this) { travels ->
            travelSelectionFragment.setItems(travels)
        }
        viewModel.selectedTravel.observe(this) { selectedTravel ->
            val dates = selectedTravel.buildDatesInRange()
            viewModel.updateSelectedVisitedAt(null)
            visitedAtSelectionFragment.setItems(dates)
        }
        viewModel.createdVisitId.observe(this) { createdVisitId ->
            val resultIntent = Intent()
            resultIntent.putExtra(EXTRA_VISIT_ID, createdVisitId)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun onUrisSelected(vararg uris: Uri) {
        viewModel.setImageUris(uris)
    }

    override fun onTravelSelectionClicked() {
        travelSelectionFragment.show(fragmentManager, TravelSelectionFragment.TAG)
    }

    override fun onVisitedAtClicked() {
        visitedAtSelectionFragment.show(fragmentManager, VisitedAtSelectionFragment.TAG)
    }

    override fun onPhotoAttachClicked() {
        photoAttachFragment.show(fragmentManager, PhotoAttachFragment.TAG)
    }

    companion object {
        const val EXTRA_VISIT_ID = "visitId"

        fun startWithResultLauncher(
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, VisitCreationActivity::class.java).apply {
                activityLauncher.launch(this)
            }
        }
    }
}
