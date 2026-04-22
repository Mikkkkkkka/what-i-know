package dev.mikkkkkkka.whatiknow.ui.mark

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import dev.mikkkkkkka.whatiknow.ui.theme.WhatIKnowTheme
import dev.mikkkkkkka.whatiknow.ui.workspace.WorkspaceActivity

@AndroidEntryPoint
class MarkActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel = ViewModelProvider(this)[MarkViewModel::class.java]
        setContent {
            WhatIKnowTheme {
                MarkRoute(
                    viewModel = viewModel,
                    onOpenWorkspace = {
                        startActivity(WorkspaceActivity.createIntent(this))
                        finish()
                    },
                )
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, MarkActivity::class.java)
    }
}
