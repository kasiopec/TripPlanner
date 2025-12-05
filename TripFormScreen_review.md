# TripFormScreen Code Review

File: `app/src/main/java/com/project/tripplanner/features/tripform/TripFormScreen.kt`

## High-level overview

- The screen provides a Trip creation/editing form with destination, dates, single-day toggle, notes, and a cover image picker stub.
- It uses Compose Material 3, the existing design system components (`PlannerOutlinedTextField`, `TripDateField`, `LargeRoundedButton`, `TripCoverPicker`), and `TripPlannerTheme` colors and typography.
- The implementation is self-contained and previewable, with clear structure and straightforward state updates inside the composable.

## Architecture & state management

- **Stateful screen vs. MVI/MVVM requirements**
  - The form fields (`destination`, `startDate`, `endDate`, `isSingleDay`, `notes`, `coverImageUri`) are all held as local `remember { mutableStateOf(...) }` inside `TripFormScreen`.
  - The project and `tasks.md` explicitly call for an MVI/MVVM setup with `TripFormUiState`, `TripFormEvent`, `TripFormEffect`, and `TripFormViewModel` using `StateFlow`.
  - Recommendation:
    - Refactor `TripFormScreen` into a **stateless** UI function that takes a `TripFormUiState` (or similar) plus callbacks / events:
      - Example: `fun TripFormScreen(state: TripFormUiState, onEvent: (TripFormEvent) -> Unit, onBackClick: () -> Unit)`
    - Introduce a `TripFormRoute` (or similar) composable that obtains `TripFormViewModel` via `hiltViewModel()`, collects `uiState`, and wires `onEvent`.
    - Keep only ephemeral UI state (e.g., `showStartDatePicker`, `showEndDatePicker`) inside the UI layer; all business-relevant state should live in the ViewModel.

- **Single source of truth**
  - Currently, the form fields serve as the only source of truth, but they are not persisted across configuration changes beyond what Compose handles in-memory.
  - With the planned ViewModel, this should move into `StateFlow` to:
    - Support configuration changes.
    - Align with the rest of the app (e.g., register/login features).

- **Edit mode**
  - `isEditMode` currently only controls the screen title and does not pre-populate the form or derive `isSingleDay` from `startDate == endDate` as described in `tasks.md`.
  - Recommendation:
    - Have the ViewModel load an existing `Trip` by id in edit mode and expose populated fields through `TripFormUiState`.
    - Derive the initial `isSingleDay` flag in the ViewModel and expose it to the UI.

## Validation & UX behavior

- **Missing field validation**
  - The spec requires:
    - Required `destination`, `startDate`, and `endDate`.
    - Prevent saving when `startDate > endDate`.
    - Show inline validation errors per field.
    - Disable primary action when the form is invalid or a save is in progress.
  - Current implementation:
    - `LargeRoundedButton` is always enabled.
    - No inline error messages or visual indications of invalid fields.
    - `onSaveClick` is invoked even if dates are null or out-of-order.
  - Recommendation:
    - Add derived validation state in the ViewModel (e.g., `isSaveEnabled`, `destinationError`, `startDateError`, `endDateError`) and surface it in `TripFormUiState`.
    - Use `PlannerOutlinedTextField`’s error support (if available) or a small error text below each field.
    - Disable the save button while the form is invalid or a save is in progress.

- **Single-day behavior**
  - Positives:
    - When `isSingleDay` is toggled on and `startDate` is non-null, `endDate` is set to `startDate`.
    - When a date is selected in the start date picker while in single-day mode, `endDate` is kept in sync.
  - Gaps:
    - When `isSingleDay` is turned off, the user is allowed to pick any end date, including earlier than the start date; there is no validation or guidance.
  - Recommendation:
    - Enforce business rules from the ViewModel (disallow or visibly flag `startDate > endDate`).
    - Optionally, constrain the end date picker to dates ≥ `startDate` if that aligns with product requirements.

- **Cover image picker**
  - The current wiring:
    - `TripCoverPicker` receives `selectedImageUri` and a no-arg `onClick` that is currently a TODO for launching an image picker.
    - The component itself is designed more as a "tap to open picker" than as the local cover image grid described in `tasks.md`.
  - Gaps:
    - No way to update `coverImageUri` in `TripFormScreen`.
    - No integration with a local cover image set or ViewModel-level persistence.
  - Recommendation:
    - Decide whether `TripCoverPicker` is:
      - A local gallery chooser (taking a list of local covers and emitting a `Uri`), or
      - A launcher surface for system gallery/camera that reports a `Uri` back.
    - In either case, the callback signature should surface the new `Uri` (`onCoverSelected(Uri?)`) so that the ViewModel can store it and the UI can re-render.

