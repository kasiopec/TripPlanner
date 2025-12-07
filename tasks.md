TripPlanner MVP Tasks

## 1. Data layer foundation (done)

- Added domain models: `Trip`, `ItineraryItem`, `TripWithItinerary`, `ItineraryType` and input types.
- Created Room layer: entities under `data/local/entity/*`, `TripPlannerTypeConverters`, `TripDao`, `ItineraryDao` (with relation fetch and reorder support), and `TripPlannerDatabase`.
- Implemented mapping between entities and domain models.
- Introduced time helpers: `ClockProvider`, `CountdownFormatter`.
- Added Hilt modules: `DatabaseModule`, `TimeModule`, `RepositoryModule`.
- Implemented repositories: `TripRepositoryImpl`, `ItineraryRepositoryImpl` with transaction-safe add/reorder and timestamp updates.
- Added unit tests for converters, countdown logic, trip repo CRUD/join, itinerary repo sort/reorder/date filtering (tests currently require JDK locally).

## 2. Home screen

- ViewModel and MVI:
  - Create a feature package (for example, `features.home`) to co-locate Home screen logic.
  - Add `HomeUiState`, `HomeEvent`, and `HomeEffect` following the existing MVI style, representing loading, content, error, and refresh states while keeping `HomeUiState` as a pure data holder that uses `ErrorState` for full-screen errors.
  - Implement `HomeViewModel`, injecting `TripRepository`, `ClockProvider`, and related helpers (for example, countdown/date formatters, cover storage) via Hilt.
  - Map `TripRepository.observeTrips()` into a list of presentation items with:
    - Precomputed status (`None` = upcoming, `InProgress`, `Ended`).
    - Optional countdown data for the next upcoming trip.
    - A deterministic sort order (for example, in-progress -> upcoming (`None`) -> ended).
  - Handle loading, error, and empty states, plus a refresh event (pull-to-refresh or explicit retry).

- UI:
  - Add a `HomeScreen` composable that consumes `HomeUiState` and renders:
    - Loading state (progress indicator or skeletons).
    - Error state with a retry CTA when repository operations fail.
    - Empty state encouraging creation of the first trip.
    - Populated state using `LazyColumn` with trip cards and a bottom bar "+" action.
  - Trip cards:
    - Show destination and date range.
    - Show clear labels for in-progress and ended trips (no status label when status is `None` / upcoming).
    - Display cover images from `Trip.coverImageUri` when available, with a design-system placeholder when missing.
    - Make the whole card tappable to navigate to trip details.
  - Apply spacing, typography, and color tokens from `design-system.json` and the existing theme instead of ad-hoc values.

- Shared UI components:
  - Extract and reuse a `TripCard` composable in `ui/components` so Trip detail or future surfaces can reuse it.
  - Use a dedicated `CountdownCard` component that encapsulates countdown formatting and visibility rules as a standalone element at the top of the Home screen (not inside individual trip cards).
  - Home screen is display-only and should rely on stored cover URIs.

- Behavior and state:
  - Derive trip status from `Trip.startDate`/`Trip.endDate` vs `ClockProvider.now()` in the device timezone:
    - None: `now < startDate` (upcoming trip).
    - In progress: `startDate <= now <= endDate`.
    - Ended: `now > endDate`.
  - Implement countdown behavior inside `CountdownCard` so that:
    - When at least one trip is in progress, no countdown is shown.
    - When there are no in-progress trips, the countdown is shown only for the single next upcoming (`None`-status) trip.
  - Keep Home state as a single source of truth in `HomeViewModel`; `HomeScreen` remains stateless aside from the `UiState` it receives.

- Navigation and tests:
  - Wire bottom-bar "+" action to navigate to `Screen.TripForm` in create mode.
  - Make tapping a trip card navigate to `Screen.TripDetail` with the selected trip id.
  - Add unit tests for `HomeViewModel` covering mapping from `Trip` to status/countdown, and loading/empty/error states.
  - Add Compose UI tests for:
    - Empty vs populated states.
    - Countdown visibility vs status labels (no countdown while a trip is in progress, countdown only for the next upcoming trip otherwise).
    - Navigation intents for "+" and card tap.

