package dev.mikkkkkkka.whatiknow.data.remote.bdui

import com.google.gson.Gson
import com.google.gson.JsonObject

object EmbeddedBduiTemplates {

    fun home(gson: Gson): JsonObject = gson.fromJson(HOME_TEMPLATE_JSON, JsonObject::class.java)

    fun editor(gson: Gson): JsonObject = gson.fromJson(EDITOR_TEMPLATE_JSON, JsonObject::class.java)

    fun auth(gson: Gson): JsonObject = gson.fromJson(AUTH_TEMPLATE_JSON, JsonObject::class.java)

    fun mark(gson: Gson): JsonObject = gson.fromJson(MARK_TEMPLATE_JSON, JsonObject::class.java)

    private const val HOME_TEMPLATE_JSON = """
        {
          "schemaVersion": 1,
          "id": "home",
          "title": "Knowledge Cloud",
          "subtitle": "Server-defined home screen",
          "components": [
            {
              "id": "hero-home",
              "type": "hero",
              "eyebrow": "BDUI",
              "title": "Cloud notes from schema-driven templates",
              "body": "The backend defines component order, copy, navigation, and note interactions."
            },
            {
              "id": "stats-home",
              "type": "stats",
              "items": [
                { "label": "Notes", "value": "{notesCount}" },
                { "label": "Templates", "value": "{templateSource}" },
                { "label": "Sync", "value": "{syncState}" }
              ]
            },
            {
              "id": "actions-home",
              "type": "actions",
              "items": [
                {
                  "id": "create",
                  "label": "New note",
                  "action": {
                    "kind": "navigate",
                    "destination": "editor"
                  }
                },
                {
                  "id": "daily-mark",
                  "label": "Daily mark",
                  "action": {
                    "kind": "navigate",
                    "destination": "mark"
                  }
                },
                {
                  "id": "refresh",
                  "label": "Refresh",
                  "action": {
                    "kind": "command",
                    "operation": "refresh"
                  }
                }
              ]
            },
            {
              "id": "notes-home",
              "type": "note_list",
              "title": "Saved notes",
              "emptyTitle": "No notes yet",
              "emptyBody": "Create the first note. Notes are stored in Echo under a device-specific namespace.",
              "itemAction": {
                "kind": "navigate",
                "destination": "editor",
                "noteIdBinding": "{noteId}"
              }
            }
          ]
        }
    """

    private const val EDITOR_TEMPLATE_JSON = """
        {
          "schemaVersion": 1,
          "id": "editor",
          "title": "Editor",
          "subtitle": "Server-defined editor screen",
          "components": [
            {
              "id": "hero-editor",
              "type": "hero",
              "eyebrow": "Document",
              "title": "{editorModeTitle}",
              "body": "The backend controls labels, actions, and layout order. The client only renders the schema."
            },
            {
              "id": "editor-main",
              "type": "editor",
              "titleLabel": "Title",
              "titleHint": "What did you learn today?",
              "bodyLabel": "Body",
              "bodyHint": "Capture the note, decision or result.",
              "primaryAction": {
                "id": "save",
                "label": "Save to cloud",
                "action": {
                  "kind": "command",
                  "operation": "save_note"
                }
              },
              "secondaryAction": {
                "id": "delete",
                "label": "Delete note",
                "action": {
                  "kind": "command",
                  "operation": "delete_note"
                }
              }
            },
            {
              "id": "actions-editor",
              "type": "actions",
              "items": [
                {
                  "id": "back",
                  "label": "Back",
                  "action": {
                    "kind": "navigate",
                    "destination": "home"
                  }
                },
                {
                  "id": "refresh-template",
                  "label": "Refresh schema",
                  "action": {
                    "kind": "command",
                    "operation": "refresh"
                  }
                }
              ]
            }
          ]
        }
    """

    private const val AUTH_TEMPLATE_JSON = """
        {
          "schemaVersion": 1,
          "id": "auth",
          "title": "Sync access",
          "subtitle": "Authenticate against Alfa backend",
          "components": [
            {
              "id": "hero-auth",
              "type": "hero",
              "eyebrow": "Account",
              "title": "Connect note sync",
              "body": "The backend defines the authentication screen schema. The client only binds entered credentials and executes typed actions."
            },
            {
              "id": "auth-main",
              "type": "auth_form",
              "usernameLabel": "Username",
              "usernameHint": "Enter username",
              "passwordLabel": "Password",
              "passwordHint": "Enter password",
              "primaryAction": {
                "id": "login",
                "label": "Login",
                "action": {
                  "kind": "command",
                  "operation": "login"
                }
              },
              "secondaryAction": {
                "id": "register",
                "label": "Register",
                "action": {
                  "kind": "command",
                  "operation": "register"
                }
              }
            }
          ]
        }
    """

    private const val MARK_TEMPLATE_JSON = """
        {
          "schemaVersion": 1,
          "id": "mark",
          "title": "Daily mark",
          "subtitle": "Backend-defined journal screen",
          "components": [
            {
              "id": "hero-mark",
              "type": "hero",
              "eyebrow": "Journal",
              "title": "{selectedDate}",
              "body": "The screen contract defines the calendar, editor and actions. The client only renders the schema and binds local mark data."
            },
            {
              "id": "calendar-mark",
              "type": "mark_calendar",
              "title": "Calendar",
              "subtitle": "Tap a day to load and edit its note",
              "selectAction": {
                "kind": "command",
                "operation": "select_date"
              }
            },
            {
              "id": "editor-mark",
              "type": "mark_editor",
              "bodyLabel": "Daily note",
              "bodyHint": "How was your day?",
              "primaryAction": {
                "id": "save",
                "label": "Save day",
                "action": {
                  "kind": "command",
                  "operation": "save_mark"
                }
              },
              "secondaryAction": {
                "id": "clear",
                "label": "Clear day",
                "action": {
                  "kind": "command",
                  "operation": "delete_mark"
                }
              }
            },
            {
              "id": "actions-mark",
              "type": "actions",
              "items": [
                {
                  "id": "today",
                  "label": "Today",
                  "action": {
                    "kind": "command",
                    "operation": "select_date"
                  }
                },
                {
                  "id": "workspace",
                  "label": "Workspace",
                  "action": {
                    "kind": "navigate",
                    "destination": "workspace"
                  }
                }
              ]
            }
          ]
        }
    """
}
