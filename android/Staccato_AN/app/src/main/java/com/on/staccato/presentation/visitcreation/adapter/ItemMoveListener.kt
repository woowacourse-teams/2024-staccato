package com.on.staccato.presentation.visitcreation.adapter

interface ItemMoveListener {
    fun onItemMove(
        from: Int,
        to: Int,
    )

    fun onStopDrag()
}