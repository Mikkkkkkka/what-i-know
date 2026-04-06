package dev.mikkkkkkka.whatiknow.data.session

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionStore: AuthSessionStore,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = sessionStore.token()

        val request = if (token.isNullOrBlank()) {
            originalRequest
        } else {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }

        return chain.proceed(request)
    }
}
