package com.woowacourse.staccato.presentation.visitcreation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.ActivityVisitCreationBinding
import com.woowacourse.staccato.presentation.base.BindingActivity
import com.woowacourse.staccato.presentation.common.PhotoAttachFragment
import com.woowacourse.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.woowacourse.staccato.presentation.util.showToast
import com.woowacourse.staccato.presentation.visit.VisitFragment.Companion.VISIT_ID_KEY
import com.woowacourse.staccato.presentation.visitcreation.adapter.PhotoAttachAdapter
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
    private lateinit var adapter: PhotoAttachAdapter
    private val travelId by lazy { intent.getLongExtra(MEMORY_ID_KEY, 0L) }
    private val travelTitle by lazy { intent.getStringExtra(TRAVEL_TITLE_KEY) ?: "" }

    override fun initStartView(savedInstanceState: Bundle?) {
        initTravelInfo()
        initBinding()
        initAdapter()
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
        viewModel.createdVisitId.observe(this) { createdVisitId ->
            val resultIntent =
                Intent()
                    .putExtra(VISIT_ID_KEY, createdVisitId)
                    .putExtra(MEMORY_ID_KEY, travelId)
                    .putExtra(TRAVEL_TITLE_KEY, travelTitle)
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
        viewModel.createVisit(travelId, this)
    }

    companion object {
        const val TRAVEL_TITLE_KEY = "travelTitle"

        fun startWithResultLauncher(
            travelId: Long,
            travelTitle: String,
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, VisitCreationActivity::class.java).apply {
                putExtra(MEMORY_ID_KEY, travelId)
                putExtra(TRAVEL_TITLE_KEY, travelTitle)
                activityLauncher.launch(this)
            }
        }
    }
}
