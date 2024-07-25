package com.woowacourse.staccato.presentation.visitcreation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentManager
import com.woowacourse.staccato.PhotoAttachFragment
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.ActivityVisitCreationBinding
import com.woowacourse.staccato.presentation.base.BindingActivity
import com.woowacourse.staccato.presentation.visitcreation.dialog.TravelSelectionFragment
import com.woowacourse.staccato.presentation.visitcreation.dialog.VisitedAtSelectionFragment

class VisitCreationActivity :
    BindingActivity<ActivityVisitCreationBinding>(),
    VisitCreationHandler {
    override val layoutResourceId = R.layout.activity_visit_creation
    private val viewModel = VisitCreationViewModel()

    private val travelSelectionFragment = TravelSelectionFragment()
    private val visitedAtSelectionFragment = VisitedAtSelectionFragment()
    private val photoAttachFragment = PhotoAttachFragment()
    private val fragmentManager: FragmentManager = supportFragmentManager

    override fun initStartView(savedInstanceState: Bundle?) {
        initBinding()
        initDialogHandler()
        initVisitCreateDoneButton()
        initToolbar()
        observeViewModelData()
        viewModel.fetchVisitCreation()
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
            viewModel.updateVisitedAt(selectedVisitedAt)
        }
    }

    private fun initToolbar() {
        binding.toolbarVisitCreation.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initVisitCreateDoneButton() {
        binding.btnVisitCreateDone.setOnClickListener {
            val resultIntent = Intent()
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun observeViewModelData() {
        viewModel.visitCreationData.observe(this) { visitCreationData ->
            travelSelectionFragment.setItems(visitCreationData.travels)
        }
        viewModel.selectedTravel.observe(this) { selectedTravel ->
            val dates = selectedTravel.buildLocalDatesInRange()
            viewModel.updateVisitedAt(null)
            visitedAtSelectionFragment.setItems(dates)
        }
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
        fun startWithResultLauncher(
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, VisitCreationActivity::class.java).apply {
                // putExtra(EXTRA_VISIT_ID, visitId)
                activityLauncher.launch(this)
            }
        }
    }
}
