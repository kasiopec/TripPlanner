TripPlanner MVP Plan

Scope and assumptions
- Multi-day trips; device timezone only.
- Local-only Room for MVP; keep repos interface-ready for future remote sync.
- End date mandatory; prompt and auto-fill end = start for single-day.
- Include cover images and activity type icons; drag-and-drop ordering required.
- Countdown two-mode (>24h days, <24h ticking).
- Use existing bottom bar + for "add trip"; navigation already initialized for home.

Data model
- Trip: id, title/destination, startDate, endDate, timezone (device), coverImageUri?, notes, createdAt, updatedAt.
- ItineraryItem: id, tripId, localDate, localTime?, title, type (Flight/Hotel/Activity/Food/Shopping), location, notes, sortOrder.
- Derived: day offsets/display strings from stored dates/times; countdown uses now() vs startDate in device timezone.

Architecture and data
- Room database with java.time converters; DAOs for Trip, Itinerary, and TripWithItinerary relation.
- Repositories: TripRepository (list/detail CRUD) and ItineraryRepository (CRUD + reorder persisting sortOrder).
- Clock provider for countdown and time logic; Hilt modules for DB, DAOs, repos; interfaces stay stable for later remote swap.

Features and navigation
- Trips list (home): cards with cover image placeholder; countdown chip; empty state CTA; bottom-bar + -> TripForm(new); tap card -> TripDetail.
- TripForm: create/edit; required destination/start/end; optional notes/cover image; end auto-filled = start with prompt; save -> repo -> back.
- TripDetail: header with destination, date range, countdown status; date strip from start->end to pick day; timeline for selected day.
- Timeline: grouped by day, vertical connector, icons by type; drag-and-drop reorder persists sortOrder.
- ActivityForm: modal/sheet; title, date (within trip range), time, type selector with icons, location, notes; add/edit flow.
- Navigation routes: Trips (start) -> TripForm(new/edit) -> TripDetail(id) -> ActivityForm(id?, tripId, date).

State management
- MVI per feature (UiState/Event/Effect); ViewModels inject repos + clock; derive countdown, enforce date constraints, handle reorder; countdown ticks when <24h (minute interval) or on resume.

UI behavior
- Device timezone formatting for dates/times; empty states encourage first trip; "ended" label for past trips.
- Sorting: per-day timeline sorted by sortOrder; default sortOrder can be time-based when adding.

Near-term tasks
- Add entities/DAOs, converters, Room DB, Hilt modules, repo interfaces + local impls, clock helper/formatter.
- Build Trips list UI with countdowns and bottom-bar + action.
- Build TripForm; wire create/edit flows and end-date auto-fill prompt.
- Build TripDetail with header, date strip, and timeline shell.
- Build ActivityForm with type picker and date/time constraints; hook into repo.
- Add drag-and-drop to timeline; persist new sortOrder.
- Polish visuals (cover image handling, icons) and navigation wiring.

Future plans (out-of-scope now)
- Remote backend (e.g., Supabase) with sync and conflict handling.
- Offline cache/sync improvements beyond local-only baseline.
- Notifications/reminders, auth/login, payments.
- Export/share trips (text/PDF/link); map/holiday/weather integrations.
