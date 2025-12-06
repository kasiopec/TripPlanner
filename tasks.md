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
  - Add `HomeUiState`, `HomeEvent`, and `HomeEffect` following the existing MVI style (Loading / Content / GlobalError using `ErrorState`).
  - Implement `HomeViewModel`, injecting `TripRepository` and `ClockProvider` via Hilt.
  - Map `TripRepository.observeTrips()` into a list of presentation items with precomputed status (Upcoming, InProgress, Ended), countdown text (or null), and a deterministic sort order (for example, upcoming → in-progress → ended).
  - Handle loading, error, and empty states, plus a refresh event (pull-to-refresh or explicit retry).
- UI:
  - Add `HomeScreen` composable that consumes `HomeUiState` and renders:
    - Loading state (progress indicator or skeletons).
    - Error state with a retry CTA when repository operations fail.
    - Empty state encouraging creation of the first trip.
    - Populated state using `LazyColumn` with trip cards and a bottom bar "+" action.
  - Trip cards:
    - Show destination, date range, and a countdown chip only for upcoming trips.
    - Show clear labels for in-progress and ended trips instead of a countdown chip.
    - Display cover images from `Trip.coverImageUri` when available, with a design-system placeholder when missing.
    - Make the whole card tappable to navigate to trip details.
  - Apply spacing, typography, and color tokens from `design-system.json` and the existing theme instead of ad-hoc values.
- Shared UI components:
  - Extract a reusable `TripCard` composable in `ui/components` so Trip detail or future surfaces can reuse it.
  - Consider a dedicated `CountdownChip` component that encapsulates countdown formatting and visibility rules.
  - Reuse `TripCoverPicker` only where image picking is needed; the Home screen is display-only and should rely on stored cover URIs.
- Behavior and state:
  - Derive trip status from `Trip.startDate`/`Trip.endDate` vs `ClockProvider.now()` in the device timezone:
    - Upcoming: `now < startDate`.
    - In progress: `startDate <= now <= endDate`.
    - Ended: `now > endDate`.
  - Implement countdown behavior:
    - Days-only mode when `startDate - now >= 24h`.
    - Ticking mode (for example, minute-level updates) when the next upcoming trip is within 24 hours.
  - Keep Home state as a single source of truth in `HomeViewModel`; `HomeScreen` remains stateless aside from the `UiState` it receives.
- Navigation and tests:
  - Wire bottom-bar "+" action to navigate to `Screen.TripForm` in create mode.
  - Make tapping a trip card navigate to `Screen.TripDetail` with the selected trip id.
  - Add unit tests for `HomeViewModel` covering mapping from `Trip` to status/countdown, and loading/empty/error states.
  - Add Compose UI tests for empty vs populated states, countdown vs status labels, and navigation intents for "+" and card tap.
- Potential issues:
  - Countdown ticking and UX:
    - We only show a ticking countdown for the next upcoming trip, as a standalone component at the top of the Home screen, rather than per-card timers.
    - We should iterate on spacing, typography, and interaction (for example, tap targets or “view details” affordances) once the first version is implemented.
  - Timezone and date boundaries:
    - For MVP we strictly use the device timezone when classifying trips as upcoming, in-progress, or ended (matching `plan.md`).
    - This needs a follow-up note in post-MVP planning to revisit behavior for travelers crossing timezones and potentially move to per-trip `ZoneId` logic.
  - Large lists and image performance:
    - Use `LazyColumn` with stable keys plus Coil (already in the project) for loading cover images from local file URIs.
    - Downscale or generate lightweight thumbnails at save time so the Home screen always loads small images, minimizing I/O and decode cost even for large trip lists.
  - Missing or broken cover files:
    - When `TripCoverImageStorage.resolveForDisplay` returns null, always fall back to a deterministic placeholder (for example, initials-based avatar or neutral gradient) so cards never show a broken image.
  - Error handling UX:
    - The original full-screen error design was a first draft; we should prefer non-blocking error banners or snackbars while keeping the last known-good list visible and offering a clear Retry action.

## 3. Trip form