- Potential issues:
  - Countdown ticking and UX:
    - Show a ticking countdown only for the next upcoming trip, as a standalone component at the top of the Home screen, rather than per-card timers.
    - Do not show a countdown while any trip is in progress; once there are no active trips and an upcoming trip exists, show the countdown for that upcoming trip.
    - Iterate on spacing, typography, and interaction (for example, tap targets or "view details" affordances) once the first version is implemented.
  - Timezone and date boundaries:
    - For MVP we strictly use the device timezone when classifying trips as upcoming, in-progress, or ended (matching `plan.md`).
    - This needs a follow-up note in post-MVP planning to revisit behavior for travelers crossing timezones and potentially move to per-trip `ZoneId` logic.
  - Large lists and image performance:
    - Use `LazyColumn` with stable keys plus Coil (already in the project) for loading cover images from local file URIs.
    - Downscale or generate lightweight thumbnails at save time so the Home screen always loads small images, minimizing I/O and decode cost even for large trip lists.
  - Missing or broken cover files:
    - When `TripCoverImageStorage.resolveForDisplay` returns null, always fall back to a deterministic placeholder (for example, initials-based avatar or neutral gradient) so cards never show a broken image.
  - Error handling UX:
    - The original full-screen error design was a first draft; we should prefer non-blocking error banners or snackbars on top of the last known-good list where possible, reserving full-screen error for first-load failures or hard faults.

## 3. Trip form

- Data and validation:
  - Use the existing `Trip` domain model as the backing entity.
  - Keep trip forms local-only for MVP (no remote sync), but design the API so a remote data source can be swapped in later.
  - Required fields:
    - Destination/title.
    - Start date and end date (end date mandatory).
  - Optional fields:
    - Notes.
    - Cover image (user-selected via system image picker).
  - Single-day behavior:
    - When the user enables a "single-day trip" toggle, set `endDate = startDate` and keep it in sync while the toggle is on.
    - When the toggle is turned off, allow the user to pick a different end date; do not silently overwrite user input.
  - Validation rules:
    - Destination must be non-blank.
    - Start date must be <= end date.
    - End date must be set; no open-ended trips for MVP.

- UI:
  - Build `TripFormScreen` as a Compose screen using the existing theme and design-system tokens.
  - Fields:
    - Destination text field.
    - Start date and end date pickers (date pickers or bottom sheets aligned with Material 3).
    - Single-day trip toggle.
    - Optional notes field.
    - Cover image picker (showing a preview or placeholder).
  - Actions:
    - Save button (primary).
    - Cancel/back navigation.
  - Surface validation errors inline (e.g., below fields) and via error messages sourced from `strings.xml`.

- Behavior and state:
  - Implement `TripFormViewModel` with MVI-style `UiState`, `Event`, and `Effect`.
  - Keep form fields and validation errors in `TripFormUiState` as pure data.
  - Use a validator or use-case class for form validation rules; do not embed validation logic directly in `UiState`.
  - Save flows:
    - On Save:
      - Set a saving flag in state to show a loading indicator on the Save button.
      - Validate inputs.
      - On success, map the form to a `Trip` model and call the repository to insert or update.
    - Use a `try/finally` pattern so the saving flag is reset on both success and failure.
    - On success:
      - Emit a navigation effect with the saved trip id so the navigation layer can decide whether to go back or forward to detail.
    - On failure:
      - Emit a snackbar effect with a friendly error message.
      - Keep form data intact so the user can correct and retry.
  - Image handling:
    - Resolve the picker `Uri` into an app-private copy only when the user taps Save, not when the picker closes.
    - Store a stable, app-private reference (`coverImageUri`) in the `Trip` model so images remain valid even if the original source is removed.

