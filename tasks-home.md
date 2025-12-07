# Home Screen Implementation – v2

This document refines Task 2 from `tasks.md` with a simpler, less opinionated implementation plan for the Home screen.

## 1. Goals

- Show a list of trips with destination, date range, status label, and optional cover image.
- Show a single countdown banner for the next upcoming trip.
- Handle initial loading, error, empty, and content states in a clear but minimal way.
- Keep Home logic localized to the `features.home` package using the app’s existing MVI pattern.
- Respect global edge-to-edge and design-system rules defined in `AGENTS.md`.

---

## 2. Feature Package Structure

- Package: `com.project.tripplanner.features.home`.

- State:
  - Define a single `data class HomeUiState : State` with fields such as:
    - `val isInitialLoading: Boolean`.
    - `val isRefreshing: Boolean`.
    - `val error: ErrorState?` (null when no error).
    - `val trips: List<TripUiModel>`.
    - `val countdown: Countdown?`.
    - `val countdownTripId: Long?` (id of the trip shown in the countdown banner, or null).
  - `HomeUiState` must be a pure data holder with no helper methods or formatting logic.
  - UI states (loading, error, empty, content) are derived in the composables from these fields rather than via a sealed hierarchy.

- Events (`HomeEvent : Event`):
  - `data object ScreenLoaded : HomeEvent()` – initial load.
  - `data object RefreshRequested : HomeEvent()` – pull-to-refresh or explicit reload.
  - `data object RetryClicked : HomeEvent()` – from full-screen error retry.
  - `data class TripClicked(val tripId: Long) : HomeEvent()` – tap on a trip card.

- Effects (`HomeEffect : Effect`):
  - `data class NavigateToTripDetail(val tripId: Long) : HomeEffect()`.
  - `data class ShowSnackbar(@StringRes val messageResId: Int) : HomeEffect()`.

- Trip UI model:
  - `data class TripUiModel(...)` in `features.home` with at minimum:
    - `id: Long`.
    - `destination: String`.
    - `dateRangeText: String` - preformatted using UI-layer date formatting.
    - `status: TripStatusUi` - enum for None / InProgress / Ended, where:
      - `None` means the trip has not started yet (upcoming).
      - `InProgress` means the trip is currently active.
      - `Ended` means the trip is finished.
    - `@StringRes statusLabelResId: Int` – label for the status chip.
    - `coverImageUri: Uri?` – resolved via `TripCoverImageStorage` (nullable).
  - `TripStatusUi` is an enum that maps status to a `@StringRes` label id.
  - Do not include countdown-specific flags in `TripUiModel` (countdown is driven by separate fields in `HomeUiState`).

---

## 3. ViewModel & Mapping Logic

- `HomeViewModel`:
  - Implement `@HiltViewModel class HomeViewModel @Inject constructor(...) : BaseViewModel<HomeEvent, HomeUiState, HomeEffect>`.
  - Inject `TripRepository`, `ClockProvider`, `CountdownFormatter`, and `TripCoverImageStorage` directly into the ViewModel.

- Mapping from `Trip` to `HomeUiState`:
  - Subscribe to `TripRepository.observeTrips()` in `HomeViewModel` and, for each emission:
    - Map each `Trip` to a `TripUiModel`:
      - Derive `TripStatusUi` from `Trip.startDate`/`endDate` vs `ClockProvider.now()` using device timezone (as described in `tasks.md` and `plan.md`):
        - `None` (upcoming) when `now` is before `startDate`.
        - `InProgress` when `now` is between `startDate` and `endDate` (inclusive).
        - `Ended` when `now` is after `endDate`.
      - Build a display date range using a UI-layer formatter (for example, `DateFormatter`).
      - Resolve `coverImageUri` via `TripCoverImageStorage`; fall back to null when unavailable.
    - Sort trips so `InProgress` appear first, then `None` (upcoming), then `Ended`, with deterministic ordering inside each group.
    - Determine countdown and countdown trip as follows:
      - If there is at least one `InProgress` trip, do not show countdown at all (`countdown = null`, `countdownTripId = null`).
      - If there are no `InProgress` trips, find the next upcoming trip among `None`-status trips (earliest `startDate`) and compute `Countdown?` for it via `CountdownFormatter`.
    - Populate `HomeUiState` with:
      - `isInitialLoading = false`.
      - `isRefreshing = false` (or preserved when handling a refresh).
      - `error = null` when mapping succeeds.
      - `trips = mapped list`.
      - `countdown` and `countdownTripId` set only when there is a valid upcoming trip and a non-expired countdown.
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

