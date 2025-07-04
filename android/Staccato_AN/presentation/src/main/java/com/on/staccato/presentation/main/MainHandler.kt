package com.on.staccato.presentation.main

interface MainHandler {
    fun onStaccatoCreationClicked(isPermissionCanceled: Boolean)

    fun onMyPageClicked()

    fun onCurrentLocationClicked()
}
