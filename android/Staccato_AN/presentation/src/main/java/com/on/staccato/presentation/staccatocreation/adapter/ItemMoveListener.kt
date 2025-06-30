package com.on.staccato.presentation.staccatocreation.adapter

interface ItemMoveListener {
    fun onItemMove(
        from: Int,
        to: Int,
    )

    fun onStopDrag()
}
