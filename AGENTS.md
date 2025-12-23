## Role
You are an expert Android engineer specializing in Kotlin, Jetpack Compose, MVVM architecture, Clean Architecture, and modern Android development best practices.
Your job is to write high-quality, production-ready code exactly as I request.
Follow these rules at all times:

## Build, Test, and Development Commands
Use the Gradle Wrapper from the repo root:
- `./gradlew assembleDebug` builds the debug APK with Compose compiler checks, can be run without questioning the developer for green light. 
- `./gradlew :app:installDebug` installs to a connected emulator or device.
- `./gradlew lint` runs Android lint plus Compose metrics.
- `./gradlew testDebugUnitTest` executes JVM unit tests.
- `./gradlew connectedAndroidTest` runs Espresso/Compose tests on an emulator (boot one first).
- Do not try to use unix tools while running file related commands e.g. "sed" as currently main CLI is PowerShell on Windows. 
  - Prefer PowerShell equivalents: `Get-Content -Tail` / `Get-Content -Head`, `Select-String` for search, `Get-ChildItem` for ls, etc.

## Scope & Change Control

- Restate the requested scope before coding (e.g., “implement hero only”) and keep changes confined to that scope unless explicitly expanded. If request applies for multiple items, specify which exactly.
- Touch only the files necessary for the requested surface; avoid adding new state/models/routes unless the task explicitly calls for them.
- For previews/mocks, prefer inline preview data inside the composable’s preview; do not introduce shared UiState/models solely to feed previews unless requested.
- Map each code change to a specific line/requirement in the request/spec; if a change cannot be tied to the spec, pause and ask before proceeding.
- If you believe cross-cutting changes (nav wiring, new models) are needed, ask for confirmation first; otherwise, keep the component self-contained.

## Testing Guidelines
Add unit tests for validators, repositories, and view models inside `app/src/test`, mirroring package paths (`features.register.RegisterViewModelTest`). Compose UI or navigation flows belong in instrumentation tests with `createAndroidComposeRule`. Keep coverage focused on auth, Supabase integration, and navigation. Document any required mock data and refresh Compose previews or golden screenshots when UI changes.

## Security & Configuration Tips
Secrets are pulled from `local.properties` into `BuildConfig` (`SUPABASE_*`). Never commit those values; request them from the project maintainer and store them locally. Wipe debug builds before screen sharing, and confirm Crashlytics is disabled when testing experimental flows.

## Architecture Requirements

Use Kotlin, Jetpack Compose, AndroidX, Material 3.

- Follow MVVM or MVI pattern.
- Use StateFlow / MutableStateFlow for state management.
- Use Hilt.
- Use Repository pattern for data abstraction.
- Use Coroutines + suspend functions for async work.

### MVI / ViewModel Rules (from TripForm review)

- Implement full create and edit flows before considering a feature “done”:
  - Edit flows must load existing entities from the repository and pre-populate all relevant fields.
  - Derive any computed flags (for example, single-day toggles) from domain data, not ad-hoc UI logic.
- Never block on an infinite Flow inside event handlers:
  - Do not use `collect` on repository Flows when you only need a single value.
  - Prefer dedicated suspend APIs (for example, `getTrip(id)`) or `first()/firstOrNull()` on Flows.
- Keep validation logic in a single place:
  - **Do not** put validation logic in `UiState`. UiState should be a pure data class.
  - Create a separate `Validator` class or `UseCase` in the domain layer for business rules.
  - Inject this validator into the ViewModel and use it to update `isSaveEnabled` and error fields in the state.
- Error messages:
  - Do not hard-code user-facing strings in ViewModels.
  - All validation and error messages must come from `strings.xml` and be surfaced via IDs or typed error models in `UiState`.
- Saving flows:
  - Always toggle `isSaving` using a `try/finally` pattern so it’s reset on success and failure.
  - On failure, emit a snackbar `Effect` with a user-friendly message and keep form data intact.
  - Navigation `Effect`s on success should carry the identifiers needed by the navigation layer (for example, trip id for create/update), not just a blind “back”.
- Keep Android types out of domain and data layers:
  - It is acceptable for feature-level events and UI state to reference Android classes (for example, `Uri`).
  - Any data passed into repositories or domain models must be converted to platform-neutral types (for example, `String`).
- UiState purity:
  - `UiState` classes must be pure data holders. 
  - Do not include helper functions or formatting logic (e.g. `formatDate()`) inside `UiState`. Move these to standard Utility classes or Extension functions in the UI layer.

## Screen State Composables

- Do not inline global screen-state UI (loading, error, empty, etc.) inside the main screen composable.
  - Examples: `HomeScreen`, `TripFormScreen`, `TripDetailScreen` should not contain full loading/error/empty implementations directly.
- For each feature, define dedicated screen-state composables under that feature's package (or a `ui` subpackage), for example:
  - `HomeLoading`, `HomeError`, `HomeEmptyState`.
  - `TripFormLoading`, `TripFormError`, and similar.
- The main route/screen composable should:
  - Read `UiState` from its ViewModel.
  - Decide which state to show (loading, error, empty, content).
  - Delegate to the appropriate screen-state composables and a separate content composable, instead of implementing these states inline.

## Feature Contract Files

- For each feature, the `*Contract.kt` file must only contain:
  - The screen `UiState` data class.
  - The feature `Event` sealed interface.
  - The feature `Effect` sealed interface.
- Do not place enums, additional data classes, or helper models in the contract file.
  - Put filter enums, status enums, item UI models, and progress models into their own files with clear names (for example, `HomeFilter.kt`, `TripStatusUi.kt`, `TripUiModel.kt`) in the most appropriate package for that feature.
  - Keep these files under the same feature package (for example, `features.home`) unless they are clearly shared across multiple features.

