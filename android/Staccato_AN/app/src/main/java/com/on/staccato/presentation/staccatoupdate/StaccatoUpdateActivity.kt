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
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.model.Place
import com.on.staccato.R
import com.on.staccato.databinding.ActivityVisitUpdateBinding
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.common.CustomAutocompleteSupportFragment
import com.on.staccato.presentation.common.GooglePlaceFragmentEventHandler
import com.on.staccato.presentation.common.LocationPermissionManager
import com.on.staccato.presentation.common.LocationPermissionManager.Companion.locationPermissions
import com.on.staccato.presentation.common.PhotoAttachFragment
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.on.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_TITLE_KEY
import com.on.staccato.presentation.staccato.StaccatoFragment.Companion.STACCATO_ID_KEY
import com.on.staccato.presentation.staccatocreation.CurrentLocationHandler
import com.on.staccato.presentation.staccatocreation.OnUrisSelectedListener
import com.on.staccato.presentation.staccatocreation.adapter.AttachedPhotoItemTouchHelperCallback
import com.on.staccato.presentation.staccatocreation.adapter.ItemDragListener
import com.on.staccato.presentation.staccatocreation.adapter.PhotoAttachAdapter
import com.on.staccato.presentation.staccatocreation.dialog.MemorySelectionFragment
import com.on.staccato.presentation.staccatocreation.dialog.VisitedAtSelectionFragment
import com.on.staccato.presentation.staccatocreation.model.AttachedPhotoUiModel
import com.on.staccato.presentation.staccatoupdate.viewmodel.StaccatoUpdateViewModel
import com.on.staccato.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

