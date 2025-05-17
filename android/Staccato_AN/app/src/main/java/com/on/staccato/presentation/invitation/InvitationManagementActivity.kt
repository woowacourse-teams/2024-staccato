package com.on.staccato.presentation.invitation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class InvitationManagementActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InvitationManagementScreen(
                onNavigationClick = { finish() }
            )
        }
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, InvitationManagementActivity::class.java))
        }
    }
}
