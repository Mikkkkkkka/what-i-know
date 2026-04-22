package dev.mikkkkkkka.whatiknow.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mikkkkkkka.whatiknow.data.remote.BduiRepository
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiAction
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiActionKind
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiOperation
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiScreen
import dev.mikkkkkkka.whatiknow.data.remote.bdui.EmbeddedBduiTemplates
import dev.mikkkkkkka.whatiknow.domain.usecase.auth.LoginUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.auth.RegisterAndLoginUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = true,
    val screen: BduiScreen? = null,
    val templateSource: String = "booting",
    val username: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val completed: Boolean = false,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: BduiRepository,
    private val gson: Gson,
    private val loginUseCase: LoginUseCase,
    private val registerAndLoginUseCase: RegisterAndLoginUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            repository.seedScreenIfMissing("auth", EmbeddedBduiTemplates.auth(gson))
            val snapshot = repository.loadScreen("auth", EmbeddedBduiTemplates.auth(gson))
            _state.update {
                it.copy(
                    isLoading = false,
                    screen = snapshot.screen,
                    templateSource = snapshot.source,
                )
            }
        }
    }

    fun onUsernameChange(value: String) {
        _state.update { it.copy(username = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value, errorMessage = null) }
    }

    fun onAction(action: BduiAction) {
        if (action.kind != BduiActionKind.COMMAND) {
            return
        }
        when (action.operation) {
            BduiOperation.LOGIN -> submit { username, password ->
                loginUseCase(username, password)
            }

            BduiOperation.REGISTER -> submit { username, password ->
                registerAndLoginUseCase(username, password)
            }

            BduiOperation.REFRESH -> refresh()
            else -> Unit
        }
    }

    private fun submit(action: suspend (String, String) -> Unit) {
        val snapshot = _state.value
        val username = snapshot.username.trim()
        val password = snapshot.password
        if (username.isBlank() || password.isBlank()) {
            _state.update { it.copy(errorMessage = "Username and password are required") }
            return
        }

        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching {
                action(username, password)
            }.onSuccess {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        completed = true,
                    )
                }
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Authentication failed",
                    )
                }
            }
        }
    }
}
