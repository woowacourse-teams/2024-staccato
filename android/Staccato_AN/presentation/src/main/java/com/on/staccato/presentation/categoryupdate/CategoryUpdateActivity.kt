package com.on.staccato.presentation.categoryupdate

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
import com.on.staccato.domain.UploadFile
import com.on.staccato.presentation.R
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.category.CategoryFragment.Companion.CATEGORY_ID_KEY
import com.on.staccato.presentation.category.model.CategoryUiModel.Companion.DEFAULT_CATEGORY_ID
import com.on.staccato.presentation.categoryupdate.component.PeriodActiveSwitch
import com.on.staccato.presentation.categoryupdate.viewmodel.CategoryUpdateViewModel
import com.on.staccato.presentation.color.CategoryColor.Companion.getCategoryColorBy
import com.on.staccato.presentation.color.ColorSelectionDialogFragment
import com.on.staccato.presentation.color.ColorSelectionDialogFragment.Companion.COLOR_SELECTION_REQUEST_KEY
import com.on.staccato.presentation.color.ColorSelectionDialogFragment.Companion.SELECTED_COLOR_LABEL
import com.on.staccato.presentation.common.MessageEvent
import com.on.staccato.presentation.databinding.ActivityCategoryUpdateBinding
import com.on.staccato.presentation.photo.PhotoAttachFragment
import com.on.staccato.presentation.staccatocreation.OnUrisSelectedListener
import com.on.staccato.presentation.util.convertUriToFile
import com.on.staccato.presentation.util.getSnackBarWithAction
import com.on.staccato.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryUpdateActivity :
    BindingActivity<ActivityCategoryUpdateBinding>(),
    CategoryUpdateHandler,
    OnUrisSelectedListener {
    override val layoutResourceId = R.layout.activity_category_update
    private val categoryId by lazy { intent.getLongExtra(CATEGORY_ID_KEY, DEFAULT_CATEGORY_ID) }
    private val viewModel: CategoryUpdateViewModel by viewModels()

    private val photoAttachFragment = PhotoAttachFragment()
    private val dateRangePicker = buildDateRangePicker()
    private var currentSnackBar: Snackbar? = null

    override fun initStartView(savedInstanceState: Bundle?) {
        initBinding()
        initColorSelectionResultListener()
        navigateToCategory()
        fetchCategory()
        updateCategoryPeriod()
        observeIsUpdateSuccess()
        observeIsPosting()
        observeMessageEvent()
        handleError()
    }

    override fun onPeriodSelectionClicked() {
        if (!dateRangePicker.isAdded) {
            dateRangePicker.show(supportFragmentManager, dateRangePicker.toString())
        }
    }

    override fun onSaveClicked() {
        viewModel.updateCategory()
    }

    override fun onPhotoAttachClicked() {
        if (!photoAttachFragment.isAdded) {
            photoAttachFragment.show(supportFragmentManager, PhotoAttachFragment.TAG)
        }
    }

    override fun onPhotoDeletionClicked() {
        currentSnackBar?.dismiss()
        viewModel.clearThumbnail()
    }

    override fun onUrisSelected(vararg uris: Uri) {
        currentSnackBar?.dismiss()
        val uri = uris.first()
        val file: UploadFile = convertUriToFile(this, uri)
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
                viewModel.updateCategoryColor(getCategoryColorBy(it))
            }
        }
    }

    private fun buildDateRangePicker() =
        MaterialDatePicker.Builder.dateRangePicker().setTheme(R.style.DatePickerStyle).setSelection(
            Pair(
                MaterialDatePicker.thisMonthInUtcMilliseconds(),
                MaterialDatePicker.todayInUtcMilliseconds(),
            ),
        ).build()

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.handler = this
        binding.textInputCategoryUpdateTitle.clearFocus()
        binding.cvCategoryUpdatePeriodSet.setContent {
            PeriodActiveSwitch()
        }
    }

    private fun navigateToCategory() {
        binding.toolbarCategoryUpdate.setNavigationOnClickListener {
            finish()
        }
    }

    private fun fetchCategory() {
        viewModel.fetchCategory(categoryId)
    }

    private fun updateCategoryPeriod() {
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate: Long = selection.first
            val endDate: Long = selection.second
            viewModel.setCategoryPeriod(startDate, endDate)
        }
    }

    private fun observeIsUpdateSuccess() {
        viewModel.isUpdateSuccess.observe(this) { isUpdateSuccess ->
            navigateToCategory(isUpdateSuccess)
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

    private fun navigateToCategory(isUpdated: Boolean) {
        if (isUpdated) {
            val intent =
                Intent().putExtra(CATEGORY_ID_KEY, categoryId)
                    .putExtra(KEY_IS_CATEGORY_UPDATED, true)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun observeMessageEvent() {
        viewModel.messageEvent.observe(this) { event ->
            when (event) {
                is MessageEvent.Text -> showToast(event.value)
                is MessageEvent.ResId -> {}
            }
        }
    }

    private fun handleError() {
        viewModel.error.observe(this) { error ->
            when (error) {
                is CategoryUpdateError.CategoryInitialization -> handleInitializeFail(error)
                is CategoryUpdateError.Thumbnail -> handleCreatePhotoUrlFail(error)
                is CategoryUpdateError.CategoryUpdate -> handleCategoryUpdateFail(error)
            }
        }
    }

    private fun handleInitializeFail(error: CategoryUpdateError.CategoryInitialization) {
        finish()
        showToast(getString(error.messageId))
    }

    private fun handleCreatePhotoUrlFail(error: CategoryUpdateError.Thumbnail) {
        showExceptionSnackBar(error.messageId) { reCreateThumbnailUrl(error.uri, error.file) }
    }

    private fun handleCategoryUpdateFail(error: CategoryUpdateError.CategoryUpdate) {
        showExceptionSnackBar(error.messageId) { reUpdateCategory() }
    }

    private fun reCreateThumbnailUrl(
        uri: Uri,
        file: UploadFile,
    ) {
        viewModel.createThumbnailUrl(uri, file)
    }

    private fun reUpdateCategory() {
        viewModel.updateCategory()
    }

    private fun showExceptionSnackBar(
        state: Int,
        onRetryAction: () -> Unit,
    ) {
        currentSnackBar =
            binding.root.getSnackBarWithAction(
                message = getString(state),
                actionLabel = R.string.all_retry,
                onAction = onRetryAction,
                length = Snackbar.LENGTH_INDEFINITE,
            ).apply { show() }
    }

    companion object {
        const val KEY_IS_CATEGORY_UPDATED = "isCategoryUpdated"

        fun startWithResultLauncher(
            categoryId: Long,
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, CategoryUpdateActivity::class.java).apply {
                putExtra(CATEGORY_ID_KEY, categoryId)
                activityLauncher.launch(this)
            }
        }
    }
}
