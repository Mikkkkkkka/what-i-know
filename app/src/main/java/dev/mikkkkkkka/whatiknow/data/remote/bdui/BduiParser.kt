package dev.mikkkkkkka.whatiknow.data.remote.bdui

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

object BduiParser {

    fun parseScreen(payload: JsonObject): BduiScreen {
        val schemaVersion = payload.requiredInt("schemaVersion")
        if (schemaVersion != CURRENT_SCHEMA_VERSION) {
            throw BduiSchemaException("Unsupported schema version: $schemaVersion")
        }
        return BduiScreen(
            schemaVersion = schemaVersion,
            id = payload.requiredString("id"),
            title = payload.requiredString("title"),
            subtitle = payload.optionalString("subtitle"),
            components = payload.requiredArray("components").mapIndexed { index, element ->
                parseComponent(index, element.asJsonObject)
            },
        )
    }

    private fun parseComponent(index: Int, payload: JsonObject): BduiComponent {
        val id = payload.optionalString("id").ifBlank { "component_$index" }
        return when (payload.requiredString("type")) {
            "hero" -> HeroComponent(
                id = id,
                eyebrow = payload.requiredString("eyebrow"),
                title = payload.requiredString("title"),
                body = payload.requiredString("body"),
            )

            "stats" -> StatsComponent(
                id = id,
                items = payload.requiredArray("items").mapIndexed { itemIndex, element ->
                    val item = element.asJsonObject
                    StatItem(
                        label = item.requiredString("label"),
                        value = item.requiredString("value"),
                    )
                },
            )

            "actions" -> ActionsComponent(
                id = id,
                items = payload.requiredArray("items").mapIndexed { itemIndex, element ->
                    parseActionItem(
                        payload = element.asJsonObject,
                        fallbackId = "${id}_action_$itemIndex",
                    )
                },
            )

            "note_list" -> NoteListComponent(
                id = id,
                title = payload.requiredString("title"),
                emptyTitle = payload.requiredString("emptyTitle"),
                emptyBody = payload.requiredString("emptyBody"),
                itemAction = parseAction(payload.requiredObject("itemAction")),
            )

            "editor" -> EditorComponent(
                id = id,
                titleLabel = payload.requiredString("titleLabel"),
                titleHint = payload.requiredString("titleHint"),
                bodyLabel = payload.requiredString("bodyLabel"),
                bodyHint = payload.requiredString("bodyHint"),
                primaryAction = parseActionItem(
                    payload = payload.requiredObject("primaryAction"),
                    fallbackId = "${id}_primary",
                ),
                secondaryAction = payload.optionalObject("secondaryAction")?.let {
                    parseActionItem(
                        payload = it,
                        fallbackId = "${id}_secondary",
                    )
                },
            )

            "auth_form" -> AuthFormComponent(
                id = id,
                usernameLabel = payload.requiredString("usernameLabel"),
                usernameHint = payload.requiredString("usernameHint"),
                passwordLabel = payload.requiredString("passwordLabel"),
                passwordHint = payload.requiredString("passwordHint"),
                primaryAction = parseActionItem(
                    payload = payload.requiredObject("primaryAction"),
                    fallbackId = "${id}_primary",
                ),
                secondaryAction = parseActionItem(
                    payload = payload.requiredObject("secondaryAction"),
                    fallbackId = "${id}_secondary",
                ),
            )

            "mark_calendar" -> MarkCalendarComponent(
                id = id,
                title = payload.requiredString("title"),
                subtitle = payload.optionalString("subtitle"),
                selectAction = parseAction(payload.requiredObject("selectAction")),
            )

            "mark_editor" -> MarkEditorComponent(
                id = id,
                bodyLabel = payload.requiredString("bodyLabel"),
                bodyHint = payload.requiredString("bodyHint"),
                primaryAction = parseActionItem(
                    payload = payload.requiredObject("primaryAction"),
                    fallbackId = "${id}_primary",
                ),
                secondaryAction = payload.optionalObject("secondaryAction")?.let {
                    parseActionItem(
                        payload = it,
                        fallbackId = "${id}_secondary",
                    )
                },
            )

            else -> throw BduiSchemaException("Unsupported component type: ${payload.requiredString("type")}")
        }
    }

    private fun parseActionItem(payload: JsonObject, fallbackId: String): ActionItem {
        return ActionItem(
            id = payload.optionalString("id").ifBlank { fallbackId },
            label = payload.requiredString("label"),
            action = parseAction(payload.requiredObject("action")),
        )
    }

    private fun parseAction(payload: JsonObject): BduiAction {
        return when (payload.requiredString("kind")) {
            "navigate" -> BduiAction(
                kind = BduiActionKind.NAVIGATE,
                destination = BduiDestination.valueOf(payload.requiredString("destination").uppercase()),
                noteIdBinding = payload.optionalString("noteIdBinding").ifBlank { null },
            )

            "command" -> BduiAction(
                kind = BduiActionKind.COMMAND,
                operation = BduiOperation.valueOf(payload.requiredString("operation").uppercase()),
            )

            else -> throw BduiSchemaException("Unsupported action kind: ${payload.requiredString("kind")}")
        }
    }

    private fun JsonObject.requiredString(key: String): String {
        val value = optionalString(key)
        if (value.isBlank()) {
            throw BduiSchemaException("Missing string field: $key")
        }
        return value
    }

    private fun JsonObject.optionalString(key: String): String {
        return get(key)?.takeIf { !it.isJsonNull }?.asString.orEmpty()
    }

    private fun JsonObject.requiredInt(key: String): Int {
        val value = get(key)?.takeIf { !it.isJsonNull }?.asInt
        return value ?: throw BduiSchemaException("Missing int field: $key")
    }

    private fun JsonObject.requiredArray(key: String): JsonArray {
        return getAsJsonArray(key) ?: throw BduiSchemaException("Missing array field: $key")
    }

    private fun JsonObject.requiredObject(key: String): JsonObject {
        return getAsJsonObject(key) ?: throw BduiSchemaException("Missing object field: $key")
    }

    private fun JsonObject.optionalObject(key: String): JsonObject? {
        return getAsJsonObject(key)
    }

    private inline fun <T> JsonArray.mapIndexed(transform: (Int, JsonElement) -> T): List<T> {
        val result = ArrayList<T>(size())
        forEachIndexed { index, element ->
            result += transform(index, element)
        }
        return result
    }

    private inline fun JsonArray.forEachIndexed(action: (Int, JsonElement) -> Unit) {
        var index = 0
        for (element in this) {
            action(index, element)
            index += 1
        }
    }

    const val CURRENT_SCHEMA_VERSION = 1
}
