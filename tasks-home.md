# Home Screen Implementation - v3

This document refines Task 2 from `tasks.md` with a concrete implementation plan for the Home screen.

## 1. Goals

- Show either:
  - A **current-trip hero** when there is a trip in progress, or
  - A **countdown hero** (as a separate hero item based on `CountdownCard`) for the next upcoming trip when there is no current trip.
- Show a **filterable list of trips** (All / Upcoming / Ended) with destination, date range, status label, and optional cover image.
- Handle initial loading, error, empty, and content states in a clear but minimal way.
- Keep Home logic localized to the `features.home` package using the app's existing MVI pattern.
- Respect global edge-to-edge and design-system rules defined in `AGENTS.md`.
- Use the final visual reference in `home_layout_final.svg` as the baseline layout.

---

## 2. Feature Package Structure

- Package: `com.project.tripplanner.features.home`.

- State:
  - Define a single `data class HomeUiState : State` with fields such as:
    - `val isInitialLoading: Boolean`.
    - `val error: ErrorState?` (null when no error).
    - `val trips: List<TripUiModel>`.
    - `val currentTripId: Long?` (id of the in-progress trip shown in the current-trip hero, or null).
    - `val countdown: Countdown?`.
    - `val countdownTripId: Long?` (id of the trip shown in the separate countdown hero item when there is no current trip, or null).
    - `val activeFilter: HomeFilter` (for example, `All`, `Upcoming`, `Ended`).
  - `HomeUiState` must be a pure data holder with no helper methods or formatting logic.
  - UI states (loading, error, empty, content) are derived in the composables from these fields rather than via a sealed hierarchy.

- Events (`HomeEvent : Event`):
  - `data object ScreenLoaded : HomeEvent` - initial load.
  - `data object RetryClicked : HomeEvent` - from full-screen error retry.
  - `data class TripClicked(val tripId: Long) : HomeEvent` - tap on a trip card or hero (current-trip or countdown hero item).
  - `data class FilterSelected(val filter: HomeFilter) : HomeEvent` - user taps one of the filter chips.

- Effects (`HomeEffect : Effect`):
  - `data class NavigateToTripDetail(val tripId: Long) : HomeEffect`.
  - `data class ShowSnackbar(@StringRes val messageResId: Int) : HomeEffect`.

- Trip UI model:
  - `data class TripUiModel(...)` in `features.home` with at minimum:
    - `id: Long`.
    - `destination: String`.
    - `dateRangeText: String` - preformatted using UI-layer date formatting.
    - `status: TripStatusUi` - enum for `None` / `InProgress` / `Ended`, where:
      - `None` means the trip has not started yet (upcoming).
      - `InProgress` means the trip is currently active.
      - `Ended` means the trip is finished.
    - `@StringRes statusLabelResId: Int` - label for the status chip.
    - `coverImageUri: Uri?` - resolved via `TripCoverImageStorage` (nullable).
  - `TripStatusUi` is an enum that maps status to a `@StringRes` label id.
  - Do not include countdown-specific flags in `TripUiModel` (countdown is driven by separate fields in `HomeUiState`).

---

## 3. ViewModel & Mapping Logic

- `HomeViewModel`:
  - Implement `@HiltViewModel class HomeViewModel @Inject constructor(...) : BaseViewModel<HomeEvent, HomeUiState, HomeEffect>`.
  - Inject `TripRepository`, `ClockProvider`, `CountdownFormatter`, `TripCoverImageStorage`, and `DateFormatter` (or equivalent) directly into the ViewModel.