- ViewModel and MVI:
  - Create a new feature package `features.tripform` to co-locate all Trip form logic.
  - Add `TripFormUiState`, `TripFormEvent`, and `TripFormEffect` classes in this package, following the existing MVI style from `features.register` and `features.login` (Loading / Form / GlobalError using `ErrorState`).
  - Implement `TripFormViewModel` in `features.tripform`, injecting `TripRepository` and `ClockProvider` via Hilt.
  - Support both create (no id) and edit (existing id) flows.
  - For edit, load the existing trip by id and pre-populate fields.
  - Derive initial single-day toggle from `startDate == endDate`.
- UI:
  - Add `TripFormScreen` in `features.tripform`, using the existing design system components (`PlannerOutlinedTextField`, `LargeRoundedButton`, `TripPlannerTheme` text styles).
  - The screen should include:
    - Destination text field (required) using `PlannerOutlinedTextField` and showing validation error text when invalid.
    - Start date field connected to a Material 3 date picker/dialog.
    - End date field connected to the same date picker logic, disabled when single-day is ON.
    - Single-day toggle (default OFF for new trips) displayed as a labeled row using Material 3 `Switch` or a small reusable row component.
    - Optional notes multi-line input using `PlannerOutlinedTextField` with `singleLine = false`.
    - Cover image picker that integrates with the system image picker (for example, `ActivityResultContracts.GetContent`) via `TripCoverPicker`, keeps the picker Uri for display, and only imports the selected image into app-private storage when the user taps Save.
- Shared UI components and resources:
  - Add a small reusable `TripDateField` component in `ui/components` that renders a read-only `PlannerOutlinedTextField` with a date label, formatted value, and calendar icon, and invokes a callback when clicked (Trip form wires this to a date picker dialog).
  - Add a `TripCoverPicker` component in `ui/components` that renders the current cover image and wires an `onClick` callback to the system image picker, emitting a displayable Uri while the ViewModel persists a neutral app-private reference suitable for `Trip.coverImageUri`.
  - Introduce string resources in `res/values/strings.xml` for all Trip form labels, placeholders, and error messages (destination, start date, end date, single-day label, notes, cover image, validation messages).
- Behavior and validation:
  - Enforce required destination, start date, and end date.
  - When single-day is enabled:
    - Auto-fill end = start and keep it in sync when start changes.
    - Disable manual changes to end date.
  - When single-day is off:
    - Require explicit end date selection.
    - Prevent saving when `startDate > endDate`.
  - Show inline validation errors per field.
  - Keep primary action disabled when the form is invalid or a save is in progress.
- Saving and effects:
  - On save click, send an event to `TripFormViewModel` to create or update the trip via the repository.
  - Map UI state into domain inputs (`TripInput` for create, `Trip` copy for update), including the stable cover image reference, before calling `TripRepository` so domain models stay free of UI-only concerns and Android types.
  - Only copy the cover image into app-private storage during save; do not persist files when the picker dialog closes to avoid orphaned images from cancelled selections.
  - Set a loading state while saving.
  - On success:
    - Emit a navigation effect to close the form and return the trip id.
    - Preserve state across configuration changes.
  - On failure:
    - Emit a snackbar effect with a user-friendly error.
    - Keep form data so the user can correct or retry.
- Navigation and tests:
  - Add a new `Screen.TripForm` entry in `navigation/Screen.kt` with its own route and title.
  - Wire a `composable` for `Screen.TripForm.route` in `NavGraph.kt` that:
    - Resolves `TripFormViewModel` via `hiltViewModel()`.
    - Passes any optional `tripId` argument for edit mode.
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
  - Integration test for navigation from Trips list → TripDetail.
  - Test interactions with the date strip and day selection.

## 5. Activity form

- Presentation:
  - Activity form as a modal/sheet on top of the itinerary screen.
- UI:
  - Fields: title, date (bounded to trip range), optional time, type selector with icons, location, and notes.
  - Type selector supports at least: Flight, Hotel, Activity, Food, Shopping.
- Behavior and validation:
  - Require title and date.
  - Constrain date picker to the trip’s start–end range.
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
    - Trips list → TripForm (create/edit).
    - Trips list → TripDetail → Activity form.
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
  - Countdown transitions (upcoming → in-progress → ended).
- Tighten regression coverage before extending features (e.g., remote sync, notifications).
