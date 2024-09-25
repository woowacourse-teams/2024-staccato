package com.on.staccato.presentation.momentcreation

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.model.Place
import com.google.android.material.snackbar.Snackbar
import com.on.staccato.R
import com.on.staccato.databinding.ActivityVisitCreationBinding
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.common.CustomAutocompleteSupportFragment
import com.on.staccato.presentation.common.GooglePlaceFragmentEventHandler
import com.on.staccato.presentation.common.PhotoAttachFragment
import com.on.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.on.staccato.presentation.memory.model.MemoryUiModel.Companion.buildDatesInRange
import com.on.staccato.presentation.moment.MomentFragment.Companion.MOMENT_ID_KEY
import com.on.staccato.presentation.momentcreation.adapter.PhotoAttachAdapter
import com.on.staccato.presentation.momentcreation.dialog.MemorySelectionFragment
import com.on.staccato.presentation.momentcreation.dialog.VisitedAtSelectionFragment
import com.on.staccato.presentation.momentcreation.model.AttachedPhotoUiModel
import com.on.staccato.presentation.momentcreation.viewmodel.MomentCreationViewModel
import com.on.staccato.presentation.momentcreation.viewmodel.MomentCreationViewModelFactory
import com.on.staccato.presentation.util.showToast
import com.on.staccato.presentation.visitcreation.adapter.AttachedPhotoItemTouchHelperCallback
import com.on.staccato.presentation.visitcreation.adapter.ItemDragListener

class MomentCreationActivity :
    GooglePlaceFragmentEventHandler,
    PlaceSearchHandler,
    OnUrisSelectedListener,
    MomentCreationHandler,
    BindingActivity<ActivityVisitCreationBinding>() {
    override val layoutResourceId = R.layout.activity_visit_creation
    private val viewModel: MomentCreationViewModel by viewModels { MomentCreationViewModelFactory() }
    private val visitedAtSelectionFragment by lazy {
        VisitedAtSelectionFragment().apply {
            setOnVisitedAtSelected { selectedVisitedAt ->
                viewModel.updateSelectedVisitedAt(selectedVisitedAt)
            }
        }
    }
    private val memorySelectionFragment by lazy {
        MemorySelectionFragment()
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
    private val inputManager: InputMethodManager by lazy {
        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    }
    private val autocompleteFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as CustomAutocompleteSupportFragment
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    showToast(getString(R.string.maps_location_permission_granted_message))
                } else {
                    val snackBar =
                        makeSnackBar(getString(R.string.maps_location_permission_required_message))
                    snackBar.setAction()
                    snackBar.show()
                }
                return
            }
        }
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
        fetchCurrentLocationAddress()
    }

    override fun onUrisSelected(vararg uris: Uri) {
        viewModel.updateSelectedImageUris(arrayOf(*uris))
    }

    override fun onMemorySelectionClicked() {
        if (!memorySelectionFragment.isAdded && memoryId == 0L) {
            memorySelectionFragment.show(
                fragmentManager,
                MemorySelectionFragment.TAG,
            )
        }
    }

    override fun onVisitedAtSelectionClicked() {
        if (!visitedAtSelectionFragment.isAdded) {
            visitedAtSelectionFragment.show(fragmentManager, VisitedAtSelectionFragment.TAG)
        }
    }

    override fun onCreateDoneClicked() {
        window.setFlags(FLAG_NOT_TOUCHABLE, FLAG_NOT_TOUCHABLE)
        viewModel.createMoment()
    }

    override fun initStartView(savedInstanceState: Bundle?) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchCurrentLocationAddress()
        initBinding()
        initAdapter()
        initItemTouchHelper()
        initToolbar()
        initMemorySelectionFragment()
        observeViewModelData()
        initGooglePlaceSearch()
        if (memoryId == 0L) {
            viewModel.fetchMemoriesWithDate()
        } else {
            viewModel.selectMemory(memoryId, memoryTitle)
        }
    }

    private fun Snackbar.setAction() {
        setAction(R.string.snack_bar_move_to_setting) {
            val uri = Uri.fromParts(PhotoAttachFragment.PACKAGE_SCHEME, context.packageName, null)
            val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS).setData(uri)
            startActivity(intent)
        }
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

    private fun fetchCurrentLocationAddress() {
        val isAccessFineLocationGranted =
            ContextCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        val isAccessCoarseLocationGranted =
            ContextCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        if (isAccessFineLocationGranted || isAccessCoarseLocationGranted) {
            val currentLocation =
                fusedLocationProviderClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY,
                    null,
                )
            currentLocation.addOnSuccessListener { location ->
                fetchAddress(location)
            }
            return
        } else {
            requestLocationPermissions()
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
        memorySelectionFragment.setOnMemorySelected { selectedMemory ->
            viewModel.selectMemory(selectedMemory.memoryId, selectedMemory.memoryTitle)
        }
    }

    private fun observeViewModelData() {
        viewModel.selectedMemory.observe(this) {
            val dates = buildDatesInRange(it.startAt, it.endAt)
            viewModel.updateSelectedVisitedAt(null)
//            visitedAtSelectionFragment.setItems(dates)
        }
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
        viewModel.memoryCandidates.observe(this) { memories ->
            memorySelectionFragment.setItems(memories.memoryCandidate)
        }
        viewModel.createdStaccatoId.observe(this) { createdMomentId ->
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

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION,
            ),
            LOCATION_PERMISSION_REQUEST_CODE,
        )
    }

    private fun initGooglePlaceSearch() {
        val placeFields: List<Place.Field> =
            listOf(Place.Field.NAME, Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        autocompleteFragment.setPlaceFields(placeFields)

        lifecycleScope.launchWhenCreated {
            autocompleteFragment.setHint(getString(R.string.visit_creation_place_search))
        }
    }

    private fun makeSnackBar(message: String): Snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)

    companion object {
        const val MEMORY_TITLE_KEY = "memoryTitle"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

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