- Mapping from `Trip` to `HomeUiState`:
  - Subscribe to `TripRepository.observeTrips()` in `HomeViewModel` and, for each emission:
    - Map each `Trip` to a `TripUiModel`:
      - Derive `TripStatusUi` from `Trip.startDate`/`Trip.endDate` vs `ClockProvider.now()` using device timezone (as described in `tasks.md` and `plan.md`):
        - `None` (upcoming) when `now` is before `startDate`.
        - `InProgress` when `now` is between `startDate` and `endDate` (inclusive).
        - `Ended` when `now` is after `endDate`.
      - Build a display date range using a UI-layer formatter (for example, `DateFormatter`).
      - Resolve `coverImageUri` via `TripCoverImageStorage`; fall back to null when unavailable.
    - Sort trips in a deterministic order such as:
      - `InProgress` first, then `None` (upcoming), then `Ended`, with stable ordering inside each group.
    - Derive hero-related fields for the hero area:
      - Set `currentTripId` to the id of the in-progress trip chosen to be featured (for MVP, the earliest-starting in-progress trip, or null if none).
      - When `currentTripId` is null:
        - Find the next upcoming trip among `None`-status trips (earliest `startDate`).
        - Compute `Countdown` for it via `CountdownFormatter`.
        - Set `countdown` + `countdownTripId` only when there is a valid upcoming trip and the countdown is not expired so that a dedicated countdown hero item can be rendered.
      - When `currentTripId` is not null:
        - Do not compute or expose any countdown hero item (`countdown = null`, `countdownTripId = null`).
    - Populate `HomeUiState` with:
      - `isInitialLoading = false`.
      - `isRefreshing` preserved when handling a refresh.
      - `error = null` when mapping succeeds.
      - `trips = mapped list`.
      - `currentTripId`, `countdown`, `countdownTripId`, and the last selected `activeFilter` value.
  - Mapping logic can live as private functions inside `HomeViewModel`; a separate mapper class is not required for MVP. If reused later, it can be extracted.

- Error and refresh behavior (keep simple):
  - On `HomeEvent.ScreenLoaded`:
    - Set `isInitialLoading = true`, `error = null`, and start collecting trips.
    - On success, update `HomeUiState` as above.
    - On failure when no trips are loaded yet, set `error` to an appropriate `ErrorState` and `isInitialLoading = false`.
  - On `HomeEvent.RefreshRequested`:
    - If trips are already displayed, set `isRefreshing = true`, try to reload, then reset `isRefreshing = false` in `finally`.
    - On failure with existing trips, keep `trips` as-is and emit `ShowSnackbar` instead of replacing content with full-screen error.
  - On `HomeEvent.RetryClicked`:
    - Used when there is a full-screen error and no trips; re-trigger the same loading logic as `ScreenLoaded`.
  - On `HomeEvent.FilterSelected`:
    - Update `activeFilter` in state and re-derive the filtered list for the UI (the underlying `trips` list remains the single source of truth).
  - On `HomeEvent.TripClicked`:
    - Emit `HomeEffect.NavigateToTripDetail(tripId)`.

---

## 4. UI Structure & Screen-State Composables

- Main route:
  - `HomeRoute` (navigation entry) remains the stateful wrapper:
    - Retrieves `HomeViewModel` via Hilt.
    - Collects `HomeUiState` via `state.collectAsState()`.
    - Emits `HomeEvent.ScreenLoaded` once.
    - Passes callbacks to `HomeScreen`.
    - Observes `HomeEffect` and calls navigation/snackbar APIs.

- Stateless `HomeScreen`:
  - Signature: `@Composable fun HomeScreen(uiState: HomeUiState, ...)`.
  - Responsible only for:
    - Choosing between Loading / Error / Empty / Content states based on `HomeUiState`.
    - Delegating to dedicated composables for those states (no inline loading/error/empty implementations).
    - Passing down callbacks: refresh, retry, trip click, filter selection.

- Dedicated screen-state composables (kept simple and reusable):
  - `HomeLoading(...)` - full-screen loading indicator.
  - `HomeError(...)` - full-screen error using `ErrorState` and `FullScreenError` under the hood.
  - `HomeEmptyState(...)` - empty state encouraging creation of the first trip and visually aligned with the “+” create affordance in the bottom bar.
  - These composables must live in the `features.home` package (for example, `HomeScreenStates.kt` or a `ui` subpackage), not nested inside `HomeScreen`.

