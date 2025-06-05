package com.on.staccato.presentation.util

import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import com.on.staccato.R

fun interface MenuHandler {
    fun setupActionBy(menuItemId: Int)
}

fun View.showPopupMenu(
    @MenuRes menuRes: Int,
    menuHandler: MenuHandler,
    gravity: Int = Gravity.END,
) {
    val popup = inflateCreationMenu(gravity, menuRes)
    setUpCreationMenu(popup, menuHandler)
    popup.show()
}

private fun View.inflateCreationMenu(
    gravity: Int,
    @MenuRes menuRes: Int,
): PopupMenu {
    val popup =
        PopupMenu(context, this, gravity, 0, R.style.Theme_Staccato_AN_PopupMenu)
    popup.menuInflater.inflate(menuRes, popup.menu)
    return popup
}

private fun setUpCreationMenu(
    popup: PopupMenu,
    handler: MenuHandler,
) {
    popup.setOnMenuItemClickListener { menuItem ->
        handler.setupActionBy(menuItem.itemId)
        false
    }
}
