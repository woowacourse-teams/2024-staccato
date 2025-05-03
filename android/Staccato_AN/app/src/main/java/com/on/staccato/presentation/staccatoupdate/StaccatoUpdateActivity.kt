package com.on.staccato.presentation.staccatoupdate

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.libraries.places.api.model.Place
import com.google.android.material.snackbar.Snackbar
import com.on.staccato.R
import com.on.staccato.databinding.ActivityStaccatoUpdateBinding
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.category.CategoryFragment.Companion.CATEGORY_ID_KEY
import com.on.staccato.presentation.category.CategoryFragment.Companion.CATEGORY_TITLE_KEY
import com.on.staccato.presentation.common.CustomAutocompleteSupportFragment
import com.on.staccato.presentation.common.GooglePlaceFragmentEventHandler
import com.on.staccato.presentation.common.PhotoAttachFragment
import com.on.staccato.presentation.common.categoryselection.CategorySelectionViewModelProvider
import com.on.staccato.presentation.common.location.GPSManager
import com.on.staccato.presentation.common.location.LocationPermissionManager
import com.on.staccato.presentation.common.location.LocationPermissionManager.Companion.locationPermissions
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.staccato.StaccatoFragment.Companion.STACCATO_ID_KEY
import com.on.staccato.presentation.staccatocreation.CurrentLocationHandler
import com.on.staccato.presentation.staccatocreation.OnUrisSelectedListener
import com.on.staccato.presentation.staccatocreation.adapter.AttachedPhotoItemTouchHelperCallback
import com.on.staccato.presentation.staccatocreation.adapter.PhotoAttachAdapter
import com.on.staccato.presentation.staccatocreation.dialog.CategorySelectionFragment
import com.on.staccato.presentation.staccatocreation.dialog.VisitedAtSelectionFragment
import com.on.staccato.presentation.staccatocreation.model.AttachedPhotoUiModel
import com.on.staccato.presentation.staccatocreation.viewmodel.StaccatoCreationViewModel
import com.on.staccato.presentation.staccatoupdate.viewmodel.StaccatoUpdateViewModel
import com.on.staccato.presentation.util.getSnackBarWithAction
import com.on.staccato.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StaccatoUpdateActivity :
    GooglePlaceFragmentEventHandler,
    CategorySelectionViewModelProvider,
    CurrentLocationHandler,
    OnUrisSelectedListener,
    StaccatoUpdateHandler,
    BindingActivity<ActivityStaccatoUpdateBinding>() {
    override val layoutResourceId = R.layout.activity_staccato_update
    override val viewModel: StaccatoUpdateViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()
    private val categorySelectionFragment by lazy {
        CategorySelectionFragment()
    }
    private val visitedAtSelectionFragment by lazy {
        VisitedAtSelectionFragment()
    }
    private val photoAttachFragment by lazy {
        PhotoAttachFragment().apply { setMultipleAbleOption(true) }
    }
    private lateinit var photoAttachAdapter: PhotoAttachAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private val staccatoId by lazy { intent.getLongExtra(STACCATO_ID_KEY, 0L) }
    private val categoryId by lazy { intent.getLongExtra(CATEGORY_ID_KEY, 0L) }
    private val categoryTitle by lazy { intent.getStringExtra(CATEGORY_TITLE_KEY) ?: "" }

    private val autocompleteFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as CustomAutocompleteSupportFragment
    }

    @Inject
    lateinit var gpsManager: GPSManager

    @Inject
    lateinit var locationPermissionManager: LocationPermissionManager

    private lateinit var permissionRequestLauncher: ActivityResultLauncher<Array<String>>
    private var address: String = DEFAULT_ADDRESS
    private var currentSnackBar: Snackbar? = null

    override fun onNewPlaceSelected(
        placeId: String,
        name: String,
        address: String,
        longitude: Double,
        latitude: Double,
    ) {
        viewModel.selectNewPlace(
            placeId,
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
        checkLocationSetting()
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

    override fun onUpdateDoneClicked() {
        window.setFlags(FLAG_NOT_TOUCHABLE, FLAG_NOT_TOUCHABLE)
        viewModel.updateStaccato(staccatoId)
    }

    override fun initStartView(savedInstanceState: Bundle?) {
        setupPermissionRequestLauncher()
        initBinding()
        initToolbar()
        initVisitedAtSelectionFragment()
        initAdapter()
        initItemTouchHelper()
        observeViewModelData()
        initGooglePlaceSearch()
        showWarningMessage()
        handleError()
        viewModel.fetchTargetData(staccatoId)
    }

    override fun onResume() {
        super.onResume()
        observeIsSettingClicked()
    }

    private fun setupPermissionRequestLauncher() {
        permissionRequestLauncher =
            locationPermissionManager.requestPermissionLauncher(
                view = binding.root,
                activity = this,
                activityResultCaller = this,
                actionWhenHavePermission = ::fetchCurrentLocationAddress,
            )
    }

    private fun checkLocationSetting() {
        gpsManager.checkLocationSetting(
            context = this,
            activity = this,
            actionWhenGPSIsOn = { fetchCurrentLocationAddress() },
        )
    }

    private fun fetchCurrentLocationAddress() {
        val isLocationPermissionGranted = locationPermissionManager.checkSelfLocationPermission()
        val shouldShowRequestLocationPermissionsRationale =
            locationPermissionManager.shouldShowRequestLocationPermissionsRationale(activity = this)

        when {
            isLocationPermissionGranted -> {
                viewModel.getCurrentLocation()
            }

            shouldShowRequestLocationPermissionsRationale -> {
                locationPermissionManager.showLocationRequestRationaleDialog(
                    supportFragmentManager,
                )
            }

            else -> {
                permissionRequestLauncher.launch(locationPermissions)
            }
        }
    }

    private fun observeIsSettingClicked() {
        sharedViewModel.isSettingClicked.observe(this) { isSettingClicked ->
            val checkSelfLocationPermission =
                locationPermissionManager.checkSelfLocationPermission()

            if (isSettingClicked && checkSelfLocationPermission) viewModel.getCurrentLocation()
        }
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.staccatoUpdateHandler = this
        binding.currentLocationHandler = this
    }

    private fun initAdapter() {
        photoAttachAdapter =
            PhotoAttachAdapter(viewModel) { viewModel.setUrisWithNewOrder(it) }
        binding.rvPhotoAttach.adapter = photoAttachAdapter
    }

    private fun initItemTouchHelper() {
        itemTouchHelper = ItemTouchHelper(AttachedPhotoItemTouchHelperCallback(photoAttachAdapter))
        itemTouchHelper.attachToRecyclerView(binding.rvPhotoAttach)
    }

    private fun initToolbar() {
        binding.toolbarStaccatoUpdate.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initVisitedAtSelectionFragment() {
        visitedAtSelectionFragment.setOnVisitedAtSelected { selectedVisitedAt ->
            viewModel.selectVisitedAt(selectedVisitedAt)
        }
    }

    private fun observeViewModelData() {
        observePhotoData()
        observePlaceData()
        observeVisitedAtData()
        observeIsUpdateComplete()
        observeCurrentLocation()
    }

    private fun observePhotoData() {
        viewModel.isAddPhotoClicked.observe(this) {
            if (!photoAttachFragment.isAdded && it) {
                photoAttachFragment.show(supportFragmentManager, PhotoAttachFragment.TAG)
            }
        }
        viewModel.pendingPhotos.observe(this) {
            viewModel.fetchPhotosUrlsByUris(this)
        }
        viewModel.currentPhotos.observe(this) { photos ->
            photoAttachFragment.setCurrentImageCount(StaccatoCreationViewModel.MAX_PHOTO_NUMBER - photos.size)
            photoAttachAdapter.submitList(
                listOf(AttachedPhotoUiModel.addPhotoButton, *photos.attachedPhotos.toTypedArray()),
            )
        }
    }

    private fun observePlaceData() {
        viewModel.placeName.observe(this) {
            autocompleteFragment.setText(it)
        }
    }

    private fun observeVisitedAtData() {
        viewModel.selectedVisitedAt.observe(this) {
            it?.let {
                visitedAtSelectionFragment.initCalendarByVisitedAt(it)
                viewModel.updateCategorySelectionBy(it)
            }
        }
    }

    private fun observeIsUpdateComplete() {
        viewModel.isUpdateCompleted.observe(this) { isUpdateCompleted ->
            handleUpdateComplete(isUpdateCompleted)
        }
    }

    private fun observeCurrentLocation() {
        viewModel.currentLocation.observe(this) {
            fetchAddress(it)
        }
    }

    private fun handleUpdateComplete(isUpdateCompleted: Boolean) {
        if (isUpdateCompleted) {
            val intent =
                Intent()
                    .putExtra(STACCATO_ID_KEY, staccatoId)
                    .putExtra(CATEGORY_ID_KEY, categoryId)
                    .putExtra(CATEGORY_TITLE_KEY, categoryTitle)
            setResult(RESULT_OK, intent)
            window.clearFlags(FLAG_NOT_TOUCHABLE)
            finish()
        }
    }

    private fun fetchAddress(location: Location) {
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

    private fun updateAddressByCurrentAddress(location: Location) {
        val geocoder = Geocoder(this@StaccatoUpdateActivity)
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

    private fun showWarningMessage() {
        viewModel.warningMessage.observe(this) { message ->
            window.clearFlags(FLAG_NOT_TOUCHABLE)
            showToast(message)
        }
    }

    private fun handleError() {
        viewModel.error.observe(this) { error ->
            when (error) {
                is StaccatoUpdateError.CategoryCandidates -> handleCategoryCandidatesFail(error)
                is StaccatoUpdateError.StaccatoInitialize -> handleInitializeFail(error)
                is StaccatoUpdateError.StaccatoUpdate -> handleStaccatoUpdateFail(error)
            }
        }
    }

    private fun handleCategoryCandidatesFail(error: StaccatoUpdateError.CategoryCandidates) {
        finish()
        showToast(error.message)
    }

    private fun handleInitializeFail(error: StaccatoUpdateError.StaccatoInitialize) {
        finish()
        showToast(error.message)
    }

    private fun handleStaccatoUpdateFail(error: StaccatoUpdateError.StaccatoUpdate) {
        window.clearFlags(FLAG_NOT_TOUCHABLE)
        showExceptionSnackBar(error.message) { reUpdateStaccato() }
    }

    private fun reUpdateStaccato() {
        viewModel.updateStaccato(staccatoId)
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
        private const val DEFAULT_ADDRESS = ""

        fun startWithResultLauncher(
            staccatoId: Long,
            categoryId: Long,
            categoryTitle: String,
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, StaccatoUpdateActivity::class.java).apply {
                putExtra(STACCATO_ID_KEY, staccatoId)
                putExtra(CATEGORY_ID_KEY, categoryId)
                putExtra(CATEGORY_TITLE_KEY, categoryTitle)
                activityLauncher.launch(this)
            }
        }
    }
}