- Content layout:
  - When there are trips:
    - Hero area:
      - If `uiState.currentTripId != null`:
        - Render a "CURRENT TRIP" hero using the matching `TripUiModel` (destination, date range, simple progress hint).
        - This hero is the only place where an in-progress trip is shown; the list below must not duplicate it.
      - Else, if `uiState.countdown != null` and `uiState.countdownTripId != null` matches an upcoming trip:
        - Render a single countdown hero item (a dedicated hero composable visually derived from `CountdownCard`, not a variant of `HomeHero`) for the next upcoming trip at the top of the content.
    - Filter chips:
      - Render a horizontal chip row just under the hero area with `All`, `Upcoming`, and `Ended`.
      - Chips are mutually exclusive and reflect `uiState.activeFilter`.
    - List:
      - Below the chip row, render a `LazyColumn` of full-width `TripCard` items keyed by trip id.
      - The list respects the `activeFilter` (All / Upcoming / Ended) and excludes the current trip when `currentTripId` is not null.
  - When there are no trips and no error:
    - Show `HomeEmptyState`. A clean, centered component with an illustration and a catchy message to use the “+” for a new trip.


---

## 5. Shared UI Components - Usage

- `TripCard`:
  - Reuse the shared `TripCard` composable from `ui/components`.
  - It must display:
    - Destination and date range using `TripPlannerTheme.typography` via existing text components.
    - Status chip derived from `TripStatusUi` / `statusLabelResId` with colors from `TripPlannerTheme`. In the list this will be “Upcoming” or “Ended`; “On trip” status appears only in the hero.
    - Optional cover image using Coil with a deterministic placeholder for missing images.
    - A tappable surface invoking the `onClick` callback.
  - This document does not prescribe the exact parameter list as long as it uses `TripUiModel` data and fits the design system.

- `CountdownCard`:
  - Reuse the shared `CountdownCard` from `ui/components` as the visual basis for a separate countdown hero item when there is no current trip.
  - On Home:
    - The countdown appears at most once, derived from `HomeUiState.countdown` and `countdownTripId`.
    - It is not per-list-item; it appears above the list as a standalone hero item separate from `HomeHero`.
    - It should be tappable to navigate to the associated trip details.

- `FullScreenError`:
  - Use `FullScreenError` via `HomeError` for initial hard errors (for example, first load failure with no cached trips).

- Missing composables:
  - If `TripCard`, `CountdownCard`, `FullScreenError`, or other referenced components are missing or their existing API diverges significantly from this plan:
    - Do not silently substitute or implement ad-hoc alternatives.
    - Stop and align on whether to add or adjust shared components before continuing.

---

## 6. Edge-to-Edge & Insets

- Home screen must follow the global edge-to-edge rules defined in `AGENTS.md` and the app's host `Activity`:
  - Do not use deprecated APIs such as `android:fitsSystemWindows` or legacy insets libraries.
  - Use the standard app pattern (for example, `Scaffold` with `contentWindowInsets` and insets-aware modifiers) rather than custom, one-off insets logic.
- This document does not define a separate edge-to-edge strategy for Home; it relies on the app-wide convention.

---

## 7. Navigation Integration

- `Screen.Home`:
  - Remains the main destination with `isBottomBarVisible = true`.
  - The primary create action is a centered “+” in the bottom bar, which navigates to `Screen.TripForm` in create mode (no Home-specific event needed).

- Nav graph:
  - In `NavGraph.kt`, the Home route:
    - Uses `HomeRoute` as the entry.
    - Reacts to `HomeEffect.NavigateToTripDetail` to navigate to a trip detail destination (or Trip form edit mode, depending on current MVP choices).
    - Reacts to `HomeEffect.ShowSnackbar` by showing a message (for example, using `Toast` or a shared `SnackbarHost`).

---

## 8. Follow-Ups / Post-MVP

- Move more advanced edge cases (midnight boundaries, large lists, image performance, broken cover URIs) and exhaustive timezone tests into the existing "Post-MVP test hardening" or a dedicated follow-up doc.
- If multiple features need similar mapping logic, consider re-introducing a shared mapper/use-case layer with consistent patterns after the first version is stable.
