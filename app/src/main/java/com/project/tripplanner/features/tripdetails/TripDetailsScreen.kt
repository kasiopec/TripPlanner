package com.project.tripplanner.features.tripdetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.project.tripplanner.R
import com.project.tripplanner.data.model.ItineraryType
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import com.mohamedrejeb.compose.dnd.reorder.rememberReorderState
import com.project.tripplanner.ui.components.CalendarRow
import com.project.tripplanner.ui.components.DayItem
import com.project.tripplanner.ui.components.ItineraryItemCard
import com.project.tripplanner.ui.components.ItineraryUiModel
import com.project.tripplanner.ui.components.SplitButton
import com.project.tripplanner.ui.components.text.Headline2
import com.project.tripplanner.ui.components.text.Headline3
import com.project.tripplanner.ui.components.text.MetaText
import com.project.tripplanner.ui.theme.Dimensions
import com.project.tripplanner.ui.theme.TripPlannerTheme
import java.time.LocalDate
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

private const val DRAGGED_ITEM_Z_INDEX = 2f
private const val CTA_Z_INDEX = 1f
private const val REORDER_SNAP_TO_TOP_MAX_INDEX = 1
private const val REORDER_AUTO_SCROLL_EDGE_THRESHOLD = 1
private const val REORDER_AUTO_SCROLL_ITEM_SIZE_MULTIPLIER = 2f