- Navigation:
  - On `HomeEvent.TripClicked`, emit `HomeEffect.NavigateToTripDetail(tripId)`.

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
    - Passing down callbacks: refresh, retry, trip click.

- Dedicated screen-state composables (kept simple and reusable):
  - `HomeLoading(...)` – full-screen loading indicator.
  - `HomeError(...)` – full-screen error using `ErrorState` and `FullScreenError` under the hood.
  - `HomeEmptyState(...)` – empty state encouraging creation of the first trip and/or reflecting the bottom bar “+” affordance.
  - These composables must live in the `features.home` package (for example, `HomeScreenStates.kt` or a `ui` subpackage), not nested inside `HomeScreen`.

- Content layout:
  - When there are trips:
    - Optional `CountdownCard` at the top when `uiState.countdown != null` and `uiState.countdownTripId != null` matches an item.
    - Below the countdown, `LazyColumn` of `TripCard` items keyed by trip id.
    - `isRefreshing` can be surfaced as a thin progress bar at the top of the list or inline indicator.
  - When there are no trips and no error:
    - Show `HomeEmptyState`.

---

## 5. Shared UI Components – Usage

- `TripCard`:
  - Reuse the shared `TripCard` composable from `ui/components`.
  - It must display:
    - Destination and date range using `TripPlannerTheme.typography` via existing text components.
    - Status chip derived from `TripStatusUi` / `statusLabelResId` with colors from `TripPlannerTheme`.
    - Optional cover image using Coil with a deterministic placeholder for missing images.
    - A tappable surface invoking the `onClick` callback.
  - This document does not prescribe the exact parameter list as long as it uses `TripUiModel` data and fits the design system.

- `CountdownCard`:
  - Reuse the shared `CountdownCard` from `ui/components`.
  - On Home:
    - The card is shown at most once, using data from `HomeUiState.countdown` and the associated trip.
    - The card is not per-list-item; it appears above the list as a standalone banner.
    - It should be tappable to navigate to the associated trip details.

- Missing composables:
  - If `TripCard`, `CountdownCard`, `FullScreenError`, or other referenced components are missing or their existing API diverges significantly from this plan:
    - Do not silently substitute or implement ad-hoc alternatives.
    - Stop, notify the maintainer, and agree on whether to add or adjust shared components before continuing.

---

## 6. Edge-to-Edge & Insets

- Home screen must follow the global edge-to-edge rules defined in `AGENTS.md` and the app’s host `Activity`:
  - Do not use deprecated APIs such as `android:fitsSystemWindows` or legacy insets libraries.
  - Use the standard app pattern (for example, `Scaffold` with `contentWindowInsets` and insets-aware modifiers) rather than custom, one-off insets logic.
- This document does not define a separate edge-to-edge strategy for Home; it relies on the app-wide convention.

---

## 7. Navigation Integration

- `Screen.Home`:
  - Remains the main destination with `isBottomBarVisible = true`.
  - Bottom bar “+” continues to navigate to `Screen.TripForm` in create mode (no Home-specific event needed).

- Nav graph:
  - In `NavGraph.kt`, the Home route:
    - Uses `HomeRoute` as the entry.
    - Reacts to `HomeEffect.NavigateToTripDetail` to navigate to a trip detail destination (or Trip form edit mode, depending on current MVP choices).
    - Reacts to `HomeEffect.ShowSnackbar` by showing a message (for example, using `Toast` or a shared `SnackbarHost`).
---

## 9. Follow-Ups / Post-MVP

- Move more advanced edge cases (midnight boundaries, large lists, image performance, broken cover URIs) and exhaustive timezone tests into the existing “Post-MVP test hardening” or a dedicated follow-up doc.
- If multiple features need similar mapping logic, consider re-introducing a shared mapper/use-case layer with consistent patterns after the first version is stable.
