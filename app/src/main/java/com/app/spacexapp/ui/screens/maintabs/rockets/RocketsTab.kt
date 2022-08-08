package com.app.spacexapp.ui.screens.maintabs.rockets

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.app.spacexapp.R
import com.app.core.ui.error.ErrorColumn
import com.app.core.ui.lazylists.ErrorItem
import com.app.core.ui.lazylists.LoadingItem
import com.app.core.ui.loading.LoadingColumn
import com.app.core.model.Rocket
import com.app.spacexapp.ui.screens.maintabs.rockets.rocket.RocketCard
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.flowOf
import java.net.UnknownHostException

@Composable
fun RocketsTab(
    openRocketDetail: (String) -> Unit,
    viewmodel: RocketsViewModel = hiltViewModel(),
) {
    val rockets = viewmodel.rockets.collectAsLazyPagingItems()

    RocketsContent(rockets, openRocketDetail)
}

@Composable
private fun RocketsContent(rockets: LazyPagingItems<Rocket>, openRocketDetail: (String) -> Unit) {
    when (val refreshLoadState = rockets.loadState.refresh) {
        is LoadState.Loading -> LoadingColumn()
        is LoadState.Error -> {
            if (rockets.itemCount > 0) {
                LazyRocketsColumn(rockets, openRocketDetail)
                return
            }
            val isInternetError = refreshLoadState.error is UnknownHostException
            if (isInternetError)
                ErrorColumn(
                    textRes = R.string.spacex_app_error_internet,
                    onClick = { rockets.refresh() }
                )
            else
                ErrorColumn(onClick = { rockets.refresh() })

        }
        else -> LazyRocketsColumn(rockets, openRocketDetail)
    }
}

@Composable
private fun LazyRocketsColumn(
    rockets: LazyPagingItems<Rocket>,
    openRocketDetail: (String) -> Unit,
) {
    LazyColumn(content = {
        items(rockets) { rocket ->
            rocket?.let { RocketCard(it, openRocketDetail) }
        }
        when (rockets.loadState.append) {
            is LoadState.NotLoading -> Unit
            LoadState.Loading -> item { LoadingItem() }
            is LoadState.Error -> item { ErrorItem { rockets.retry() } }
        }
    })
}

@Preview
@Composable
private fun PreviewRocketsTab() {
    val rockets = mutableListOf<Rocket>()
    repeat(10) {
        rockets.add(
            Rocket(
                "5e9d0d95eda69955f709d1eb",
                "Falcon 1",
                false,
                listOf("https://imgur.com/DaCfMsj.jpg"),
            ))
    }
    val lazyPagingRockets = flowOf(PagingData.from(rockets)).collectAsLazyPagingItems()
    RocketsContent(lazyPagingRockets) { }
}
