package com.app.feature.launchpads

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.app.core.model.Launchpad
import com.app.core.model.SortType
import com.app.core.ui.dropdown.DropDownMenuWithTitle
import com.app.core.ui.dropdown.SpaceXDropdownMenuItemWithCheckedIcon
import com.app.core.ui.error.ErrorColumn
import com.app.core.ui.lazylists.ErrorItem
import com.app.core.ui.lazylists.LoadingItem
import com.app.core.ui.loading.LoadingColumn
import kotlinx.coroutines.flow.flowOf
import java.net.UnknownHostException

@Composable
fun LaunchpadTab(
    openLaunchpadDetail: (String) -> Unit,
    viewModel: LaunchpadsViewModel = hiltViewModel()
) {
    val launchpads = viewModel.launchpads.collectAsLazyPagingItems()
    val sortType = viewModel.sortType.collectAsState()
    val uiEffects = viewModel.uiEffects.collectAsState(initial = null)
    val onSortTypeClicked: (LaunchpadsAction.ChangeSortType) -> Unit = { action ->
        viewModel.submitAction(LaunchpadsAction.ChangeSortType(action.type))
    }
    handleUiEffects(uiEffects, launchpads)

    LaunchpadsContent(launchpads, sortType, openLaunchpadDetail, onSortTypeClicked)
}

@Composable
private fun LaunchpadsContent(
    launchpads: LazyPagingItems<Launchpad>,
    sortType: State<SortType>,
    openLaunchpadDetail: (String) -> Unit,
    onSortTypeClicked: (LaunchpadsAction.ChangeSortType) -> Unit,
) {
    when (val refreshLoadState = launchpads.loadState.refresh) {
        is LoadState.Loading -> LoadingColumn()
        is LoadState.Error -> {
            if (launchpads.itemCount > 0) {
                RocketsSortTypeWithList(launchpads, sortType, onSortTypeClicked, openLaunchpadDetail)
                return
            }
            val isInternetError = refreshLoadState.error is UnknownHostException
            if (isInternetError)
                ErrorColumn(
                    textRes = R.string.spacex_app_error_internet,
                    onClick = { launchpads.refresh() }
                )
            else
                ErrorColumn(onClick = { launchpads.refresh() })
        }
        else -> RocketsSortTypeWithList(launchpads, sortType, onSortTypeClicked, openLaunchpadDetail)
    }
}

@Composable
private fun RocketsSortTypeWithList(
    launchpads: LazyPagingItems<Launchpad>,
    sortType: State<SortType>,
    onSortTypeClicked: (LaunchpadsAction.ChangeSortType) -> Unit,
    openLaunchpadDetail: (String) -> Unit,
) {
    Column {
        DropDownMenu(sortType, onSortTypeClicked)
        LazyLaunchpadsColumn(launchpads, openLaunchpadDetail)
    }
}

@Composable
private fun DropDownMenu(sortType: State<SortType>, onSortTypeClicked: (LaunchpadsAction.ChangeSortType) -> Unit) {
    DropDownMenuWithTitle {
        SpaceXDropdownMenuItemWithCheckedIcon(
            textRes = R.string.spacex_app_sort_type_asc,
            onClick = {
                onSortClick(SortType.ASC, onSortTypeClicked)
            },
            showCheckedIcon = {
                sortType.value.value == SortType.ASC.value
            }
        )

        SpaceXDropdownMenuItemWithCheckedIcon(
            textRes = R.string.spacex_app_sort_type_desc,
            onClick = {
                onSortClick(SortType.DESC, onSortTypeClicked)
            },
            showCheckedIcon = {
                sortType.value.value == SortType.DESC.value
            }
        )
    }
}

@Composable
private fun LazyLaunchpadsColumn(
    launchpads: LazyPagingItems<Launchpad>,
    openLaunchpadDetail: (String) -> Unit,
) {
    LazyColumn(verticalArrangement = Arrangement.Top) {
        items(launchpads) { item ->
            item?.let { LaunchpadCard(it, openLaunchpadDetail) }
        }

        when (launchpads.loadState.append) {
            is LoadState.NotLoading -> Unit
            LoadState.Loading -> item { LoadingItem() }
            is LoadState.Error -> item { ErrorItem { launchpads.retry() } }
            else -> {}
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewLaunchpadsContent() {
    val launchpad = Launchpad(
        "5e9e4501f5090910d4566f83",
        "VAFB SLC 3W",
        "Vandenberg Space Force Base Space Launch Complex 3W",
        "retired",
        "https://i.imgur.com/7uXe1Kv.png",
    )
    val launchpads = mutableListOf<Launchpad>()
    repeat(10) {
        launchpads.add(launchpad)
    }
    val lazyPagingLaunchpads = flowOf(PagingData.from(launchpads)).collectAsLazyPagingItems()

//    LaunchpadsContent(lazyPagingLaunchpads, {}, sortType, onSortTypeClicked)
}

private fun handleUiEffects(uiEffects: State<LaunchpadsUiEffect?>, rockets: LazyPagingItems<Launchpad>) {
    if (uiEffects.value == null) return
    when (uiEffects.value) {
        is LaunchpadsUiEffect.ChangeSortType -> {
            rockets.refresh()
        }
        else -> {}
    }
}

private fun onSortClick(type: SortType, onSortTypeClicked: (LaunchpadsAction.ChangeSortType) -> Unit) {
    onSortTypeClicked.invoke(LaunchpadsAction.ChangeSortType(type))
}