package dev.mikkkkkkka.whatiknow.ui.workspace

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.mikkkkkkka.whatiknow.BuildConfig
import dev.mikkkkkkka.whatiknow.databinding.ActivityWorkspaceBinding
import dev.mikkkkkkka.whatiknow.domain.usecase.auth.IsSignedInUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.auth.LogoutUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.mark.SyncMarksUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.note.SyncNotesUseCase
import dev.mikkkkkkka.whatiknow.ui.auth.AuthActivity
import dev.mikkkkkkka.whatiknow.ui.mark.MarkActivity
import dev.mikkkkkkka.whatiknow.ui.note.NoteFragment
import java.io.IOException
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@AndroidEntryPoint
class WorkspaceActivity : AppCompatActivity(), WorkspaceFragment.Callbacks {

    @Inject lateinit var isSignedInUseCase: IsSignedInUseCase
    @Inject lateinit var logoutUseCase: LogoutUseCase
    @Inject lateinit var syncNotesUseCase: SyncNotesUseCase
    @Inject lateinit var syncMarksUseCase: SyncMarksUseCase

    private lateinit var binding: ActivityWorkspaceBinding
    private var isTwoPane = false
    private var currentNoteId: String? = null
    private var isShowingNoteInSinglePane = false
    private val authLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (isSignedInUseCase()) {
            runSync(showSuccessToast = true)
        }
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!isTwoPane && isShowingNoteInSinglePane) {
                showWorkspacePane()
            } else {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkspaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, backPressedCallback)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        isTwoPane = resources.getBoolean(dev.mikkkkkkka.whatiknow.R.bool.is_two_pane)
        currentNoteId = savedInstanceState?.getString(STATE_CURRENT_NOTE_ID)
        isShowingNoteInSinglePane = savedInstanceState?.getBoolean(STATE_SHOWING_NOTE)
            ?: intent.hasExtra(EXTRA_NOTE_ID)

        if (savedInstanceState == null) {
            currentNoteId = intent.getStringExtra(EXTRA_NOTE_ID)
        }

        ensureWorkspaceFragment()
        ensureNoteFragment(currentNoteId)
        syncVisiblePanes()

        if (isSignedInUseCase()) {
            runSync()
        } else {
            authLauncher.launch(AuthActivity.createIntent(this))
        }
    }

    override fun onOpenNote(noteId: String) {
        showNote(noteId)
    }

    override fun onCreateNote() {
        showNote(noteId = null)
    }

    override fun onOpenMarks() {
        val intent = Intent(this, MarkActivity::class.java)
        startActivity(intent)
    }

    override fun onSync() {
        if (isSignedInUseCase()) {
            runSync(showSuccessToast = true)
        } else {
            authLauncher.launch(AuthActivity.createIntent(this))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(STATE_CURRENT_NOTE_ID, currentNoteId)
        outState.putBoolean(STATE_SHOWING_NOTE, isShowingNoteInSinglePane)
        super.onSaveInstanceState(outState)
    }

    private fun ensureWorkspaceFragment() {
        if (supportFragmentManager.findFragmentById(binding.workspaceContainer.id) != null) {
            return
        }

        supportFragmentManager.beginTransaction()
            .replace(binding.workspaceContainer.id, WorkspaceFragment())
            .commit()
    }

    private fun ensureNoteFragment(noteId: String?) {
        if (supportFragmentManager.findFragmentById(binding.noteContainer.id) != null) {
            return
        }

        supportFragmentManager.beginTransaction()
            .replace(binding.noteContainer.id, NoteFragment.create(noteId))
            .commit()
    }

    private fun showNote(noteId: String?) {
        currentNoteFragment()?.saveContentImmediately()
        currentNoteId = noteId
        isShowingNoteInSinglePane = true

        supportFragmentManager.beginTransaction()
            .replace(binding.noteContainer.id, NoteFragment.create(noteId))
            .commit()

        syncVisiblePanes()
    }

    private fun showWorkspacePane() {
        currentNoteFragment()?.saveContentImmediately()
        isShowingNoteInSinglePane = false
        syncVisiblePanes()
    }

    private fun syncVisiblePanes() {
        binding.workspaceContainer.isVisible = isTwoPane || !isShowingNoteInSinglePane
        binding.noteContainer.isVisible = isTwoPane || isShowingNoteInSinglePane
    }

    private fun currentNoteFragment(): NoteFragment? {
        return supportFragmentManager.findFragmentById(binding.noteContainer.id) as? NoteFragment
    }

    private fun runSync(showSuccessToast: Boolean = false) {
        lifecycleScope.launch {
            val failure = runCatching {
                syncNotesUseCase()
                syncMarksUseCase()
            }.exceptionOrNull()

            if (failure is HttpException && failure.code() == 401) {
                logoutUseCase()
                Toast.makeText(
                    this@WorkspaceActivity,
                    "Session expired, sign in again",
                    Toast.LENGTH_SHORT
                ).show()
                authLauncher.launch(AuthActivity.createIntent(this@WorkspaceActivity))
                return@launch
            }

            if (showSuccessToast) {
                val message = syncResultMessage(failure)
                Toast.makeText(this@WorkspaceActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun syncResultMessage(failure: Throwable?): String {
        return when (failure) {
            null -> "Sync finished"
            is HttpException -> "Sync failed (${failure.code()}), local changes kept"
            is IOException -> "Sync failed: can't reach ${BuildConfig.API_BASE_URL}"
            else -> "Sync failed, local changes kept"
        }
    }

    companion object {
        const val EXTRA_NOTE_ID = "note_id"
        private const val STATE_CURRENT_NOTE_ID = "state_current_note_id"
        private const val STATE_SHOWING_NOTE = "state_showing_note"

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
                putExtra(EXTRA_NOTE_ID, null as String?)
            }
        }
    }
}
