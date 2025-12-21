mplementation Details & Clarifications
Data Layer Verification
ItineraryRepository: Confirmed existence in com.project.tripplanner.repositories. It has

observeItineraryForDate
(for day view) and

reorderItems
(taking a list of IDs).
ItineraryItem: Confirmed existence in com.project.tripplanner.data.model. Contains all necessary fields (id, localDate, type, sortOrder).
Reorder Mode Logic
Trigger: Toggled via

SplitButton
("Reorder" side) -> ViewModel.toggleReorderMode -> UiState.isReorderMode = true.
Interaction: We will implement standard Jetpack Compose drag-and-drop.
Optimistic Update: On item move, ViewModel immediately swaps items in TripDetailsUiState.itinerary to ensure UI responsiveness.
Persistence: ViewModel calls ItineraryRepository.reorderItems(tripId, newIdList) to commit changes.
Exit Strategy:
When isReorderMode is true, the SplitButton is replaced by a "Done" button (Single FAB or Button).
Clicking "Done" commits the final order (if not already done incrementally) and sets isReorderMode = false.
Floating CTA Logic
Placement: Box layout with

SplitButton
aligned to Alignment.BottomEnd (with padding).
Visibility/Animation:
We will use AnimatedVisibility (slideIn/slideOut).
Trigger: derivedStateOf noticing scroll direction.
Logic: Visible when listState.firstVisibleItemIndex == 0 (at top) OR listState.isScrollingUp(). Hides when scrolling down.
Feature: Trip Details (features.tripdetails)
[MODIFY]

TripRepository.kt
Add suspend fun getTrip(tripId: Long): Trip? to interface and implementation.
Reason: The ViewModel requires a single-shot load for the Trip header.
[MODIFY]

TripDao.kt
Add suspend fun getTrip(tripId: Long): TripEntity? query.
[NEW]

TripDetailsContract.kt
Define TripDetailsUiState, TripDetailsEvent, TripDetailsEffect.
UiState will hold:
isInitialLoading, error.
headerData (Title, Date Range).
days: List<DayItem> for

CalendarRow
.
selectedDate: LocalDate.
itinerary: List<ItineraryUiModel> for the selected day.
isReorderMode: Boolean.
[NEW]

TripDetailsViewModel.kt
Hilt ViewModel.
Injects

TripRepository
,

ItineraryRepository
.
Trip Load: Uses the new suspend getTrip(id) for the initial header data (title, dates, cover).
Itinerary Load: Uses

observeItineraryForDate(tripId, date)
(Flow) to reactively update the list.
Why Flow?: If the user adds/edits an item (even from a separate screen later), or if reorder changes happen, the UI must automatically reflect the new state without manual refresh calls.
Maps Trip start/end dates to List<DayItem>.
Loads itinerary items and filters by selectedDate.
Handles events: DaySelected, AddPlacesClicked, ReorderClicked, ItineraryItemClicked, DoneClicked.
[NEW]

TripDetailsScreen.kt
TripDetailsRoute: Stateful entry point.
TripDetailsScreen: Stateless UI.
Header: Destination and date range.
Day Strip: Uses

CalendarRow
.
Content: LazyColumn of

ItineraryItemCard
.
Floating CTA:

SplitButton
aligned to bottom-end, with scroll awareness.
TripDetailsLoading, TripDetailsEmptyState.
Navigation
[MODIFY]

Screen.kt
Ensure TripDetail route is defined with tripId argument.
[MODIFY]

NavGraph.kt
Add composable for TripDetail.