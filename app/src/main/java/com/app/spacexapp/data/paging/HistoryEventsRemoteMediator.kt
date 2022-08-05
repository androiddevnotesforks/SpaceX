package com.app.spacexapp.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.app.spacexapp.data.remote.SpaceXService
import com.app.spacexapp.data.local.HistoryEventsDatabase
import com.app.spacexapp.model.local.entities.HistoryEventEntity
import com.app.spacexapp.model.local.entities.RemoteKeysEntity
import com.app.spacexapp.model.remote.Options
import com.app.spacexapp.model.remote.QueryBody
import com.app.spacexapp.model.remote.mappers.HistoryEventResponseToHistoryEventEntityMapper
import com.app.spacexapp.util.Constants
import com.app.spacexapp.util.ResponseField
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class HistoryEventsRemoteMediator(
    private val spaceXService: SpaceXService,
    private val historyEventsDatabase: HistoryEventsDatabase,
    private val mapper: HistoryEventResponseToHistoryEventEntityMapper,
) : RemoteMediator<Int, HistoryEventEntity>() {

    override suspend fun initialize(): InitializeAction {
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(
            Constants.REMOTE_MEDIATOR_CACHE_TIMEOUT_IN_HOURS,
            TimeUnit.HOURS
        )
        var firstHistoryEvent: HistoryEventEntity? = null
        historyEventsDatabase.withTransaction {
            firstHistoryEvent = historyEventsDatabase.historyEventsDao().getFirst()
        }
        val isCacheTimeout = firstHistoryEvent?.let {
            (System.currentTimeMillis() - it.createdAt) >= cacheTimeout
        } ?: true
        return if (isCacheTimeout) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, HistoryEventEntity>,
    ): MediatorResult {
        val page: Int = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: HISTORY_EVENTS_STARTING_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }


            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }
        try {
            val options = Options(
                page,
                Constants.PAGE_SIZE,
                mapOf(Pair(ResponseField.eventDateUnix, Constants.Network.SORT_BY_DESC))
            )
            val queryBody = QueryBody(options)
            val apiResponse = spaceXService.getHistoryEvents(queryBody)
            val endOfPaginationReached = page >= apiResponse.totalPages
            val historyEvents = apiResponse.historyEvents
            historyEventsDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    historyEventsDatabase.remoteKeysDao().clearRemoteKeys()
                    historyEventsDatabase.historyEventsDao().clearAll()
                }
                val prevKey = if (page == HISTORY_EVENTS_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = historyEvents.map {
                    RemoteKeysEntity(it.id, prevKey, nextKey)
                }
                historyEventsDatabase.remoteKeysDao().insertAll(keys)
                historyEventsDatabase.historyEventsDao().insertAll(historyEvents.map(mapper::map))
            }
            return MediatorResult.Success(endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, HistoryEventEntity>): RemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { historyEvent ->
                historyEventsDatabase.remoteKeysDao().remoteKeysRepoId(historyEvent.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, HistoryEventEntity>): RemoteKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { historyEvent ->
                historyEventsDatabase.remoteKeysDao().remoteKeysRepoId(historyEvent.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, HistoryEventEntity>,
    ): RemoteKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { historyEventId ->
                historyEventsDatabase.remoteKeysDao().remoteKeysRepoId(historyEventId)
            }
        }
    }
}

private const val HISTORY_EVENTS_STARTING_PAGE_INDEX = 1