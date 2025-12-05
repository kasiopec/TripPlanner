TripPlanner MVP Plan

Scope and assumptions
- Multi-day trips; device timezone only.
- Local-only Room for MVP; keep repos interface-ready for future remote sync.
- Always follow the app's defined design-system theme (colors, typography, spacing) as the single source of truth; never derive colors from arbitrary example images, marketing mocks, or unrelated web screenshots attached for reference.
- End date mandatory; when user marks a trip as single-day, auto-fill end = start and keep it in sync, otherwise user must explicitly choose both start and end dates.
- Include cover images from a small local set and activity type icons; drag-and-drop ordering required.
- Countdown for upcoming trips only with two modes (>24h days, <24h ticking); hide countdown once a trip is in progress or ended.
- Use existing bottom bar + for "add trip"; navigation already initialized for home.

Data model
- Trip: id, title/destination, startDate, endDate, timezone (device), coverImageUri?, notes, createdAt, updatedAt. For MVP, coverImageUri points to bundled/local images but remains flexible for future remote sources.
- ItineraryItem: id, tripId, localDate, localTime?, title, type (Flight/Hotel/Activity/Food/Shopping), location, notes, sortOrder.
- Derived: day offsets/display strings from stored dates/times; countdown uses now() vs startDate in device timezone.

Architecture and data
- Room database with java.time converters; DAOs for Trip, Itinerary, and TripWithItinerary relation.
- Repositories: TripRepository (list/detail CRUD) and ItineraryRepository (CRUD + reorder persisting sortOrder), with explicit error surfaces for failures.
- Clock provider for countdown and time logic; Hilt modules for DB, DAOs, repos; interfaces stay stable for later remote swap.

Features and navigation
- Trips list (home): cards with cover image placeholder; countdown chip only for upcoming trips; ended/in-progress trips show status label instead; empty state CTA; bottom-bar + -> TripForm(new); tap card -> TripDetail.
- TripForm: create/edit; required destination/start/end; optional notes/cover image; includes a single-day trip option that auto-fills end = start (and disables manual end selection) when enabled; otherwise user selects both start and end; save -> repo -> back; surface validation and save errors via MVI effects (e.g., snackbar).
- TripDetail (itinerary screen): full-screen header with destination, date range, status; countdown visible only for upcoming trips; date strip from start->end to pick day; timeline for selected day.
- Timeline: grouped by day, vertical connector, icons by type; drag-and-drop reorder persists sortOrder using standard Compose APIs (no external drag-and-drop libs).
- ActivityForm: modal/sheet; title, date (within trip range), time, type selector with icons, location, notes; add/edit flow; error handling for invalid input and save failures.
- Navigation routes: Trips (start) -> TripForm(new/edit) -> TripDetail(id) -> ActivityForm(id?, tripId, date).

State management
- MVI per feature (UiState/Event/Effect); ViewModels inject repos + clock; derive countdown, enforce date constraints, handle reorder; countdown ticks when <24h before start (minute interval) or on resume for upcoming trips only; error states surfaced via effects and dedicated error fields in UiState.

UI behavior
- Device timezone formatting for dates/times; empty states encourage first trip; "in progress" label while current; "ended" label for past trips; no countdown chip for in-progress/ended trips.
- Sorting: per-day timeline sorted by sortOrder; default sortOrder can be time-based when adding.

Near-term tasks
- Confirm data layer foundation and time helpers (already implemented) match this plan and keep tests green.
- Build Trips list UI with countdowns for upcoming trips, status labels, error handling, and bottom-bar + action.
- Build TripForm; wire create/edit flows and single-day toggle behavior (end auto-filled = start only when single-day is enabled); include validation, error states, and tests.
- Build TripDetail with header, date strip, timeline shell, status handling, and basic error/loading states.
- Build ActivityForm with type picker and date/time constraints; hook into repo with validation and error handling.
- Add drag-and-drop to timeline using standard Compose APIs; persist new sortOrder.
- Polish visuals (cover image handling from local set, icons) and navigation wiring.

Future plans (out-of-scope now)
- Remote backend (e.g., Supabase) with sync and conflict handling.
- Offline cache/sync improvements beyond local-only baseline.
- Notifications/reminders, auth/login, payments.
- Export/share trips (text/PDF/link); map/holiday/weather integrations.
- Extended ViewModel and UI test coverage across Trips list, TripDetail, and ActivityForm once flows stabilize.
