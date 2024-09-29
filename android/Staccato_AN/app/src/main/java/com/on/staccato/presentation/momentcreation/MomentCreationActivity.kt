package com.on.staccato.presentation.momentcreation

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
import com.on.staccato.databinding.ActivityVisitCreationBinding
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.common.CustomAutocompleteSupportFragment
import com.on.staccato.presentation.common.GooglePlaceFragmentEventHandler
import com.on.staccato.presentation.common.LocationPermissionManager
import com.on.staccato.presentation.common.PhotoAttachFragment
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.on.staccato.presentation.moment.MomentFragment.Companion.STACCATO_ID_KEY
import com.on.staccato.presentation.momentcreation.adapter.PhotoAttachAdapter
import com.on.staccato.presentation.momentcreation.dialog.MemoryVisitedAtSelectionFragment
import com.on.staccato.presentation.momentcreation.model.AttachedPhotoUiModel
import com.on.staccato.presentation.momentcreation.viewmodel.MomentCreationViewModel
import com.on.staccato.presentation.util.showToast
import com.on.staccato.presentation.visitcreation.adapter.AttachedPhotoItemTouchHelperCallback
import com.on.staccato.presentation.visitcreation.adapter.ItemDragListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MomentCreationActivity :
    GooglePlaceFragmentEventHandler,
    PlaceSearchHandler,
    OnUrisSelectedListener,
    MomentCreationHandler,
    BindingActivity<ActivityVisitCreationBinding>() {
    override val layoutResourceId = R.layout.activity_visit_creation
    private val viewModel: MomentCreationViewModel by viewModels()
    private val memoryVisitedAtSelectionFragment by lazy {
        MemoryVisitedAtSelectionFragment()
    }
    private val photoAttachFragment by lazy {
        PhotoAttachFragment().apply { setMultipleAbleOption(true) }
    }
    private val fragmentManager: FragmentManager = supportFragmentManager
    private lateinit var photoAttachAdapter: PhotoAttachAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private val memoryId by lazy { intent.getLongExtra(MEMORY_ID_KEY, 0L) }
    private val memoryTitle by lazy { intent.getStringExtra(MEMORY_TITLE_KEY) ?: "" }
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var address: String
    private val autocompleteFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as CustomAutocompleteSupportFragment
    }
    private val locationPermissionManager = LocationPermissionManager(context = this, activity = this)
    private val locationPermissions = locationPermissionManager.locationPermissions
    private lateinit var permissionRequestLauncher: ActivityResultLauncher<Array<String>>

    private val sharedViewModel: SharedViewModel by viewModels()

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

    override fun onSearchClicked() {
        checkLocationSetting(isCurrentLocationCallClicked = true)
    }

    override fun onUrisSelected(vararg uris: Uri) {
        viewModel.updateSelectedImageUris(arrayOf(*uris))
    }

    override fun onMemorySelectionClicked() {
        if (!memoryVisitedAtSelectionFragment.isAdded) {
            memoryVisitedAtSelectionFragment.show(
                fragmentManager,
                MemoryVisitedAtSelectionFragment.TAG,
            )
        }
    }

    override fun onCreateDoneClicked() {
        window.setFlags(FLAG_NOT_TOUCHABLE, FLAG_NOT_TOUCHABLE)
        viewModel.createMoment()
    }

    override fun initStartView(savedInstanceState: Bundle?) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        viewModel.fetchMemoryCandidates(memoryId)
        setupPermissionRequestLauncher()
        fetchCurrentLocationAddress()
        initBinding()
        initAdapter()
        initItemTouchHelper()
        initToolbar()
        initMemorySelectionFragment()
        observeViewModelData()
        initGooglePlaceSearch()
    }

    override fun onResume() {
        super.onResume()
        checkLocationSetting()
    }

    private fun setupPermissionRequestLauncher() {
        permissionRequestLauncher =
            locationPermissionManager.requestPermissionLauncher(
                activityResultCaller = this,
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

    private fun fetchCurrentLocationAddress(isCurrentLocationCallClicked: Boolean = false) {
        val checkSelfLocationPermission = locationPermissionManager.checkSelfLocationPermission()
        val shouldShowRequestLocationPermissionsRationale =
            locationPermissionManager.shouldShowRequestLocationPermissionsRationale()

        when {
            checkSelfLocationPermission -> {
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
                observeIsPermissionCancelClicked { permissionRequestLauncher.launch(locationPermissions) }
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
        binding.visitCreationHandler = this
        binding.placeSearchHandler = this
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
        binding.toolbarVisitCreation.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initMemorySelectionFragment() {
        memoryVisitedAtSelectionFragment.setOnSelected { selectedMemory, selectedVisitedAt ->
            viewModel.selectMemoryVisitedAt(selectedMemory, selectedVisitedAt)
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
        viewModel.pendingPhotos.observe(this) {
            viewModel.fetchPhotosUrlsByUris(this)
        }
        viewModel.currentPhotos.observe(this) { photos ->
            photoAttachAdapter.submitList(
                listOf(AttachedPhotoUiModel.addPhotoButton).plus(photos.attachedPhotos),
            )
        }
        viewModel.memoryAndVisitedAt.observe(this) { memoryAndVisitedAt ->
            memoryVisitedAtSelectionFragment.initMemoryCandidates(
                memoryAndVisitedAt.first.memoryCandidate.toList(),
                memoryAndVisitedAt.second,
                memoryAndVisitedAt.third,
            )
        }
        viewModel.createdStaccatoId.observe(this) { createdMomentId ->
            val resultIntent =
                Intent()
                    .putExtra(STACCATO_ID_KEY, createdMomentId)
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

    private fun fetchAddress(location: Location) {
        val geocoder = Geocoder(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val geocodeListener = initGeocodeListener(location)
            geocoder.getFromLocation(location.latitude, location.longitude, 1, geocodeListener)
        } else {
            address =
                geocoder.getFromLocation(location.latitude, location.longitude, 1)?.get(0)
                    ?.getAddressLine(0).toString()
            viewModel.setPlaceByCurrentAddress(address, location)
        }
    }

    private fun initGeocodeListener(location: Location) =
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        object : Geocoder.GeocodeListener {
            override fun onGeocode(addresses: MutableList<Address>) {
                address = addresses[0].getAddressLine(0)
                viewModel.setPlaceByCurrentAddress(address, location)
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
