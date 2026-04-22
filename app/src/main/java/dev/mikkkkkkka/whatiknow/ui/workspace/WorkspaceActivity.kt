package dev.mikkkkkkka.whatiknow.ui.workspace

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import dev.mikkkkkkka.whatiknow.ui.auth.AuthActivity
import dev.mikkkkkkka.whatiknow.ui.mark.MarkActivity
import dev.mikkkkkkka.whatiknow.ui.theme.WhatIKnowTheme

@AndroidEntryPoint
class WorkspaceActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = ViewModelProvider(this)[WorkspaceViewModel::class.java]
        viewModel.openNoteFromIntent(intent.getStringExtra(EXTRA_NOTE_ID))
        setContent {
            WhatIKnowTheme {
                WorkspaceRoute(
                    viewModel = viewModel,
                    onOpenMark = {
                        startActivity(MarkActivity.createIntent(this))
                    },
                    onOpenAuth = {
                        startActivity(AuthActivity.createIntent(this))
                    },
                )
            }
        }
    }

    companion object {
        const val EXTRA_NOTE_ID = "note_id"

        fun createIntent(context: Context): Intent {
            return Intent(context, WorkspaceActivity::class.java)
        }

        fun createIntent(context: Context, noteId: String): Intent {
            return Intent(context, WorkspaceActivity::class.java).apply {
                putExtra(EXTRA_NOTE_ID, noteId)
            }
        }

        fun createNewNoteIntent(context: Context): Intent {
            return Intent(context, WorkspaceActivity::class.java).apply {
                putExtra(EXTRA_NOTE_ID, "")
            }
        }
    }
}
