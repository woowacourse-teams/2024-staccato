package com.on.staccato.presentation.staccatocreation

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.libraries.places.api.model.Place
import com.google.android.material.snackbar.Snackbar
import com.on.staccato.presentation.R
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.category.CategoryFragment.Companion.CATEGORY_ID_KEY
import com.on.staccato.presentation.category.CategoryFragment.Companion.CATEGORY_TITLE_KEY
import com.on.staccato.presentation.categoryselection.CategorySelectionFragment
import com.on.staccato.presentation.categoryselection.CategorySelectionViewModelProvider
import com.on.staccato.presentation.common.MessageEvent
import com.on.staccato.presentation.databinding.ActivityStaccatoCreationBinding
import com.on.staccato.presentation.location.GPSManager
import com.on.staccato.presentation.location.LocationDialogFragment.Companion.PERMISSION_CANCEL_KEY
import com.on.staccato.presentation.location.LocationPermissionManager
import com.on.staccato.presentation.location.LocationPermissionManager.Companion.locationPermissions
import com.on.staccato.presentation.location.PermissionCancelListener
import com.on.staccato.presentation.map.model.LocationUiModel
import com.on.staccato.presentation.photo.PhotoAttachFragment
import com.on.staccato.presentation.photo.PhotosUiModel.Companion.MAX_PHOTO_NUMBER
import com.on.staccato.presentation.place.CustomAutocompleteSupportFragment
import com.on.staccato.presentation.place.GooglePlaceFragmentEventHandler
import com.on.staccato.presentation.staccato.StaccatoFragment.Companion.CREATED_STACCATO_KEY
import com.on.staccato.presentation.staccato.StaccatoFragment.Companion.STACCATO_ID_KEY
import com.on.staccato.presentation.staccatocreation.adapter.AttachedPhotoItemTouchHelperCallback
import com.on.staccato.presentation.staccatocreation.adapter.PhotoAttachAdapter
import com.on.staccato.presentation.staccatocreation.adapter.PhotoAttachAdapter.Companion.photoAdditionButton
import com.on.staccato.presentation.staccatocreation.dialog.VisitedAtSelectionFragment
import com.on.staccato.presentation.staccatocreation.viewmodel.StaccatoCreationViewModel
import com.on.staccato.presentation.util.convertUriToFile
import com.on.staccato.presentation.util.getSnackBarWithAction
import com.on.staccato.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class StaccatoCreationActivity :
    CategorySelectionViewModelProvider,
    GooglePlaceFragmentEventHandler,
    CurrentLocationHandler,
    OnUrisSelectedListener,
    StaccatoCreationHandler,
    BindingActivity<ActivityStaccatoCreationBinding>() {
    override val layoutResourceId = R.layout.activity_staccato_creation
    override val viewModel: StaccatoCreationViewModel by viewModels()
    private val categorySelectionFragment by lazy {
        CategorySelectionFragment()
    }
    private val visitedAtSelectionFragment by lazy {
        VisitedAtSelectionFragment()
    }

    private val photoAttachFragment by lazy {
        PhotoAttachFragment().apply { setMultipleAbleOption(true) }
    }
    private val autocompleteFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as CustomAutocompleteSupportFragment
    }

    private lateinit var photoAttachAdapter: PhotoAttachAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    private val categoryId by lazy { intent.getLongExtra(CATEGORY_ID_KEY, DEFAULT_CATEGORY_ID) }
    private val categoryTitle by lazy { intent.getStringExtra(CATEGORY_TITLE_KEY) ?: "" }
    private val isPermissionCanceled by lazy {
        intent.getBooleanExtra(
            PERMISSION_CANCEL_KEY,
            false,
        )
    }

    @Inject
    lateinit var gpsManager: GPSManager

    @Inject
    lateinit var locationPermissionManager: LocationPermissionManager

    private lateinit var permissionRequestLauncher: ActivityResultLauncher<Array<String>>
    private var address: String = DEFAULT_ADDRESS
    private var currentSnackBar: Snackbar? = null

    override fun initStartView(savedInstanceState: Bundle?) {
        viewModel.fetchAllCategories()
        setupPermissionRequestLauncher()
        initBinding()
        initAdapter()
        initItemTouchHelper()
        initToolbar()
        initVisitedAtSelectionFragment()
        observeViewModelData()
        initGooglePlaceSearch()
        observeMessageEvent()
        handleError()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isFromPlaceSearch.getValue() != true) checkLocationSetting()
    }

    override fun onNewPlaceSelected(
        id: String,
        name: String,
        address: String,
        longitude: Double,
        latitude: Double,
    ) {
        viewModel.selectNewPlace(
            id,
            name,
            address,
            longitude,
            latitude,
        )
    }

    override fun onSelectedPlaceCleared() {
        viewModel.clearPlace()
    }

    override fun onCurrentLocationClicked() {
        checkLocationSetting(isCurrentLocationCallClicked = true)
    }

    override fun onUrisSelected(vararg uris: Uri) {
        viewModel.updateSelectedImageUris(arrayOf(*uris))
    }

    override fun onCategorySelectionClicked() {
        if (!categorySelectionFragment.isAdded) {
            categorySelectionFragment.show(
                supportFragmentManager,
                CategorySelectionFragment.TAG,
            )
        }
    }

    override fun onVisitedAtSelectionClicked() {
        if (!visitedAtSelectionFragment.isAdded) {
            viewModel.selectedVisitedAt.value?.let {
                visitedAtSelectionFragment.updateSelectedVisitedAt(it)
            }
            visitedAtSelectionFragment.show(
                supportFragmentManager,
                VisitedAtSelectionFragment.TAG,
            )
        }
    }

    override fun onCreateDoneClicked() {
        window.setFlags(FLAG_NOT_TOUCHABLE, FLAG_NOT_TOUCHABLE)
        viewModel.createStaccato()
    }

    private fun setupPermissionRequestLauncher() {
        permissionRequestLauncher =
            locationPermissionManager.requestPermissionLauncher(
                activity = this,
                activityResultCaller = this,
                actionWhenHavePermission = ::fetchCurrentLocationAddress,
            )
    }

    private fun checkLocationSetting(isCurrentLocationCallClicked: Boolean = false) {
        gpsManager.checkLocationSetting(
            context = this,
            activity = this,
            actionWhenGPSIsOn = {
                fetchCurrentLocationAddress(
                    isCurrentLocationCallClicked,
                )
            },
        )
    }

    private fun fetchCurrentLocationAddress(isCurrentLocationCallClicked: Boolean = false) {
        val isLocationPermissionGranted = locationPermissionManager.checkSelfLocationPermission()
        val shouldShowRequestLocationPermissionsRationale =
            locationPermissionManager.shouldShowRequestLocationPermissionsRationale(activity = this)

        when {
            isLocationPermissionGranted -> {
                viewModel.getCurrentLocation()
            }

            isCurrentLocationCallClicked -> {
                locationPermissionManager.showLocationRequestRationaleDialog(supportFragmentManager)
            }

            shouldShowRequestLocationPermissionsRationale -> {
                observeIsPermissionCancelClicked {
                    locationPermissionManager.showLocationRequestRationaleDialog(
                        supportFragmentManager,
                    )
                }
            }

            else -> {
                observeIsPermissionCancelClicked {
                    permissionRequestLauncher.launch(locationPermissions)
                }
            }
        }
    }

    private fun observeIsPermissionCancelClicked(listener: PermissionCancelListener) {
        if (!isPermissionCanceled) listener.requestPermission()
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.staccatoCreationHandler = this
        binding.currentLocationHandler = this
    }

    private fun initAdapter() {
        photoAttachAdapter =
            PhotoAttachAdapter(viewModel) { viewModel.updatePhotosWithNewOrder(it) }
        binding.rvPhotoAttach.adapter = photoAttachAdapter
    }

    private fun initItemTouchHelper() {
        itemTouchHelper = ItemTouchHelper(AttachedPhotoItemTouchHelperCallback(photoAttachAdapter))
        itemTouchHelper.attachToRecyclerView(binding.rvPhotoAttach)
    }

    private fun initToolbar() {
        binding.toolbarStaccatoCreation.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initVisitedAtSelectionFragment() {
        visitedAtSelectionFragment.setOnVisitedAtSelected { selectedVisitedAt ->
            viewModel.selectedVisitedAt(selectedVisitedAt)
        }
    }

    private fun observeViewModelData() {
        observePhotoData()
        observePlaceData()
        observeVisitedAtData()
        observeCategoryData()
        observeCreatedStaccatoId()
        observeCurrentLocation()
    }

    private fun observePhotoData() {
        viewModel.isAddPhotoClicked.observe(this) {
            if (!photoAttachFragment.isAdded && it) {
                photoAttachFragment.show(supportFragmentManager, PhotoAttachFragment.TAG)
            }
        }
        viewModel.pendingPhotos.observe(this) { photos ->
            photos.forEach { photo ->
                photo.uri?.let { uri ->
                    val file = convertUriToFile(this, uri)
                    viewModel.launchPhotoUploadJob(file, photo)
                }
            }
        }
        viewModel.currentPhotos.observe(this) { photos ->
            photoAttachFragment.setCurrentImageCount(MAX_PHOTO_NUMBER - photos.size)
            photoAttachAdapter.submitList(
                listOf(photoAdditionButton, *photos.photos.toTypedArray()),
            )
        }
    }

    private fun observePlaceData() {
        viewModel.placeName.observe(this) {
            autocompleteFragment.setText(it)
        }
    }

    private fun observeVisitedAtData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedVisitedAt.collect {
                    it?.let {
                        visitedAtSelectionFragment.updateSelectedVisitedAt(it)
                        if (categoryId == DEFAULT_CATEGORY_ID) {
                            viewModel.updateCategorySelectionBy(it)
                        }
                    }
                }
            }
        }
    }

    private fun observeCategoryData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allCategories.collect {
                    if (categoryId == DEFAULT_CATEGORY_ID) {
                        visitedAtSelectionFragment.initCalendarByPeriod()
                    }
                    viewModel.initCategoryAndVisitedAt(categoryId, LocalDateTime.now())
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedCategory.collect {
                    it?.let {
                        if (categoryId != DEFAULT_CATEGORY_ID) {
                            visitedAtSelectionFragment.initCalendarByPeriod(
                                it.startAt,
                                it.endAt,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observeCreatedStaccatoId() {
        viewModel.createdStaccatoId.observe(this) { createdStaccatoId ->
            val resultIntent =
                Intent()
                    .putExtra(STACCATO_ID_KEY, createdStaccatoId)
                    .putExtra(CATEGORY_ID_KEY, categoryId)
                    .putExtra(CATEGORY_TITLE_KEY, categoryTitle)
                    .putExtra(CREATED_STACCATO_KEY, true)
            setResult(RESULT_OK, resultIntent)
            window.clearFlags(FLAG_NOT_TOUCHABLE)
            finish()
        }
    }

    private fun observeCurrentLocation() {
        viewModel.currentLocation.observe(this) {
            fetchAddress(it)
        }
    }

    private fun fetchAddress(location: LocationUiModel) {
        lifecycleScope.launch {
            val defaultDelayJob =
                launch {
                    delay(500L)
                }
            val getCurrentLocationJob =
                launch {
                    updateAddressByCurrentAddress(location)
                }
            getCurrentLocationJob.join()
            defaultDelayJob.join()
            viewModel.setPlaceByCurrentAddress(address)
        }
    }

    private fun updateAddressByCurrentAddress(location: LocationUiModel) {
        val geocoder = Geocoder(this@StaccatoCreationActivity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val geocodeListener = initGeocodeListener()
            geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1,
                geocodeListener,
            )
        } else {
            address =
                geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    ?.get(0)
                    ?.getAddressLine(0).toString()
        }
    }

    private fun initGeocodeListener() =
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        object : Geocoder.GeocodeListener {
            override fun onGeocode(addresses: MutableList<Address>) {
                address = addresses[0].getAddressLine(0)
            }

            override fun onError(errorMessage: String?) {
                showToast(getString(R.string.staccato_creation_not_found_address))
            }
        }

    private fun initGooglePlaceSearch() {
        val placeFields: List<Place.Field> =
            listOf(Place.Field.NAME, Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        autocompleteFragment.setPlaceFields(placeFields)

        lifecycleScope.launchWhenCreated {
            autocompleteFragment.setHint(getString(R.string.staccato_creation_place_search))
        }
    }

    private fun observeMessageEvent() {
        viewModel.messageEvent.observe(this) { event ->
            window.clearFlags(FLAG_NOT_TOUCHABLE)
            showToast(
                when (event) {
                    is MessageEvent.ResId -> getString(event.value)
                    is MessageEvent.Text -> event.value
                },
            )
        }
    }

    private fun handleError() {
        viewModel.error.observe(this) { error ->
            when (error) {
                is AllCandidates -> handleCategoryCandidatesFail(error.messageEvent)
                is StaccatoCreate -> handleStaccatoCreateFail(error.messageEvent)
            }
        }
    }

    private fun handleCategoryCandidatesFail(messageEvent: MessageEvent) {
        finish()
        showToast(
            when (messageEvent) {
                is MessageEvent.ResId -> getString(messageEvent.value)
                is MessageEvent.Text -> messageEvent.value
            },
        )
    }

    private fun handleStaccatoCreateFail(messageEvent: MessageEvent) {
        window.clearFlags(FLAG_NOT_TOUCHABLE)
        showExceptionSnackBar(
            when (messageEvent) {
                is MessageEvent.ResId -> getString(messageEvent.value)
                is MessageEvent.Text -> messageEvent.value
            },
        ) { recreateStaccato() }
    }

    private fun recreateStaccato() {
        viewModel.createStaccato()
    }

    private fun showExceptionSnackBar(
        message: String,
        onRetryAction: () -> Unit,
    ) {
        currentSnackBar =
            binding.root.getSnackBarWithAction(
                message = message,
                actionLabel = R.string.all_retry,
                onAction = onRetryAction,
                length = Snackbar.LENGTH_INDEFINITE,
            ).apply { show() }
    }

    companion object {
        const val DEFAULT_CATEGORY_ID = 0L
        private const val DEFAULT_CATEGORY_TITLE = ""
        private const val DEFAULT_ADDRESS = ""

        fun startWithResultLauncher(
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
            isPermissionCanceled: Boolean,
            categoryId: Long = DEFAULT_CATEGORY_ID,
            categoryTitle: String = DEFAULT_CATEGORY_TITLE,
        ) {
            Intent(context, StaccatoCreationActivity::class.java).apply {
                putExtra(CATEGORY_ID_KEY, categoryId)
                putExtra(CATEGORY_TITLE_KEY, categoryTitle)
                putExtra(PERMISSION_CANCEL_KEY, isPermissionCanceled)
                activityLauncher.launch(this)
            }
        }
    }
}
