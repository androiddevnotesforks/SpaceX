package com.example.spacexapp.ui.historyevents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.spacexapp.R
import com.example.spacexapp.ui.common.error.ErrorColumn
import com.example.spacexapp.ui.common.loading.LoadingColumn
import com.example.spacexapp.ui.historyevents.historyevent.HistoryEvent
import com.example.spacexapp.ui.historyevents.historyevent.HistoryEventCard
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.getViewModel
import java.util.Date.from

@Composable
fun HistoryEventsScreen() {
    val viewModel: HistoryEventsViewModel = getViewModel()
    val historyEvents = viewModel.historyEvents.collectAsLazyPagingItems()

    HistoryEventContent(historyEvents)
}

@Composable
fun HistoryEventContent(historyEvents: LazyPagingItems<HistoryEvent>) {
    Box(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .padding(top = 15.dp),
    ) {
        when (historyEvents.loadState.refresh) {
            is LoadState.Loading ->
                LoadingColumn()

            is LoadState.Error ->
                ErrorColumn()

            else ->
                LazyHistoryEventsColumn(historyEvents)

        }
    }
}


@Composable
fun LazyHistoryEventsColumn(historyEvents: LazyPagingItems<HistoryEvent>) {
    LazyColumn {
        items(historyEvents) { item ->
            item?.let { HistoryEventCard(it) }
        }

        when (historyEvents.loadState.append) {
            is LoadState.NotLoading -> Unit
            LoadState.Loading -> {
                item {
                    LoadingItem()
                }
            }
            is LoadState.Error -> {
                item {
                    ErrorItem(message = "Some error occurred")
                }
            }
        }
    }

}

@Composable
fun LoadingItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .width(42.dp)
                .height(42.dp)
                .padding(8.dp),
            strokeWidth = 5.dp
        )

    }
}

@Composable
fun ErrorItem(message: String) {
    Card(
        elevation = 2.dp,
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Red)
                .padding(8.dp)
        ) {
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .width(42.dp)
                    .height(42.dp),
                painter = painterResource(id = R.drawable.ic_error),
                contentDescription = "",
                colorFilter = ColorFilter.tint(Color.White)
            )
            Text(
                color = Color.White,
                text = message,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Preview
@Composable
fun PreviewHistoryEventContent(){
    val historyEvents = mutableListOf<HistoryEvent>()
    repeat(10){
        historyEvents.add(
            HistoryEvent("http://www.spacex.com/news/2013/02/11/flight-4-launch-update-0",
            "Falcon reaches Earth orbit",
            1222643700,
            "Falcon 1 becomes the first privately developed liquid-fuel rocket to reach Earth orbit.")
        )
    }
    val lazyPagingHistoryEvents = flowOf(PagingData.from(historyEvents)).collectAsLazyPagingItems()

    HistoryEventContent(lazyPagingHistoryEvents)
}