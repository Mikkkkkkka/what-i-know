package dev.mikkkkkkka.whatiknow.data.remote.bdui

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BduiParserTest {

    private val gson = Gson()

    @Test
    fun `parses embedded home template into typed screen`() {
        val payload = EmbeddedBduiTemplates.home(gson)

        val screen = BduiParser.parseScreen(payload)

        assertEquals("home", screen.id)
        assertEquals(4, screen.components.size)
        assertTrue(screen.components[2] is ActionsComponent)
        val noteList = screen.components[3] as NoteListComponent
        assertEquals(BduiDestination.EDITOR, noteList.itemAction.destination)
        assertEquals("{noteId}", noteList.itemAction.noteIdBinding)
    }

    @Test(expected = BduiSchemaException::class)
    fun `rejects unsupported schema version`() {
        val payload = JsonObject().apply {
            addProperty("schemaVersion", 99)
            addProperty("id", "bad-screen")
            addProperty("title", "Bad")
            addProperty("subtitle", "")
            add("components", gson.fromJson("[]", com.google.gson.JsonArray::class.java))
        }

        BduiParser.parseScreen(payload)
    }
}
