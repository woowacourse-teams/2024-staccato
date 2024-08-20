package com.woowacourse.staccato.presentation.momentcreation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.ActivityVisitCreationBinding
import com.woowacourse.staccato.presentation.base.BindingActivity
import com.woowacourse.staccato.presentation.common.PhotoAttachFragment
import com.woowacourse.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.woowacourse.staccato.presentation.moment.MomentFragment.Companion.MOMENT_ID_KEY
import com.woowacourse.staccato.presentation.momentcreation.adapter.PhotoAttachAdapter
import com.woowacourse.staccato.presentation.momentcreation.model.AttachedPhotoUiModel
import com.woowacourse.staccato.presentation.momentcreation.viewmodel.MomentCreationViewModel
import com.woowacourse.staccato.presentation.momentcreation.viewmodel.MomentCreationViewModelFactory
import com.woowacourse.staccato.presentation.util.showToast
import com.woowacourse.staccato.presentation.visitcreation.adapter.AttachedPhotoItemTouchHelperCallback
import com.woowacourse.staccato.presentation.visitcreation.adapter.ItemDragListener

class MomentCreationActivity :
    BindingActivity<ActivityVisitCreationBinding>(),
    OnUrisSelectedListener,
    MomentCreationHandler {
    override val layoutResourceId = R.layout.activity_visit_creation
    private val viewModel: MomentCreationViewModel by viewModels { MomentCreationViewModelFactory() }
    private val photoAttachFragment = PhotoAttachFragment()
    private val fragmentManager: FragmentManager = supportFragmentManager
    private lateinit var adapter: PhotoAttachAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private val memoryId by lazy { intent.getLongExtra(MEMORY_ID_KEY, 0L) }
    private val memoryTitle by lazy { intent.getStringExtra(MEMORY_TITLE_KEY) ?: "" }
    private val inputManager: InputMethodManager by lazy {
        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onUrisSelected(vararg uris: Uri) {
        viewModel.updateSelectedImageUris(arrayOf(*uris))
    }

    override fun onCreateDoneClicked() {
        window.setFlags(FLAG_NOT_TOUCHABLE, FLAG_NOT_TOUCHABLE)
        showToast(getString(R.string.visit_creation_posting))
        viewModel.createMoment(memoryId)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let { view ->
                if (!isTouchInsideView(event, view)) {
                    clearFocusAndHideKeyboard(view)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun isTouchInsideView(
        event: MotionEvent,
        view: View,
    ): Boolean {
        val rect = android.graphics.Rect()
        view.getGlobalVisibleRect(rect)
        return rect.contains(event.rawX.toInt(), event.rawY.toInt())
    }

    private fun clearFocusAndHideKeyboard(view: View) {
        view.clearFocus()
        hideKeyboard(view)
    }

    private fun hideKeyboard(view: View) {
        inputManager.hideSoftInputFromWindow(
            view.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS,
        )
    }

    override fun initStartView(savedInstanceState: Bundle?) {
        initBinding()
        initMemoryInfo()
        initAdapter()
        initItemTouchHelper()
        initToolbar()
        observeViewModelData()
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.visitCreationHandler = this
    }

    private fun initMemoryInfo() {
        viewModel.initMemoryInfo(memoryId, memoryTitle)
    }

    private fun initAdapter() {
        adapter =
            PhotoAttachAdapter(
                object : ItemDragListener {
                    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                        itemTouchHelper.startDrag(viewHolder)
                    }

                    override fun onStopDrag(list: List<AttachedPhotoUiModel>) {
                        viewModel.setUrisWithNewOrder(list)
                    }
                },
                viewModel,
            )
        binding.rvPhotoAttach.adapter = adapter
    }

    private fun initItemTouchHelper() {
        itemTouchHelper = ItemTouchHelper(AttachedPhotoItemTouchHelperCallback(adapter))
        itemTouchHelper.attachToRecyclerView(binding.rvPhotoAttach)
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
        viewModel.pendingPhotos.observe(this) {
            viewModel.fetchPhotosUrlsByUris(this)
        }
        viewModel.currentPhotos.observe(this) { photos ->
            adapter.submitList(
                listOf(AttachedPhotoUiModel.addPhotoButton).plus(photos.attachedPhotos),
            )
        }
        viewModel.createdMomentId.observe(this) { createdMomentId ->
            val resultIntent =
                Intent()
                    .putExtra(MOMENT_ID_KEY, createdMomentId)
                    .putExtra(MEMORY_ID_KEY, memoryId)
                    .putExtra(MEMORY_TITLE_KEY, memoryTitle)
            setResult(RESULT_OK, resultIntent)
            window.clearFlags(FLAG_NOT_TOUCHABLE)
            finish()
        }
        viewModel.errorMessage.observe(this) {
            window.clearFlags(FLAG_NOT_TOUCHABLE)
            showToast(it)
        }
    }

    companion object {
        const val MEMORY_TITLE_KEY = "memoryTitle"

        fun startWithResultLauncher(
            memoryId: Long,
            memoryTitle: String,
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, MomentCreationActivity::class.java).apply {
                putExtra(MEMORY_ID_KEY, memoryId)
                putExtra(MEMORY_TITLE_KEY, memoryTitle)
                activityLauncher.launch(this)
            }
        }
    }
}
