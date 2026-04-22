# What I Know

What I Know is an Android note-taking app built for the ITMO x Alfa-Bank Mobile Development course. In this branch the app is rewritten around a BDUI flow: screen templates are loaded from Alfa Echo API, and notes are stored in the same generic JSON backend.

## Features

- Compose-based single-activity app with a local design system.
- Backend-driven home and editor screens rendered from JSON templates.
- Cloud note storage through `PUT/GET /server/echo/{echoPath}`.
- Automatic template bootstrapping when Echo storage is empty.
- Cached note fallback when the remote backend is unavailable.

## Tech Stack

- Kotlin
- Jetpack Compose + Material 3
- Retrofit + OkHttp + Gson for networking
- Hilt for dependency injection
- Coroutines + StateFlow

## Requirements

- Android Studio with the Android SDK installed
- JDK 17 or newer (JDK 21 is also supported)
- An Android emulator or a physical Android device

## Getting Started

1. Clone the repository.
2. Open the project in Android Studio.
3. Let Gradle sync the project.
4. Run the `app` configuration on an emulator or device.

You can also build from the command line:

```bash
./gradlew assembleDebug
```

## Backend Configuration

The app now talks directly to the Echo service described in [`.tmp/openapi.yaml`](.tmp/openapi.yaml):

- base URL: `https://alfaitmo.ru/`
- read: `GET /server/echo/{echoPath}`
- write: `PUT /server/echo/{echoPath}`

The client uses these namespaces:

- `what-i-know-bdui/templates/home`
- `what-i-know-bdui/templates/editor`
- `what-i-know-bdui/notes/{deviceId}`

## Usage

On first launch, the app seeds default BDUI templates into Echo if they do not exist yet. After that you can:

- refresh remote templates and note data
- create notes
- open and edit notes
- delete notes

If Echo is unavailable, the last cached notes remain visible locally.

## Development Notes

- The main application entry point is [`WorkspaceActivity`](app/src/main/java/dev/mikkkkkkka/whatiknow/ui/workspace/WorkspaceActivity.kt).
- BDUI rendering lives in [`WorkspaceRoute.kt`](app/src/main/java/dev/mikkkkkkka/whatiknow/ui/workspace/WorkspaceRoute.kt).
- Echo integration lives in [`BduiRepository.kt`](app/src/main/java/dev/mikkkkkkka/whatiknow/data/remote/BduiRepository.kt).

## Contributing

Contributions are welcome. If you want to improve the app, a good contribution usually includes:

- a short description of the problem or change
- focused code changes
- verification steps for the behavior you changed
- README updates when setup or developer workflow changes

For backend-related local development, prefer repo-level documentation and shared tooling so contributors can reproduce the same environment easily.

## License

This project is licensed under the terms in [`LICENSE.md`](LICENSE.md).
