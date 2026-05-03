package dev.mikkkkkkka.whatiknow.data.remote

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonElement
import dev.mikkkkkkka.whatiknow.data.remote.api.EchoApi
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiParser
import dev.mikkkkkkka.whatiknow.data.remote.bdui.BduiScreen
import dev.mikkkkkkka.whatiknow.data.remote.bdui.EmbeddedBduiTemplates
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class BduiNote(
    val id: String,
    val title: String,
    val content: String,
    val updatedAt: String,
)

data class TemplateSnapshot(
    val home: BduiScreen,
    val editor: BduiScreen,
    val mark: BduiScreen,
    val source: String,
)

data class ScreenSnapshot(
    val screen: BduiScreen,
    val source: String,
)

data class NoteSnapshot(
    val notes: List<BduiNote>,
    val source: String,
    val fromRemote: Boolean,
    val message: String? = null,
)

@Singleton
class BduiRepository @Inject constructor(
    private val echoApi: EchoApi,
    private val gson: Gson,
    private val sharedPreferences: SharedPreferences,
) {

    suspend fun seedTemplatesIfMissing() {
        seedTemplateIfMissing("home", EmbeddedBduiTemplates.home(gson))
        seedTemplateIfMissing("editor", EmbeddedBduiTemplates.editor(gson))
        seedTemplateIfMissing("mark", EmbeddedBduiTemplates.mark(gson))
    }

    suspend fun loadTemplates(): TemplateSnapshot {
        val home = loadScreen("home", EmbeddedBduiTemplates.home(gson))
        val editor = loadScreen("editor", EmbeddedBduiTemplates.editor(gson))
        val mark = loadScreen("mark", EmbeddedBduiTemplates.mark(gson))
        val fromRemote = home.source == "cloud" && editor.source == "cloud"

        return TemplateSnapshot(
            home = home.screen,
            editor = editor.screen,
            mark = mark.screen,
            source = if (fromRemote) "cloud" else "embedded",
        )
    }

    suspend fun seedScreenIfMissing(screen: String, payload: JsonElement) {
        seedTemplateIfMissing(screen, payload)
    }

    suspend fun loadScreen(screen: String, embeddedPayload: JsonElement): ScreenSnapshot {
        val embedded = BduiParser.parseScreen(embeddedPayload.asJsonObject)
        val loaded = loadScreen(path = templatePath(screen), fallback = embedded)
        return ScreenSnapshot(
            screen = loaded.screen,
            source = if (loaded.source == TemplateOrigin.REMOTE) "cloud" else "embedded",
        )
    }

    suspend fun loadNotes(): NoteSnapshot {
        return try {
            val element = loadRaw(notesPath())
            val notes = parseNotes(element)
            cacheNotes(notes)
            NoteSnapshot(
                notes = notes.sortedByDescending { it.updatedAt },
                source = "cloud",
                fromRemote = true,
                message = "Synced from Alfa Echo",
            )
        } catch (_: Exception) {
            NoteSnapshot(
                notes = cachedNotes(),
                source = "cache",
                fromRemote = false,
                message = "Cloud unavailable, showing cached notes",
            )
        }
    }

    suspend fun saveNotes(notes: List<BduiNote>): NoteSnapshot {
        return try {
            saveRaw(notesPath(), gson.toJsonTree(notes))
            cacheNotes(notes)
            NoteSnapshot(
                notes = notes.sortedByDescending { it.updatedAt },
                source = "cloud",
                fromRemote = true,
                message = null,
            )
        } catch (_: Exception) {
            cacheNotes(notes)
            NoteSnapshot(
                notes = notes.sortedByDescending { it.updatedAt },
                source = "cache",
                fromRemote = false,
                message = "Saved locally, cloud update failed",
            )
        }
    }

    private suspend fun loadScreen(path: String, fallback: BduiScreen): LoadedScreen {
        val remote = runCatching {
            loadRaw(path)?.asJsonObject?.let(BduiParser::parseScreen)
        }.getOrNull()

        return if (remote != null) {
            LoadedScreen(remote, TemplateOrigin.REMOTE)
        } else {
            LoadedScreen(fallback, TemplateOrigin.EMBEDDED)
        }
    }

    private suspend fun seedTemplateIfMissing(screen: String, payload: JsonElement) {
        if (loadRaw(templatePath(screen)) == null) {
            saveRaw(templatePath(screen), payload)
        }
    }

    private suspend fun loadRaw(path: String): JsonElement? {
        val response = echoApi.get(encode(path))
        if (response.code() == 404) {
            return null
        }
        if (!response.isSuccessful) {
            throw IllegalStateException("Echo GET failed with ${response.code()}")
        }
        return response.body()
    }

    private suspend fun saveRaw(path: String, payload: JsonElement) {
        val response = echoApi.put(encode(path), payload)
        if (!response.isSuccessful) {
            throw IllegalStateException("Echo PUT failed with ${response.code()}")
        }
    }

    private fun templatePath(screen: String): String = "what-i-know-bdui/templates/$screen"

    private fun notesPath(): String = "what-i-know-bdui/notes/${deviceId()}"

    private fun deviceId(): String {
        val cached = sharedPreferences.getString(KEY_DEVICE_ID, null)
        if (cached != null) {
            return cached
        }
        return UUID.randomUUID().toString().also {
            sharedPreferences.edit().putString(KEY_DEVICE_ID, it).apply()
        }
    }

    private fun parseNotes(payload: JsonElement?): List<BduiNote> {
        if (payload == null || !payload.isJsonArray) {
            return emptyList()
        }
        val type = object : com.google.gson.reflect.TypeToken<List<BduiNote>>() {}.type
        return gson.fromJson(payload, type) ?: emptyList()
    }

    private fun cacheNotes(notes: List<BduiNote>) {
        sharedPreferences.edit()
            .putString(KEY_NOTES_CACHE, gson.toJson(notes))
            .apply()
    }

    private fun cachedNotes(): List<BduiNote> {
        val payload = sharedPreferences.getString(KEY_NOTES_CACHE, null) ?: return emptyList()
        val type = object : com.google.gson.reflect.TypeToken<List<BduiNote>>() {}.type
        return gson.fromJson(payload, type) ?: emptyList()
    }

    private fun encode(path: String): String {
        return URLEncoder.encode(path, StandardCharsets.UTF_8.toString())
    }

    private data class LoadedScreen(
        val screen: BduiScreen,
        val source: TemplateOrigin,
    )

    private enum class TemplateOrigin {
        REMOTE,
        EMBEDDED,
    }

    private companion object {
        const val KEY_DEVICE_ID = "bdui_device_id"
        const val KEY_NOTES_CACHE = "bdui_notes_cache"
    }
}
