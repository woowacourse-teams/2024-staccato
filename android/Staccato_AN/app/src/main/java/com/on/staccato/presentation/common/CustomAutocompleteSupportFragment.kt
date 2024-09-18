package com.on.staccato.presentation.common

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.activityViewModels
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.on.staccato.R
import com.on.staccato.presentation.momentcreation.viewmodel.MomentCreationViewModel

class CustomAutocompleteSupportFragment : AutocompleteSupportFragment() {
    private val viewModel: MomentCreationViewModel by activityViewModels()

    init {
        setOnPlaceSelectedListener(
            object : PlaceSelectionListener {
                override fun onPlaceSelected(p0: Place) {
                    val id = p0.id
                    val name = p0.name
                    val address = p0.address
                    val longitude = p0.latLng?.longitude
                    val latitude = p0.latLng?.latitude
                    if (id != null && name != null && address != null && longitude != null && latitude != null) {
                        viewModel.selectNewPlace(id, name, address, longitude, latitude)
                    }
                }

                override fun onError(p0: Status) {
                }
            },
        )
    }

    override fun onViewCreated(
        view: View,
        p1: Bundle?,
    ) {
        super.onViewCreated(view, p1)
        val editText =
            view.findViewById<EditText>(com.google.android.libraries.places.R.id.places_autocomplete_search_input)
        editText.setTextAppearance(R.style.CustomAutocompleteTextAppearance)

        val searchInput =
            view.findViewById<EditText>(com.google.android.libraries.places.R.id.places_autocomplete_search_input)
        searchInput.setPadding(
            0,
            searchInput.paddingTop,
            0,
            searchInput.paddingBottom,
        )

        val clearButton =
            view.findViewById<AppCompatImageButton>(com.google.android.libraries.places.R.id.places_autocomplete_clear_button)
        clearButton.setOnClickListener {
            resources.getString(R.string.visit_creation_empty_address)
            viewModel.clearPlace()
        }
    }
}
