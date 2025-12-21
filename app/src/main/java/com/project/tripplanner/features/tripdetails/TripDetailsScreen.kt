package com.project.tripplanner.features.tripdetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.compose.runtime.snapshotFlow
import com.project.tripplanner.R
import com.project.tripplanner.data.model.ItineraryType
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

private const val DRAGGED_ITEM_Z_INDEX = 2f
private const val CTA_Z_INDEX = 1f

@Composable
fun TripDetailsScreen(
    uiState: TripDetailsUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (TripDetailsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val reorderState = rememberReorderState(listState) { from, to ->
        onEvent(TripDetailsEvent.ItineraryItemMoved(from, to))
    }
    var expandedItemIds by remember { mutableStateOf(setOf<String>()) }

    TripDetailsScaffold(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = onEvent,
        listState = listState,
        modifier = modifier
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
                items = uiState.itinerary,
                key = { _, item -> item.id }
            ) { index, item ->
                val isExpanded = expandedItemIds.contains(item.id)
                val isDragging = uiState.isReorderMode && reorderState.draggedIndex == index
                val dragOffset = if (isDragging) reorderState.dragOffset else 0f

                ItineraryItemCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(if (isDragging) DRAGGED_ITEM_Z_INDEX else 0f)
                        .graphicsLayer { translationY = dragOffset }
                        .then(
                            if (uiState.isReorderMode) {
                                Modifier.pointerInput(reorderState) {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = { reorderState.onDragStart(index) },
                                        onDragCancel = reorderState::onDragEnd,
                                        onDragEnd = reorderState::onDragEnd,
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            reorderState.onDrag(dragAmount.y)
                                        }
                                    )
                                }
                            } else {
                                Modifier
                            }
                        ),
                    itinerary = item,
                    isExpanded = isExpanded,
                    onExpandedChange = { expanded ->
                        expandedItemIds = if (expanded) {
                            expandedItemIds + item.id
                        } else {
                            expandedItemIds - item.id
                        }
                    }
                )
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
                onDoneClick = { onEvent(TripDetailsEvent.DoneClicked) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
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
        enter = slideInVertically { it },
        exit = slideOutVertically { it },
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
    var isScrollingUp by remember { mutableStateOf(true) }
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
                isScrollingUp = isUp
                previousIndex = index
                previousOffset = offset
                isVisible.value = index == 0 || isScrollingUp
            }
    }
    return isVisible
}

@Stable
private class ReorderState(
    private val listState: LazyListState,
    private val onMove: (Int, Int) -> Unit
) {
    var draggedIndex by mutableStateOf<Int?>(null)
        private set
    var dragOffset by mutableFloatStateOf(0f)
        private set

    fun onDragStart(index: Int) {
        draggedIndex = index
    }

    fun onDrag(dragDelta: Float) {
        val draggingIndex = draggedIndex ?: return
        dragOffset += dragDelta
        val visibleItems = listState.layoutInfo.visibleItemsInfo
        val draggedItem = visibleItems.firstOrNull { it.index == draggingIndex } ?: return
        val draggedItemMiddle = draggedItem.offset + dragOffset + draggedItem.size / 2f
        val targetItem = visibleItems.firstOrNull { item ->
            item.index != draggingIndex &&
                draggedItemMiddle in item.offset.toFloat()..(item.offset + item.size).toFloat()
        } ?: return

        if (targetItem.index != draggingIndex) {
            onMove(draggingIndex, targetItem.index)
            draggedIndex = targetItem.index
            dragOffset += draggedItem.offset - targetItem.offset
        }
    }

    fun onDragEnd() {
        draggedIndex = null
        dragOffset = 0f
    }
}

@Composable
private fun rememberReorderState(
    listState: LazyListState,
    onMove: (Int, Int) -> Unit
): ReorderState {
    return remember(listState, onMove) { ReorderState(listState, onMove) }
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
            categoryName = "Sightseeing",
            durationText = "2h",
            type = ItineraryType.Activity,
            hasMap = true,
            hasDocs = false
        ),
        ItineraryUiModel(
            id = "2",
            title = "Roman Forum",
            categoryName = "Historical Tour",
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
