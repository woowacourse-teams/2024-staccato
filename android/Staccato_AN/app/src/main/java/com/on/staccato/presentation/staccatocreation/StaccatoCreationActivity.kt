package com.on.staccato.presentation.staccatocreation

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
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.model.Place
import com.google.android.material.snackbar.Snackbar
import com.on.staccato.R
import com.on.staccato.databinding.ActivityStaccatoCreationBinding
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.common.CustomAutocompleteSupportFragment
import com.on.staccato.presentation.common.GooglePlaceFragmentEventHandler
import com.on.staccato.presentation.common.LocationPermissionManager
import com.on.staccato.presentation.common.LocationPermissionManager.Companion.locationPermissions
import com.on.staccato.presentation.common.PhotoAttachFragment
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.on.staccato.presentation.staccato.StaccatoFragment.Companion.STACCATO_ID_KEY
import com.on.staccato.presentation.staccatocreation.adapter.AttachedPhotoItemTouchHelperCallback
import com.on.staccato.presentation.staccatocreation.adapter.PhotoAttachAdapter
import com.on.staccato.presentation.staccatocreation.dialog.MemorySelectionFragment
import com.on.staccato.presentation.staccatocreation.dialog.VisitedAtSelectionFragment
import com.on.staccato.presentation.staccatocreation.model.AttachedPhotoUiModel
import com.on.staccato.presentation.staccatocreation.viewmodel.StaccatoCreationViewModel
import com.on.staccato.presentation.util.getSnackBarWithAction
import com.on.staccato.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@AndroidEntryPoint
class StaccatoCreationActivity :
    GooglePlaceFragmentEventHandler,
    CurrentLocationHandler,
    OnUrisSelectedListener,
    StaccatoCreationHandler,
    BindingActivity<ActivityStaccatoCreationBinding>() {
    override val layoutResourceId = R.layout.activity_staccato_creation
    private val viewModel: StaccatoCreationViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()
    private val memorySelectionFragment by lazy {
        MemorySelectionFragment()
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

    private val fragmentManager: FragmentManager = supportFragmentManager
    private lateinit var photoAttachAdapter: PhotoAttachAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    private val memoryId by lazy { intent.getLongExtra(MEMORY_ID_KEY, DEFAULT_CATEGORY_ID) }
    private val memoryTitle by lazy { intent.getStringExtra(MEMORY_TITLE_KEY) ?: "" }

    private val locationPermissionManager =
        LocationPermissionManager(context = this, activity = this)
    private lateinit var permissionRequestLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var address: String
    private var currentSnackBar: Snackbar? = null

    override fun initStartView(savedInstanceState: Bundle?) {
        viewModel.fetchMemoryCandidates()
        setupPermissionRequestLauncher()
        setupFusedLocationProviderClient()
        initBinding()
        initAdapter()
        initItemTouchHelper()
        initToolbar()
        initMemorySelectionFragment()
        initVisitedAtSelectionFragment()
        observeViewModelData()
        initGooglePlaceSearch()
        showWarningMessage()
        handleError()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isPlaceSearchClicked.value != true) {
            checkLocationSetting()
        }
    }

    override fun onPause() {
        super.onPause()
        val hasPlaceSearchClicked = autocompleteFragment.isVisible
        viewModel.setIsPlaceSearchClicked(hasPlaceSearchClicked)
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

    override fun onMemorySelectionClicked() {
        if (!memorySelectionFragment.isAdded) {
            memorySelectionFragment.show(
                fragmentManager,
                MemorySelectionFragment.TAG,
            )
        }
    }

    override fun onVisitedAtSelectionClicked() {
        if (!visitedAtSelectionFragment.isAdded) {
            viewModel.selectedVisitedAt.value?.let {
                visitedAtSelectionFragment.updateSelectedVisitedAt(it)
            }
            visitedAtSelectionFragment.show(
                fragmentManager,
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
                view = binding.root,
                actionWhenHavePermission = ::fetchCurrentLocationAddress,
            )
    }

    private fun checkLocationSetting(isCurrentLocationCallClicked: Boolean = false) {
        locationPermissionManager.checkLocationSetting(
            actionWhenHavePermission = {
                fetchCurrentLocationAddress(
                    isCurrentLocationCallClicked,
                )
            },
        )
    }

    private fun setupFusedLocationProviderClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun fetchCurrentLocationAddress(isCurrentLocationCallClicked: Boolean = false) {
        val isLocationPermissionGranted = locationPermissionManager.checkSelfLocationPermission()
        val shouldShowRequestLocationPermissionsRationale =
            locationPermissionManager.shouldShowRequestLocationPermissionsRationale()

        when {
            isLocationPermissionGranted -> {
                viewModel.setCurrentLocationLoading(true)
                val currentLocation: Task<Location> =
                    fusedLocationProviderClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null,
                    )
                currentLocation.addOnSuccessListener { location ->
                    fetchAddress(location)
                }
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

    private fun observeIsPermissionCancelClicked(requestLocationPermissions: () -> Unit) {
        sharedViewModel.isPermissionCancelClicked.observe(this) { isCancel ->
            if (!isCancel) requestLocationPermissions()
        }
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.staccatoCreationHandler = this
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
        binding.toolbarStaccatoCreation.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initMemorySelectionFragment() {
        memorySelectionFragment.setOnMemorySelected { selectedMemory ->
            viewModel.selectMemory(selectedMemory)
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
        observeMemoryData()
        observeCreatedStaccatoId()
    }

    private fun observePhotoData() {
        viewModel.isAddPhotoClicked.observe(this) {
            if (!photoAttachFragment.isAdded && it) {
                photoAttachFragment.show(fragmentManager, PhotoAttachFragment.TAG)
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
                visitedAtSelectionFragment.updateSelectedVisitedAt(it)
                if (memoryId == DEFAULT_CATEGORY_ID) {
                    viewModel.updateMemorySelectionBy(it)
                }
            }
        }
    }

    private fun observeMemoryData() {
        viewModel.memoryCandidates.observe(this) {
            it?.let {
                if (memoryId == DEFAULT_CATEGORY_ID) {
                    visitedAtSelectionFragment.initCalendarByPeriod()
                }
                viewModel.initMemoryAndVisitedAt(memoryId, LocalDateTime.now())
            }
        }
        viewModel.selectableMemories.observe(this) {
            it?.let {
                memorySelectionFragment.setItems(it.categoryCandidates)
            }
        }
        viewModel.selectedMemory.observe(this) {
            it?.let {
                memorySelectionFragment.updateKeyMemory(it)
                if (memoryId != DEFAULT_CATEGORY_ID) {
                    visitedAtSelectionFragment.initCalendarByPeriod(
                        it.startAt,
                        it.endAt,
                    )
                }
            }
        }
    }

    private fun observeCreatedStaccatoId() {
        viewModel.createdStaccatoId.observe(this) { createdStaccatoId ->
            val resultIntent =
                Intent()
                    .putExtra(STACCATO_ID_KEY, createdStaccatoId)
                    .putExtra(MEMORY_ID_KEY, memoryId)
                    .putExtra(MEMORY_TITLE_KEY, memoryTitle)
            setResult(RESULT_OK, resultIntent)
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
            viewModel.setPlaceByCurrentAddress(address, location)
        }
    }

    private fun updateAddressByCurrentAddress(location: Location) {
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

    private fun showWarningMessage() {
        viewModel.warningMessage.observe(this) { message ->
            window.clearFlags(FLAG_NOT_TOUCHABLE)
            showToast(message)
        }
    }

    private fun handleError() {
        viewModel.error.observe(this) { error ->
            when (error) {
                is StaccatoCreationError.MemoryCandidates -> handleMemoryCandidatesFail(error)
                is StaccatoCreationError.StaccatoCreation -> handleStaccatoCreateFail(error)
            }
        }
    }

    private fun handleMemoryCandidatesFail(error: StaccatoCreationError.MemoryCandidates) {
        finish()
        showToast(error.message)
    }

    private fun handleStaccatoCreateFail(error: StaccatoCreationError.StaccatoCreation) {
        window.clearFlags(FLAG_NOT_TOUCHABLE)
        showExceptionSnackBar(error.message) { reCreateStaccato() }
    }

    private fun reCreateStaccato() {
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
        const val MEMORY_TITLE_KEY = "memoryTitle"
        const val DEFAULT_CATEGORY_ID = 0L
        private const val DEFAULT_CATEGORY_TITLE = ""

        fun startWithResultLauncher(
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
            memoryId: Long = DEFAULT_CATEGORY_ID,
            memoryTitle: String = DEFAULT_CATEGORY_TITLE,
        ) {
            Intent(context, StaccatoCreationActivity::class.java).apply {
                putExtra(MEMORY_ID_KEY, memoryId)
                putExtra(MEMORY_TITLE_KEY, memoryTitle)
                activityLauncher.launch(this)
            }
        }
    }
}
