package dev.mikkkkkkka.whatiknow.data.remote.bdui

data class BduiScreen(
    val schemaVersion: Int,
    val id: String,
    val title: String,
    val subtitle: String,
    val components: List<BduiComponent>,
)

sealed interface BduiComponent {
    val id: String
}

data class HeroComponent(
    override val id: String,
    val eyebrow: String,
    val title: String,
    val body: String,
) : BduiComponent

data class StatsComponent(
    override val id: String,
    val items: List<StatItem>,
) : BduiComponent

data class ActionsComponent(
    override val id: String,
    val items: List<ActionItem>,
) : BduiComponent

data class NoteListComponent(
    override val id: String,
    val title: String,
    val emptyTitle: String,
    val emptyBody: String,
    val itemAction: BduiAction,
) : BduiComponent

data class EditorComponent(
    override val id: String,
    val titleLabel: String,
    val titleHint: String,
    val bodyLabel: String,
    val bodyHint: String,
    val primaryAction: ActionItem,
    val secondaryAction: ActionItem?,
) : BduiComponent

data class AuthFormComponent(
    override val id: String,
    val usernameLabel: String,
    val usernameHint: String,
    val passwordLabel: String,
    val passwordHint: String,
    val primaryAction: ActionItem,
    val secondaryAction: ActionItem,
) : BduiComponent

data class MarkCalendarComponent(
    override val id: String,
    val title: String,
    val subtitle: String,
    val selectAction: BduiAction,
) : BduiComponent

data class MarkEditorComponent(
    override val id: String,
    val bodyLabel: String,
    val bodyHint: String,
    val primaryAction: ActionItem,
    val secondaryAction: ActionItem?,
) : BduiComponent

data class StatItem(
    val label: String,
    val value: String,
)

data class ActionItem(
    val id: String,
    val label: String,
    val action: BduiAction,
)

data class BduiAction(
    val kind: BduiActionKind,
    val destination: BduiDestination? = null,
    val operation: BduiOperation? = null,
    val noteIdBinding: String? = null,
)

enum class BduiActionKind {
    NAVIGATE,
    COMMAND,
}

enum class BduiDestination {
    HOME,
    EDITOR,
    WORKSPACE,
    AUTH,
    MARK,
}

enum class BduiOperation {
    REFRESH,
    SAVE_NOTE,
    DELETE_NOTE,
    LOGIN,
    REGISTER,
    SAVE_MARK,
    DELETE_MARK,
    SELECT_DATE,
}

class BduiSchemaException(message: String) : IllegalArgumentException(message)
