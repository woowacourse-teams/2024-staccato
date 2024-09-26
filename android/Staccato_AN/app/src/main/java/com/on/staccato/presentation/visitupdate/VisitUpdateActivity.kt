package com.on.staccato.presentation.visitupdate

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.on.staccato.R
import com.on.staccato.databinding.ActivityVisitUpdateBinding
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.common.CustomAutocompleteSupportFragment
import com.on.staccato.presentation.common.GooglePlaceFragmentEventHandler
import com.on.staccato.presentation.common.PhotoAttachFragment
import com.on.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.on.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_TITLE_KEY
import com.on.staccato.presentation.moment.MomentFragment.Companion.MOMENT_ID_KEY
import com.on.staccato.presentation.momentcreation.OnUrisSelectedListener
import com.on.staccato.presentation.momentcreation.PlaceSearchHandler
import com.on.staccato.presentation.momentcreation.adapter.PhotoAttachAdapter
import com.on.staccato.presentation.momentcreation.dialog.MemoryVisitedAtSelectionFragment
import com.on.staccato.presentation.momentcreation.model.AttachedPhotoUiModel
import com.on.staccato.presentation.util.showToast
import com.on.staccato.presentation.visitcreation.adapter.AttachedPhotoItemTouchHelperCallback
import com.on.staccato.presentation.visitcreation.adapter.ItemDragListener
import com.on.staccato.presentation.visitupdate.viewmodel.VisitUpdateViewModel
import com.on.staccato.presentation.visitupdate.viewmodel.VisitUpdateViewModelFactory

class VisitUpdateActivity :
    GooglePlaceFragmentEventHandler,
    PlaceSearchHandler,
    OnUrisSelectedListener,
    VisitUpdateHandler,
    BindingActivity<ActivityVisitUpdateBinding>() {
    override val layoutResourceId = R.layout.activity_visit_update
    private val viewModel: VisitUpdateViewModel by viewModels { VisitUpdateViewModelFactory() }
    private val memoryVisitedAtSelectionFragment by lazy {
        MemoryVisitedAtSelectionFragment()
    }
    private val photoAttachFragment by lazy {
        PhotoAttachFragment().apply { setMultipleAbleOption(true) }
    }
    private val fragmentManager: FragmentManager = supportFragmentManager
    private lateinit var photoAttachAdapter: PhotoAttachAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private val staccatoId by lazy { intent.getLongExtra(MOMENT_ID_KEY, 0L) }
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
        if (!memoryVisitedAtSelectionFragment.isAdded) {
            memoryVisitedAtSelectionFragment.show(
                fragmentManager,
                MemoryVisitedAtSelectionFragment.TAG,
            )
        }
    }

    override fun onUpdateDoneClicked() {
        window.setFlags(FLAG_NOT_TOUCHABLE, FLAG_NOT_TOUCHABLE)
        viewModel.updateVisit(staccatoId)
    }

    override fun initStartView(savedInstanceState: Bundle?) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        initBinding()
        initToolbar()
        initMemorySelectionFragment()
        initAdapter()
        initItemTouchHelper()
        observeViewModelData()
        initGooglePlaceSearch()
        viewModel.initViewModelData(staccatoId, memoryId, memoryTitle)
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
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        val isAccessCoarseLocationGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION,
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
        viewModel.memoryAndVisitedAt.observe(this) { memoryAndVisitedAt ->
            memoryVisitedAtSelectionFragment.initMemoryCandidates(
                memoryAndVisitedAt.first.memoryCandidate.toList(),
                memoryAndVisitedAt.second,
                memoryAndVisitedAt.third,
            )
        }
        viewModel.errorMessage.observe(this) { message ->
            handleError(message)
        }
    }

    private fun handleUpdateComplete(isUpdateCompleted: Boolean) {
        if (isUpdateCompleted) {
            val intent =
                Intent()
                    .putExtra(MOMENT_ID_KEY, staccatoId)
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
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
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

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        fun startWithResultLauncher(
            visitId: Long,
            memoryId: Long,
            memoryTitle: String,
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, VisitUpdateActivity::class.java).apply {
                putExtra(MOMENT_ID_KEY, visitId)
                putExtra(MEMORY_ID_KEY, memoryId)
                putExtra(MEMORY_TITLE_KEY, memoryTitle)
                activityLauncher.launch(this)
            }
        }
    }
}
