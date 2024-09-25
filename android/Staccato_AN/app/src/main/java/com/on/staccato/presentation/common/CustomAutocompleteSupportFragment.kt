package com.on.staccato.presentation.common

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageButton
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.on.staccato.R

class CustomAutocompleteSupportFragment :
    AutocompleteSupportFragment() {
    private var handler: GooglePlaceFragmentEventHandler? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is GooglePlaceFragmentEventHandler) handler = context
    }

    override fun onViewCreated(
        view: View,
        p1: Bundle?,
    ) {
        super.onViewCreated(view, p1)
        if (handler != null) {
            setupPlaceSelectionListener()
            setupEditText(view)
            setupClearButton(view)
        }
    }

    private fun setupPlaceSelectionListener() {
        setOnPlaceSelectedListener(
            object : PlaceSelectionListener {
                override fun onPlaceSelected(p0: Place) {
                    handleNewPlaceSelected(p0)
                }

                override fun onError(p0: Status) {
                }
            },
        )
    }

    private fun setupEditText(view: View) {
        view.findViewById<EditText>(com.google.android.libraries.places.R.id.places_autocomplete_search_input)
            .apply {
                setTextAppearance(R.style.CustomAutocompleteTextAppearance)
                setPadding(
                    0,
                    paddingTop,
                    0,
                    paddingBottom,
                )
            }
    }

    private fun setupClearButton(view: View) {
        val clearButton =
            view.findViewById<AppCompatImageButton>(com.google.android.libraries.places.R.id.places_autocomplete_clear_button)
        clearButton.setOnClickListener {
            handler!!.onSelectedPlaceCleared()
        }
    }

    private fun handleNewPlaceSelected(p0: Place) {
        val id = p0.id ?: return
        val name = p0.name ?: return
        val address = p0.address ?: return
        val longitude = p0.latLng?.longitude ?: return
        val latitude = p0.latLng?.latitude ?: return
        handler!!.onNewPlaceSelected(id, name, address, longitude, latitude)
    }

    override fun onDetach() {
        super.onDetach()
        handler = null
    }
}
