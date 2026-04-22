# What I Know

What I Know is an Android note-taking app built for the ITMO x Alfa-Bank Mobile Development course. It stores notes and marks locally, and can sync them with a backend API after the user signs in.

## Features

- Create and edit notes with local persistence.
- Track marks by date.
- Sign in or register from inside the app.
- Sync notes and marks with a remote API.
- Keep local changes if sync fails.
- Support phone and tablet-style workspace layouts.

## Tech Stack

- Kotlin
- Android Views + ViewBinding
- Room for local storage
- Retrofit + OkHttp + Gson for networking
- Hilt for dependency injection
- Coroutines + LiveData

## Requirements

- Android Studio with the Android SDK installed
- JDK 17 or newer (JDK 21 is also supported)
- Docker Desktop or Docker Engine with Docker Compose
- An Android emulator or a physical Android device
- The local backend stack in [`compose.yaml`](compose.yaml) if you want to test authentication and sync

## Getting Started

1. Clone the repository.
2. Open the project in Android Studio.
3. Let Gradle sync the project.
4. Start the local backend stack.
5. Run the `app` configuration on an emulator or device.

You can also build from the command line:

```bash
./gradlew assembleDebug
```

Start the backend stack from the repository root:

```bash
docker compose up -d
```

Stop it when you are done:

```bash
docker compose down
```

## Backend Configuration

The app uses different API base URLs per build type:

- `debug`: `http://10.0.2.2:8080`
- `release`: `https://api.what-i-know.mikkkkkkka.ru`

Cleartext traffic is allowed for `10.0.2.2` and `localhost` in debug-oriented local development scenarios.

### Local Backend With Docker Compose

The repository includes [`docker-compose.yaml`](compose.yaml) for local testing and contributor setup.

It starts:

- `api` on `http://localhost:8080`
- `db` on `localhost:5432`

The API container uses:

- image: `mikkkkkkka/what-i-know-api:dev`
- database: PostgreSQL 16
- database name: `what_i_know`
- database user: `postgres`
- database password: `postgres`

For Android emulator testing, the app reaches the local API at `http://10.0.2.2:8080`.

Typical local workflow:

1. Run `docker compose up -d`.
2. Wait for the `db` container health check to pass and the `api` container to start.
3. Launch the app in the `debug` build.
4. Register a user or sign in from the app.
5. Create notes, update marks, and use the sync action to test API integration.

### Expected API Areas

The Android app currently integrates with endpoints for:

- `POST /auth/login`
- `POST /auth/register`
- `GET /users/{userId}/notes`
- `POST /notes/`
- `PATCH /notes/{noteId}`
- `DELETE /notes/{noteId}`
- `GET /users/{userId}/marks`
- `POST /marks/`
- `PATCH /marks/{markId}`
- `DELETE /marks/{markId}`

## Usage

On first launch, the app opens the workspace and prompts for authentication if sync is not connected yet. After signing in or registering, you can:

- create notes
- open existing notes
- open the marks screen
- trigger sync manually

The app is designed to stay usable even when the API is unavailable. Local data remains in Room and pending deletions are tracked until a later sync succeeds.

If the local backend is running through Docker Compose, you can exercise the full authentication and sync flow in the debug build without changing the app configuration.

## Development Notes

- The main application entry point is [`WorkspaceActivity`](app/src/main/java/dev/mikkkkkkka/whatiknow/ui/workspace/WorkspaceActivity.kt).
- Networking is configured in [`DataModule.kt`](app/src/main/java/dev/mikkkkkkka/whatiknow/di/DataModule.kt).
- Sync behavior lives in [`SyncedNoteRepository.kt`](app/src/main/java/dev/mikkkkkkka/whatiknow/data/repository/SyncedNoteRepository.kt) and [`SyncedMarkRepository.kt`](app/src/main/java/dev/mikkkkkkka/whatiknow/data/repository/SyncedMarkRepository.kt).

## Contributing

Contributions are welcome. If you want to improve the app, a good contribution usually includes:

- a short description of the problem or change
- focused code changes
- verification steps for the behavior you changed
- README updates when setup or developer workflow changes

For backend-related local development, prefer repo-level documentation and shared tooling so contributors can reproduce the same environment easily.

## License

This project is licensed under the terms in [`LICENSE.md`](LICENSE.md).