- Navigation and tests:
  - Add a new `Screen.TripForm` entry in `navigation/Screen.kt` with its own route and title.
  - Wire a `composable` for `Screen.TripForm.route` in `NavGraph.kt` that:
    - Resolves `TripFormViewModel` via `hiltViewModel()`.
    - Accepts an optional `tripId` argument for edit mode.
    - Uses `ObserveNavigationEffect` to react to `TripFormEffect` (close, navigate back, or navigate to `TripDetail` with the newly created/updated id).
  - Integrate Trips list "+" so it navigates to `Screen.TripForm` in create mode, and any edit entry point navigates with a `tripId` path/argument.
  - Add unit tests for `TripFormViewModel`:
    - Validation rules.
    - Single-day behavior (syncing dates, toggling on/off in create/edit).
    - Save success and failure paths.
    - (These tests can be part of a follow-up pass once flows stabilize.)

## 4. Trip detail + timeline shell

- Screen structure:
  - Full-screen itinerary screen (`TripDetail`) with:
    - Header showing destination, date range, and status.
    - Countdown banner only for upcoming trips.
    - Status only (no countdown) for in-progress and ended trips.
  - Date strip from start to end dates with stateful selection.
  - Timeline scaffold for the selected day.

- Timeline:
  - Group items by day.
  - Show vertical connectors and placeholders where appropriate.
  - Prepare structure for drag-and-drop and icons (detailed in later tasks).

- State and error handling:
  - Support loading and error states when fetching trip + itinerary.
  - Ensure state restoration across configuration changes.

- Testing:
  - Integration test for navigation from Trips list -> TripDetail.
  - Test interactions with the date strip and day selection.

## 5. Activity form

- Presentation:
  - Activity form as a modal/sheet on top of the itinerary screen.

- UI:
  - Fields: title, date (bounded to trip range), optional time, type selector with icons, location, and notes.
  - Type selector supports at least: Flight, Hotel, Activity, Food, Shopping.

- Behavior and validation:
  - Require title and date.
  - Constrain date picker to the trip's start-end range.
  - Provide sensible defaults for time and type when not specified.
  - When submitting:
    - For create, insert a new itinerary item via the repository.
    - For edit, update the existing item by id.
  - On success, close the modal and refresh timeline data.
  - On failure, show a snackbar and keep data for retry.

- Testing:
  - Unit tests for validators (title, date bounds).
  - Unit tests for Activity form ViewModel flows (create/edit success and failure).
  - These tests can be done as a later step after flows are solid.

## 6. Drag & drop + sorting

- Reordering:
  - Enable drag-and-drop reorder in the per-day timeline using standard Compose drag-and-drop APIs (no third-party libraries).
  - Update and persist `sortOrder` when items are moved.
  - Ensure default `sortOrder` for new items is time-based (fallback when necessary).

- State and persistence:
  - Keep UI state consistent while dragging.
  - Reloading the screen should reflect the persisted order.

- Testing:
  - UI test to verify reorder persists after reload.

## 7. Polish + navigation

- Visual polish:
  - Cover image handling for user-selected photos:
    - Placeholders when no image is selected.
    - Basic loading state.
  - Activity type icons aligned with the design system.
  - Apply visual tweaks in line with `design-system.json`.

- Navigation:
  - Ensure route arguments and types match the nav graph definition.
  - Confirm back stack behavior for:
    - Trips list -> TripForm (create/edit).
    - Trips list -> TripDetail -> Activity form.

- Quality checks:
  - Run `./gradlew assembleDebug` for a smoke build.
  - Run key feature UI tests for Trips list, TripForm, TripDetail, and Activity form.

## 8. Post-MVP test hardening

- Expand ViewModel unit tests:
  - `TripsViewModel`.
  - TripDetail/itinerary ViewModel(s).
  - Activity form ViewModel (full coverage).

- Broaden Compose UI and navigation tests:
  - Multi-screen flows and edge cases (empty data, error states).
  - Countdown transitions (upcoming -> in-progress -> ended).

- Tighten regression coverage before extending features (e.g., remote sync, notifications).

