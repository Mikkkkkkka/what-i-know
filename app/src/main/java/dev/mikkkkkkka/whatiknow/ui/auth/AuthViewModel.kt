package dev.mikkkkkkka.whatiknow.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mikkkkkkka.whatiknow.domain.usecase.auth.LoginUseCase
import dev.mikkkkkkka.whatiknow.domain.usecase.auth.RegisterAndLoginUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerAndLoginUseCase: RegisterAndLoginUseCase,
) : ViewModel() {

    private val innerState = MutableLiveData(AuthUiState())
    val state: LiveData<AuthUiState> = innerState

    private val innerComplete = MutableLiveData(false)
    val complete: LiveData<Boolean> = innerComplete

    fun login(username: String, password: String) {
        submit(username, password) { sanitizedUsername, rawPassword ->
            loginUseCase(sanitizedUsername, rawPassword)
        }
    }

    fun register(username: String, password: String) {
        submit(username, password) { sanitizedUsername, rawPassword ->
            registerAndLoginUseCase(sanitizedUsername, rawPassword)
        }
    }

    private fun submit(
        username: String,
        password: String,
        action: suspend (String, String) -> Unit,
    ) {
        val sanitizedUsername = username.trim()
        if (sanitizedUsername.isBlank() || password.isBlank()) {
            innerState.value = AuthUiState(errorMessage = "Username and password are required")
            return
        }

        innerState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            runCatching {
                action(sanitizedUsername, password)
            }.onSuccess {
                innerState.value = AuthUiState()
                innerComplete.value = true
            }.onFailure { throwable ->
                innerState.value = AuthUiState(errorMessage = throwable.message ?: "Authentication failed")
            }
        }
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
