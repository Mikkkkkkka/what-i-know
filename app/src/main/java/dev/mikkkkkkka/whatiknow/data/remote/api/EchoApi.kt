package dev.mikkkkkkka.whatiknow.data.remote.api

import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface EchoApi {

    @GET("/server/echo/333723/{echoPath}")
    suspend fun get(
        @Path(value = "echoPath", encoded = true) echoPath: String,
    ): Response<JsonElement>

    @PUT("/server/echo/333723/{echoPath}")
    suspend fun put(
        @Path(value = "echoPath", encoded = true) echoPath: String,
        @Body payload: JsonElement,
    ): Response<JsonElement>
}