@AndroidEntryPoint
class StaccatoUpdateActivity :
    GooglePlaceFragmentEventHandler,
    CurrentLocationHandler,
    OnUrisSelectedListener,
    StaccatoUpdateHandler,
    BindingActivity<ActivityVisitUpdateBinding>() {
    override val layoutResourceId = R.layout.activity_visit_update
    private val viewModel: StaccatoUpdateViewModel by viewModels()
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
    private val fragmentManager: FragmentManager = supportFragmentManager
    private lateinit var photoAttachAdapter: PhotoAttachAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private val staccatoId by lazy { intent.getLongExtra(STACCATO_ID_KEY, 0L) }
    private val memoryId by lazy { intent.getLongExtra(MEMORY_ID_KEY, 0L) }
    private val memoryTitle by lazy { intent.getStringExtra(MEMORY_TITLE_KEY) ?: "" }

    private val autocompleteFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as CustomAutocompleteSupportFragment
    }

    private val locationPermissionManager =
        LocationPermissionManager(context = this, activity = this)
    private lateinit var permissionRequestLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var address: String

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
            visitedAtSelectionFragment.show(
                fragmentManager,
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
        setupFusedLocationProviderClient()
        initBinding()
        initToolbar()
        initMemorySelectionFragment()
        initVisitedAtSelectionFragment()
        initAdapter()
        initItemTouchHelper()
        observeViewModelData()
        initGooglePlaceSearch()
        viewModel.fetchTargetData(staccatoId, memoryId, memoryTitle)
    }

    override fun onResume() {
        super.onResume()
        observeIsSettingClicked()
    }

    private fun setupPermissionRequestLauncher() {
        permissionRequestLauncher =
            locationPermissionManager.requestPermissionLauncher(
                view = binding.root,
                actionWhenHavePermission = ::fetchCurrentLocationAddress,
            )
    }

    private fun checkLocationSetting() {
        locationPermissionManager.checkLocationSetting(
            actionWhenHavePermission = { fetchCurrentLocationAddress() },
        )
    }

    private fun setupFusedLocationProviderClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun fetchCurrentLocationAddress() {
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

            if (isSettingClicked && checkSelfLocationPermission) {
                val currentLocation: Task<Location> =
                    fusedLocationProviderClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null,
                    )
                currentLocation.addOnSuccessListener { location ->
                    fetchAddress(location)
                }
            }
        }
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.visitUpdateHandler = this
        binding.currentLocationHandler = this
    }

    private fun initAdapter() {
        photoAttachAdapter =
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
        binding.rvPhotoAttach.adapter = photoAttachAdapter
    }

    private fun initItemTouchHelper() {
        itemTouchHelper = ItemTouchHelper(AttachedPhotoItemTouchHelperCallback(photoAttachAdapter))
        itemTouchHelper.attachToRecyclerView(binding.rvPhotoAttach)
    }

    private fun initToolbar() {
        binding.toolbarVisitUpdate.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initMemorySelectionFragment() {
        memorySelectionFragment.setOnMemorySelected { selectedMemory ->
            val startAt = selectedMemory.startAt ?: LocalDate.now()
            val initializedDateTime =
                LocalDateTime.of(startAt.year, startAt.month, startAt.dayOfMonth, 0, 0, 0)
            viewModel.selectedVisitedAt(initializedDateTime)
            viewModel.selectMemory(selectedMemory)
        }
    }

    private fun initVisitedAtSelectionFragment() {
        visitedAtSelectionFragment.setOnVisitedAtSelected { selectedVisitedAt ->
            viewModel.selectedVisitedAt(selectedVisitedAt)
        }
    }

    private fun observeViewModelData() {
        viewModel.placeName.observe(this) {
            autocompleteFragment.setText(it)
        }
        viewModel.isAddPhotoClicked.observe(this) {
            if (!photoAttachFragment.isAdded && it) {
                photoAttachFragment.show(fragmentManager, PhotoAttachFragment.TAG)
            }
        }
        viewModel.isUpdateCompleted.observe(this) { isUpdateCompleted ->
            handleUpdateComplete(isUpdateCompleted)
        }
        viewModel.pendingPhotos.observe(this) {
            viewModel.fetchPhotosUrlsByUris(this)
        }
        viewModel.currentPhotos.observe(this) { photos ->
            photoAttachAdapter.submitList(
                listOf(AttachedPhotoUiModel.addPhotoButton).plus(photos.attachedPhotos),
            )
        }
        viewModel.memoryCandidates.observe(this) {
            memorySelectionFragment.setItems(it.memoryCandidate)
        }
        viewModel.selectedMemory.observe(this) { selectedMemory ->
            val startAt = selectedMemory.startAt ?: LocalDate.now()
            val initializedDateTime =
                LocalDateTime.of(startAt.year, startAt.month, startAt.dayOfMonth, 0, 0, 0)
            memorySelectionFragment.updateKeyMemory(selectedMemory)
            visitedAtSelectionFragment.initDateCandidates(selectedMemory, initializedDateTime)
        }
        viewModel.selectedVisitedAt.observe(this) { selectedVisitedAt ->
            if (selectedVisitedAt != null) {
                visitedAtSelectionFragment.initKeyWithSelectedValues(selectedVisitedAt)
            }
        }
        viewModel.errorMessage.observe(this) { message ->
            handleError(message)
        }
    }

    private fun handleUpdateComplete(isUpdateCompleted: Boolean) {
        if (isUpdateCompleted) {
            val intent =
                Intent()
                    .putExtra(STACCATO_ID_KEY, staccatoId)
                    .putExtra(MEMORY_ID_KEY, memoryId)
                    .putExtra(MEMORY_TITLE_KEY, memoryTitle)
            setResult(RESULT_OK, intent)
            window.clearFlags(FLAG_NOT_TOUCHABLE)
            finish()
        }
    }

    private fun handleError(errorMessage: String) {
        window.clearFlags(FLAG_NOT_TOUCHABLE)
        showToast(errorMessage)
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
                showToast(getString(R.string.moment_creation_not_found_address))
            }
        }

    private fun initGooglePlaceSearch() {
        val placeFields: List<Place.Field> =
            listOf(Place.Field.NAME, Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        autocompleteFragment.setPlaceFields(placeFields)

        lifecycleScope.launchWhenCreated {
            autocompleteFragment.setHint(getString(R.string.visit_creation_place_search))
        }
    }

    companion object {
        fun startWithResultLauncher(
            visitId: Long,
            memoryId: Long,
            memoryTitle: String,
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, StaccatoUpdateActivity::class.java).apply {
                putExtra(STACCATO_ID_KEY, visitId)
                putExtra(MEMORY_ID_KEY, memoryId)
                putExtra(MEMORY_TITLE_KEY, memoryTitle)
                activityLauncher.launch(this)
            }
        }
    }
}