## Theming, dimensions, and design system

- **Use of `Dimensions`**
  - The main form uses `Dimensions` for spacing and sizes, which is consistent with the design system.
  - There are a few hardcoded `dp` usages that diverge from the AGENTS.md guidance:
    - `height(120.dp)` for the notes field.
    - In `TripCoverPicker` preview: `padding(16.dp)`.
  - Recommendation:
    - Replace hard-coded `dp` values with `Dimensions` (or introduce new constants in `Dimensions` if needed), to keep styling consistent and centrally tunable.

- **Colors & typography**
  - `TripPlannerTheme.typography.h2` and `TripPlannerTheme.colors` are used consistently across the top app bar and body content.
  - The single-day toggle row uses a custom `RoundedCornerShape(Dimensions.radiusM)` and a `colors.surface` background, which fits the design system.
  - For `DatePickerDialog` buttons:
    - Explicitly setting `Text` colors to `colors.primary` / `colors.secondary` overrides Material defaults; that may be desired, but if not, consider letting `TextButton` use theme defaults for better consistency with other dialogs.

## Accessibility & internationalization

- **Hardcoded strings**
  - Several user-visible strings are hardcoded instead of using string resources:
    - `contentDescription = "Back"` in the top app bar.
    - `"OK"` and `"Cancel"` in `TripDatePickerDialog`.
    - `"Tap to add picture"` and `"Change"` in `TripCoverPicker`.
  - Recommendation:
    - Move all user-facing text to `strings.xml` and use `stringResource` to support i18n and make copy consistent with the rest of the app.

- **Content descriptions**
  - The back button has a content description, which is good for TalkBack users, but it should also be localized.
  - Other visual elements (cover image, placeholder icon) intentionally have `contentDescription = null`, which is reasonable if they are purely decorative and the interactive surface is otherwise described.

## Composability & reusability

- **SingleDayToggleRow**
  - Well-encapsulated and stateless, taking `isSingleDay` and `onToggleChange`.
  - Potentially reusable in other date-based flows (e.g., activity form) with minor generalization (label text as a parameter).

- **TripDatePickerDialog**
  - Encapsulates the date picker dialog logic with conversion between `LocalDate` and epoch millis.
  - Consider adding parameters for label texts and optional min/max dates so it can be reused across the app (e.g., for Activity form with constrained ranges).
  - Ensure that any new usages keep control over when the dialog is shown (boolean in state) in the ViewModel, keeping the composable itself purely presentational.

## Alignment with future TripFormViewModel & navigation

- The current `TripFormScreen` is a good starting point for layout and UX, but it needs the following to align with the broader architecture:
  - A `TripFormViewModel` holding:
    - `TripFormUiState` with all form fields, validation errors, loading flags, and navigation effects.
    - `TripFormEvent` to handle user actions (field changes, date taps, cover selection, save, back).
    - `TripFormEffect` for one-off events like navigation back or opening pickers/snackbars.
  - A navigation composable (`TripFormRoute`) that:
    - Reads a `tripId` argument when present (edit mode).
    - Calls `onBackClick` and reacts to `TripFormEffect` using the existing navigation observer pattern.
  - Once that is in place, `TripFormScreen` should be refactored to:
    - Derive its state entirely from `TripFormUiState`.
    - Call `onEvent` instead of mutating local state directly (e.g., `onDestinationChanged`, `onStartDateTap`, `onEndDateTap`, `onSingleDayToggled`, `onNotesChanged`, `onCoverClicked`, `onSaveClicked`).

## Suggested next steps

1. Implement `TripFormUiState`, `TripFormEvent`, `TripFormEffect`, and `TripFormViewModel` following the patterns from `features.register` and `features.login`.
2. Refactor `TripFormScreen` into a stateless, state-driven composable that consumes `TripFormUiState` and emits events.
3. Add validation and error display per field, and wire `isSaveEnabled`/`isSaving` to the primary button.
4. Extract all remaining hardcoded strings into `strings.xml` and replace with `stringResource` usages.
5. Replace hardcoded `dp` values with constants from `Dimensions`.
6. Introduce a dedicated `TripFormRoute` composable that wires navigation, ViewModel, and `TripFormScreen` together.***
