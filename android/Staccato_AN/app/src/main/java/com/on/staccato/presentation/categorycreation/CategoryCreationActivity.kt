package com.on.staccato.presentation.categorycreation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.on.staccato.R
import com.on.staccato.databinding.ActivityCategoryCreationBinding
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.category.CategoryFragment.Companion.CATEGORY_ID_KEY
import com.on.staccato.presentation.categorycreation.component.CategoryShareSection
import com.on.staccato.presentation.categorycreation.component.PeriodActiveSwitch
import com.on.staccato.presentation.categorycreation.model.CategoryCreationError
import com.on.staccato.presentation.categorycreation.viewmodel.CategoryCreationViewModel
import com.on.staccato.presentation.common.PhotoAttachFragment
import com.on.staccato.presentation.common.color.CategoryColor
import com.on.staccato.presentation.common.color.ColorSelectionDialogFragment
import com.on.staccato.presentation.common.color.ColorSelectionDialogFragment.Companion.COLOR_SELECTION_REQUEST_KEY
import com.on.staccato.presentation.common.color.ColorSelectionDialogFragment.Companion.SELECTED_COLOR_LABEL
import com.on.staccato.presentation.common.photo.UploadFile
import com.on.staccato.presentation.staccatocreation.OnUrisSelectedListener
import com.on.staccato.presentation.util.ExceptionState2
import com.on.staccato.presentation.util.convertCategoryUriToFile
import com.on.staccato.presentation.util.getSnackBarWithAction
import com.on.staccato.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryCreationActivity :
    BindingActivity<ActivityCategoryCreationBinding>(),
    CategoryCreationHandler,
    OnUrisSelectedListener {
    override val layoutResourceId = R.layout.activity_category_creation
    private val viewModel: CategoryCreationViewModel by viewModels()

    private val photoAttachFragment = PhotoAttachFragment()
    private val dateRangePicker = buildDateRangePicker()
    private var currentSnackBar: Snackbar? = null

    override fun initStartView(savedInstanceState: Bundle?) {
        initBinding()
        initColorSelectionResultListener()
        navigateToHome()
        updateCategoryPeriod()
        observeCreatedCategoryId()
        observeIsPosting()
        showErrorToast()
        handleError()
    }

    override fun onPeriodSelectionClicked() {
        if (!dateRangePicker.isAdded) {
            dateRangePicker.show(supportFragmentManager, dateRangePicker.toString())
        }
    }

    override fun onSaveClicked() {
        viewModel.createCategory()
    }

    override fun onPhotoAttachClicked() {
        if (!photoAttachFragment.isAdded) {
            photoAttachFragment.show(supportFragmentManager, PhotoAttachFragment.TAG)
        }
    }

    override fun onImageDeletionClicked() {
        currentSnackBar?.dismiss()
        viewModel.clearThumbnail()
    }

    override fun onUrisSelected(vararg uris: Uri) {
        currentSnackBar?.dismiss()
        val uri = uris.first()
        val file: UploadFile = convertCategoryUriToFile(this, uri)
        viewModel.createThumbnailUrl(uri, file)
    }

    override fun onColorSelectionClicked() {
        val existing = supportFragmentManager.findFragmentByTag(ColorSelectionDialogFragment.TAG)
        val selectedColor = viewModel.color.value
        if (existing == null && selectedColor != null) {
            ColorSelectionDialogFragment.newInstance(selectedColor)
                .show(supportFragmentManager, ColorSelectionDialogFragment.TAG)
        }
    }

    private fun initColorSelectionResultListener() {
        supportFragmentManager.setFragmentResultListener(
            COLOR_SELECTION_REQUEST_KEY,
            this,
        ) { _, bundle ->
            bundle.getString(SELECTED_COLOR_LABEL)?.let {
                viewModel.updateCategoryColor(CategoryColor.getColorBy(it))
            }
        }
    }

    private fun buildDateRangePicker() =
        MaterialDatePicker.Builder.dateRangePicker()
            .setTheme(R.style.DatePickerStyle)
            .setSelection(
                Pair(
                    MaterialDatePicker.todayInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds(),
                ),
            ).build()

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.handler = this
        binding.cvCategoryCreationPeriodSet.setContent {
            PeriodActiveSwitch()
        }
        binding.cvCategoryCreationShare.setContent {
            CategoryShareSection()
        }
    }

    private fun navigateToHome() {
        binding.toolbarCategoryCreation.setNavigationOnClickListener {
            finish()
        }
    }

    private fun updateCategoryPeriod() {
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate: Long = selection.first
            val endDate: Long = selection.second
            viewModel.setCategoryPeriod(startDate, endDate)
        }
    }

    private fun observeCreatedCategoryId() {
        viewModel.createdCategoryId.observe(this) { categoryId ->
            val resultIntent = Intent()
            resultIntent.putExtra(CATEGORY_ID_KEY, categoryId)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun observeIsPosting() {
        viewModel.isPosting.observe(this) {
            if (it) {
                window.setFlags(FLAG_NOT_TOUCHABLE, FLAG_NOT_TOUCHABLE)
            } else {
                window.clearFlags(FLAG_NOT_TOUCHABLE)
            }
        }
    }

    private fun showErrorToast() {
        viewModel.errorMessage.observe(this) {
            showToast(it)
        }
    }

    private fun handleError() {
        viewModel.error.observe(this) { error ->
            when (error) {
                is CategoryCreationError.Thumbnail -> handleCreatePhotoUrlFail(error)
                is CategoryCreationError.CategoryCreation -> handleCreateException(error)
            }
        }
    }

    private fun handleCreatePhotoUrlFail(error: CategoryCreationError.Thumbnail) {
        showExceptionSnackBar(error.state) { recreateThumbnailUrl(error.uri, error.file) }
    }

    private fun handleCreateException(error: CategoryCreationError.CategoryCreation) {
        showExceptionSnackBar(error.state) { recreateCategory() }
    }

    private fun recreateThumbnailUrl(
        uri: Uri,
        file: UploadFile,
    ) {
        viewModel.createThumbnailUrl(uri, file)
    }

    private fun recreateCategory() {
        viewModel.createCategory()
    }

    private fun showExceptionSnackBar(
        state: ExceptionState2,
        onRetryAction: () -> Unit,
    ) {
        currentSnackBar =
            binding.root.getSnackBarWithAction(
                message = getString(state.messageId),
                actionLabel = R.string.all_retry,
                onAction = onRetryAction,
                length = Snackbar.LENGTH_INDEFINITE,
            ).apply { show() }
    }

    companion object {
        fun startWithResultLauncher(
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, CategoryCreationActivity::class.java).apply {
                activityLauncher.launch(this)
            }
        }
    }
}