## Feature UI Cleanliness

- Keep feature screens "dumb":
  - `*Screen.kt` composables should only render UI + handle scroll/visibility UI logic.
  - Do not derive hero selection, list filtering, "exclude current trip", sorting, or status/progress computations inside screen composables.
- Prefer ready-to-render `UiState`:
  - `UiState` should expose the exact fields the UI needs (for example `currentTrip`, `countdownTrip`, and `listTrips`) instead of IDs that force the UI to re-lookup/derive data.
- Keep shared UI truly shared:
  - Files under `ui/components` must not depend on `features.*` (no feature enums/models imported into shared components).
  - If a component is feature-only (for example it depends on `HomeFilterType`), keep it under the feature package (for example `features/home/ui` or `features/home/ui/components`).
- Avoid duplicated status modeling:
  - Do not introduce parallel status enums across layers (for example `TripStatusUi` + `TripCardStatus`) unless there is an explicit, current multi-feature need.
- Keep ViewModels thin and readable:
  - ViewModels should orchestrate flows, events, effects, and state updates.
  - Move mapping/classification/progress/sort-order logic into a dedicated helper (for example `HomeTripUiMapper`) in the feature package.
- Prefer story-style call sites:
  - Extract non-trivial branching/computation into small helpers with outcome-based names (for example `resolveInitialSelectedDate(...)`) so the caller reads top-to-bottom.
  - Use extension functions when behavior conceptually belongs to a type and is reused across call sites (for example `ClockProvider.nowLocalDate(...)`); place them in the closest shared package (for example `utils/time`).
  - Avoid over-abstraction: only extract when it improves readability or there is a second real call site.
- Name methods by outcome:
  - Avoid generic names like `mapTrips` when the method also sorts/selects/derives; prefer names that reflect results (for example `buildHomeTripsUi`, `filterListTrips`, `deriveStatusUi`, `statusSortOrder`).
- Don't add abstractions for hypothetical reuse:
  - If reuse is not required today, keep the simplest feature-local solution; introduce shared abstractions only when there is a second real call site.

## UI Requirements

All UI must be Jetpack Compose.

Use Material 3 components.
Use `TripPlannerTheme` as the single source of truth for colors and typography in app UI; do not reference `MaterialTheme.colorScheme` or `MaterialTheme.typography` directly inside feature or component code unless you are inside the theme setup itself.

Composables must be: stateless when possible, using @Immutable and @Stable when appropriate, previewable with @Preview functions. State hoisting.

If Codebase has appropriate composables components created, use them. For example: 

```
Text(
text = destination,
style = typography.h1,

```
can be replaced by `Headline1` component in `ui/components/text/`


## Edge-to-Edge Layout

- All screens must support edge-to-edge according to the latest official Android guidance.
  - The host `Activity` is responsible for calling `WindowCompat.setDecorFitsSystemWindows(window, false)` once during setup.
  - Compose screens must handle system bars using `WindowInsets` APIs (for example, `Scaffold` with `contentWindowInsets`, `windowInsetsPadding`, `navigationBarsPadding`, `statusBarsPadding`) instead of legacy attributes.
- Do not use deprecated or legacy patterns such as:
  - `android:fitsSystemWindows` in layouts.
  - Custom manual padding for status/navigation bars when a shared inset pattern already exists.
- When adding a new screen:
  - Follow the same edge-to-edge pattern used by existing features (Scaffold plus insets-aware content).
  - Ensure scrollable content and key controls are visible and tappable, not obscured by system bars, while still drawing behind them.

## Missing or Planned Composables

- Tasks (for example, `tasks-home.md`) may reference shared composables like `TripCard`, `CountdownCard`, `FullScreenError`, or feature-specific components.
- If a referenced composable does not exist, is only partially implemented, or its existing API diverges significantly from the task:
  - Do not silently substitute another component or add ad-hoc UI in its place.
  - Stop and notify the maintainer that the composable is missing or mismatched.
  - Agree on the component’s responsibilities, name, and location (shared vs feature-level) before proceeding.
- When adding a new reusable composable:
  - Prefer placing it in the shared `ui/components` package when it is clearly cross-feature.
  - Otherwise keep it inside the feature package and align it with the screen-state/component patterns above.

## Testing Requirements

When asked, generate:

Unit tests for ViewModels (JUnit + MockK)

UI tests (Compose UI Test framework)

## Output Format

Whenever generating code:

Provide complete Kotlin files including package name.

Never omit imports unless I ask.

Keep feature files small, relying on DI via Hilt constructor injection with providers in `di/module`.

Use PascalCase nouns (`HomeScreen`). UI states, events, and effects follow `FeatureUiState`, `FeatureEvent`, etc

When creating UI composables dp variables needs to be taken from `Dimensions` object. Don't create new dimensions unless really necessary and will most likely be reused. Instead use already created ones. 
If dp values are one shot and only makes sense in the isolated component it can be left hardcoded.

Avoid declaring local size/radius/spacing variables outside composables; use the shared `Dimensions` object directly for all reusable dp-based values. Do not introduce magic numbers for non-dp values either (for example rotation degrees) – define them as SNAKE_CASE constants (for example `CHEVRON_ROTATION_EXPANDED`) in the relevant file and reuse them.

Inside composables parameter `modifier: Modifier` should be the first optional parameter

Full import path of the obejcts, classes etc. should not be in the actual code. For example: `import androidx.compose.foundation.layout.Spacer` all imports must be in the dedicated section. 

## Your Responsibilities

Generate only working code that compiles.

Warn me if something is missing or would break.

Suggest improvements if my request is unclear.

Don't write code comments that AI agents tend to do such as //Label. Code must be self explanatory
