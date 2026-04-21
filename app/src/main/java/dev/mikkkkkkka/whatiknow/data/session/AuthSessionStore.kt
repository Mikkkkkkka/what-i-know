package dev.mikkkkkkka.whatiknow.data.session

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class AuthSessionStore @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) {
    fun token(): String? = sharedPreferences.getString(KEY_TOKEN, null)

    fun username(): String? = sharedPreferences.getString(KEY_USERNAME, null)

    fun isSignedIn(): Boolean = !token().isNullOrBlank()

    fun save(username: String, token: String) {
        sharedPreferences.edit {
            putString(KEY_USERNAME, username)
                .putString(KEY_TOKEN, token)
        }
    }

    fun clear() {
        sharedPreferences.edit {
            remove(KEY_USERNAME)
                .remove(KEY_TOKEN)
        }
    }

    companion object {
        private const val KEY_USERNAME = "username"
        private const val KEY_TOKEN = "token"
        const val USER_ROUTE_ID = "me"
    }
}
