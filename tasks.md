TripPlanner MVP Tasks

## 1. Data layer foundation (done)

- Added domain models: `Trip`, `ItineraryItem`, `TripWithItinerary`, `ItineraryType` and input types.
- Created Room layer: entities under `data/local/entity/*`, `TripPlannerTypeConverters`, `TripDao`, `ItineraryDao` (with relation fetch and reorder support), and `TripPlannerDatabase`.
- Implemented mapping between entities and domain models.
- Introduced time helpers: `ClockProvider`, `CountdownFormatter`.
- Added Hilt modules: `DatabaseModule`, `TimeModule`, `RepositoryModule`.
- Implemented repositories: `TripRepositoryImpl`, `ItineraryRepositoryImpl` with transaction-safe add/reorder and timestamp updates.
- Added unit tests for converters, countdown logic, trip repo CRUD/join, itinerary repo sort/reorder/date filtering (tests currently require JDK locally).

## 2. Trips list

- Implement `TripsViewModel` with:
  - Countdown and status handling for upcoming, in-progress, and ended trips.
  - State for loading, error, and empty list.
- Build Compose UI:
  - Trip cards with cover placeholders (and user-selected cover images when available).
  - Countdown chip shown only for upcoming trips.
  - Clear labels for in-progress and ended trips (no countdown).
  - Empty state CTA encouraging creation of the first trip.
- Navigation:
  - Bottom-bar "+" action navigates to `TripForm` in create mode.
  - Tapping a trip card navigates to `TripDetail` with the trip id.
- Testing:
  - UI tests for empty vs populated states.
  - UI tests for countdown modes and status labels.

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
    - Cover image picker stub that integrates with the system image picker (for example, `ActivityResultContracts.GetContent`) via `TripCoverPicker` and exposes the selected `coverImageUri` back to the ViewModel.
- Shared UI components and resources:
  - Add a small reusable `TripDateField` component in `ui/components` that renders a read-only `PlannerOutlinedTextField` with a date label, formatted value, and calendar icon, and invokes a callback when clicked (Trip form wires this to a date picker dialog).
  - Add a `TripCoverPicker` component in `ui/components` that renders the current cover image and wires an `onClick` callback to the system image picker, emitting the chosen `coverImageUri` for Trip form and Trips list reuse.
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
  - Map UI state into a domain model (`TripInput` for create, `Trip` copy for update) before calling `TripRepository` so domain models stay free of UI-only concerns.
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
