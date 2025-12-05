# Repository Guidelines

## Project Structure & Module Organization
TripPlanner is a single Android module under `app/`. Core Compose UI, view models, and DI modules live in `app/src/main/java/com/project/tripplanner`. Feature folders (for example `features/register`, `features/home`) keep UI, events, state, and view models co-located. Shared UI components stay in `ui/components`, theming under `ui/theme`, navigation flows in `navigation`, and dependency bindings in `di/module`. Resources (layouts, drawables, and strings) live in `app/src/main/res`. JVM unit tests sit in `app/src/test`, and Espresso/Compose instrumentation tests live in `app/src/androidTest`.

## Build, Test, and Development Commands
Use the Gradle Wrapper from the repo root:
- `./gradlew assembleDebug` builds the debug APK with Compose compiler checks.
- `./gradlew :app:installDebug` installs to a connected emulator or device.
- `./gradlew lint` runs Android lint plus Compose metrics.
- `./gradlew testDebugUnitTest` executes JVM unit tests.
- `./gradlew connectedAndroidTest` runs Espresso/Compose tests on an emulator (boot one first).

## Coding Style & Naming Conventions
Kotlin sources follow 4-space indentation and Compose best practices: `@Composable` functions use PascalCase nouns (`HomeScreen`). UI states, events, and effects follow `FeatureUiState`, `FeatureEvent`, etc. Prefer immutable data classes and sealed interfaces for state machines. Keep feature files small, relying on DI via Hilt constructor injection with providers in `di/module`. Run `./gradlew lintKotlin` (if enabled) or apply Ktlint rules before opening a PR.

## Testing Guidelines
Add unit tests for validators, repositories, and view models inside `app/src/test`, mirroring package paths (`features.register.RegisterViewModelTest`). Compose UI or navigation flows belong in instrumentation tests with `createAndroidComposeRule`. Keep coverage focused on auth, Supabase integration, and navigation. Document any required mock data and refresh Compose previews or golden screenshots when UI changes.

## Commit & Pull Request Guidelines
Recent history favors concise, imperative messages (`Added BottomBar`, `Added some basic navigation`). Follow that voice: lead with a verb under ~60 chars, and add a short body when behavior changes. Pull requests should describe user-facing impact, list the Gradle commands executed, link issues, and attach screenshots/GIFs for Compose UI updates or nav tweaks. Flag reviewers when Gradle/plugins/local.properties inputs change.

## Security & Configuration Tips
Secrets are pulled from `local.properties` into `BuildConfig` (`SUPABASE_*`). Never commit those values; request them from the project maintainer and store them locally. Wipe debug builds before screen sharing, and confirm Crashlytics is disabled when testing experimental flows.

## Role
You are Codex Cli, a highly skilled software engineer with extensive knowledge in many programming languages, frameworks, design patterns, and best practices.