@Composable
fun TripDetailsScreen(
    uiState: TripDetailsUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (TripDetailsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val reorderState = rememberReorderState<ItineraryUiModel>(dragAfterLongPress = true)
    val coroutineScope = rememberCoroutineScope()
    var expandedItineraryItemIds by remember { mutableStateOf(setOf<String>()) }
    var displayedItinerary by remember { mutableStateOf(uiState.itinerary) }

    LaunchedEffect(uiState.isReorderMode, uiState.itinerary) {
        val incomingItinerary = uiState.itinerary
        val shouldSyncFromUiState = uiState.isReorderMode.not() ||
                displayedItinerary.map(ItineraryUiModel::id) != incomingItinerary.map(ItineraryUiModel::id)
        if (shouldSyncFromUiState) {
            displayedItinerary = incomingItinerary
        }
    }

    TripDetailsScaffold(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = onEvent,
        onDoneClick = {
            onEvent(
                TripDetailsEvent.DoneClicked(
                    orderedIds = displayedItinerary.map(ItineraryUiModel::id)
                )
            )
        },
        listState = listState,
        modifier = modifier
    ) {
        ReorderContainer(
            state = reorderState,
            enabled = uiState.isReorderMode
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(
                    start = Dimensions.spacingL,
                    end = Dimensions.spacingL,
                    bottom = Dimensions.fabSize + Dimensions.spacingXL
                ),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingM)
            ) {
                itemsIndexed(
                    items = displayedItinerary,
                    key = { _, item -> item.id }
                ) { _, item ->
                    val isExpanded = expandedItineraryItemIds.contains(item.id)

                    ReorderableItem(
                        state = reorderState,
                        key = item.id,
                        data = item,
                        enabled = uiState.isReorderMode,
                        onDragEnter = {
                            if (!uiState.isReorderMode) return@ReorderableItem
                            val targetIndex = displayedItinerary.indexOfFirst { it.id == item.id }
                            if (targetIndex == -1) return@ReorderableItem
                            coroutineScope.launch {
                                handleReorderAutoScroll(
                                    lazyListState = listState,
                                    targetIndex = targetIndex
                                )
                            }
                        },
                        onDrop = { draggedItem ->
                            if (!uiState.isReorderMode) return@ReorderableItem
                            val currentItinerary = displayedItinerary
                            val result = reorderItinerary(
                                itinerary = currentItinerary,
                                draggedId = draggedItem.data.id,
                                targetId = item.id
                            ) ?: return@ReorderableItem

                            displayedItinerary = result.itinerary
                            if (result.insertedIndex <= REORDER_SNAP_TO_TOP_MAX_INDEX) {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            }
                        }
                    ) {
                        ItineraryItemCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    alpha = if (isDragging) 0f else 1f
                                }
                                .zIndex(if (isDragging) DRAGGED_ITEM_Z_INDEX else 0f),
                            itinerary = item,
                            isExpanded = isExpanded,
                            isReorderMode = uiState.isReorderMode,
                            onExpandedChange = { expanded ->
                                expandedItineraryItemIds = if (expanded) {
                                    expandedItineraryItemIds + item.id
                                } else {
                                    expandedItineraryItemIds - item.id
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TripDetailsScaffold(
    uiState: TripDetailsUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (TripDetailsEvent) -> Unit,
    onDoneClick: () -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colors = TripPlannerTheme.colors
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val ctaVisible by rememberCtaVisibility(listState)

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = colors.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Headline2(text = uiState.tripTitle, color = colors.onBackground)
                        MetaText(text = uiState.tripDateRange, color = colors.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(TripDetailsEvent.BackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                            tint = colors.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colors.background
                ),
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                CalendarRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Dimensions.spacingM),
                    days = uiState.days,
                    onDaySelected = { onEvent(TripDetailsEvent.DaySelected(it)) }
                )
                Box(modifier = Modifier.weight(1f)) {
                    content()
                }
            }

            TripDetailsFloatingCta(
                visible = ctaVisible,
                isReorderMode = uiState.isReorderMode,
                onAddPlacesClick = { onEvent(TripDetailsEvent.AddPlacesClicked) },
                onReorderClick = { onEvent(TripDetailsEvent.ReorderClicked) },
                onDoneClick = onDoneClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(Dimensions.spacingL)
                    .zIndex(CTA_Z_INDEX)
            )
        }
    }
}

@Composable
private fun TripDetailsFloatingCta(
    visible: Boolean,
    isReorderMode: Boolean,
    onAddPlacesClick: () -> Unit,
    onReorderClick: () -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = TripPlannerTheme.colors

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        if (isReorderMode) {
            ExtendedFloatingActionButton(
                onClick = onDoneClick,
                containerColor = colors.primary,
                contentColor = colors.onPrimary
            ) {
                Headline3(text = stringResource(R.string.trip_details_done), color = colors.onPrimary)
            }
        } else {
            SplitButton(
                onAddPlacesClick = onAddPlacesClick,
                onReorderClick = onReorderClick
            )
        }
    }
}

@Composable
private fun rememberCtaVisibility(listState: LazyListState): State<Boolean> {
    var previousIndex by remember { mutableIntStateOf(listState.firstVisibleItemIndex) }
    var previousOffset by remember { mutableIntStateOf(listState.firstVisibleItemScrollOffset) }
    val isVisible = remember { mutableStateOf(true) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .distinctUntilChanged()
            .collectLatest { (index, offset) ->
                val isUp = if (index != previousIndex) {
                    index < previousIndex
                } else {
                    offset < previousOffset
                }
                previousIndex = index
                previousOffset = offset
                isVisible.value = index == 0 || isUp
            }
    }
    return isVisible
}

@Composable
@Preview(showBackground = true)
private fun TripDetailsScreenPreview() {
    val startDate = LocalDate.of(2025, 1, 12)
    val sampleDays = (0..4).map { index ->
        val date = startDate.plusDays(index.toLong())
        DayItem(
            dayIndex = index + 1,
            date = date,
            isSelected = index == 0
        )
    }
    val sampleItinerary = listOf(
        ItineraryUiModel(
            id = "1",
            title = "Colosseum",
            categoryLabelResId = R.string.itinerary_type_activity,
            durationText = "2h",
            type = ItineraryType.Activity,
            hasMap = true,
            hasDocs = false
        ),
        ItineraryUiModel(
            id = "2",
            title = "Roman Forum",
            categoryLabelResId = R.string.itinerary_type_activity,
            durationText = "1.5h",
            type = ItineraryType.Activity,
            hasMap = true,
            hasDocs = false
        )
    )

    TripPlannerTheme {
        TripDetailsScreen(
            uiState = TripDetailsUiState(
                isInitialLoading = false,
                tripTitle = "Rome Trip",
                tripDateRange = "May 12, 2025 - May 16, 2025",
                days = sampleDays,
                selectedDate = startDate,
                itinerary = sampleItinerary
            ),
            snackbarHostState = SnackbarHostState(),
            onEvent = {}
        )
    }
}

private suspend fun handleReorderAutoScroll(
    lazyListState: LazyListState,
    targetIndex: Int
) {
    val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
    val firstVisibleItemScrollOffset = lazyListState.firstVisibleItemScrollOffset

    if (targetIndex <= REORDER_AUTO_SCROLL_EDGE_THRESHOLD) {
        lazyListState.scrollToItem(firstVisibleItemIndex, firstVisibleItemScrollOffset)
    }

    val lastVisibleItemIndex =
        lazyListState.firstVisibleItemIndex + lazyListState.layoutInfo.visibleItemsInfo.lastIndex

    val firstVisibleItem = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull() ?: return
    val scrollAmount = firstVisibleItem.size * REORDER_AUTO_SCROLL_ITEM_SIZE_MULTIPLIER

    if (targetIndex <= firstVisibleItemIndex + REORDER_AUTO_SCROLL_EDGE_THRESHOLD) {
        lazyListState.animateScrollBy(-scrollAmount)
    } else if (targetIndex == lastVisibleItemIndex) {
        lazyListState.animateScrollBy(scrollAmount)
    }
}

private data class ItineraryReorderResult(
    val itinerary: List<ItineraryUiModel>,
    val insertedIndex: Int
)

private fun reorderItinerary(
    itinerary: List<ItineraryUiModel>,
    draggedId: String,
    targetId: String
): ItineraryReorderResult? {
    val fromIndex = itinerary.indexOfFirst { it.id == draggedId }
    val targetIndex = itinerary.indexOfFirst { it.id == targetId }
    if (fromIndex == -1 || targetIndex == -1 || fromIndex == targetIndex) return null

    val mutableItinerary = itinerary.toMutableList()
    val movedItem = mutableItinerary.removeAt(fromIndex)

    val desiredIndex = if (fromIndex < targetIndex) targetIndex + 1 else targetIndex
    val insertIndex = desiredIndex.coerceIn(0, mutableItinerary.size)
    mutableItinerary.add(insertIndex, movedItem)

    return ItineraryReorderResult(
        itinerary = mutableItinerary,
        insertedIndex = insertIndex
    )
}
