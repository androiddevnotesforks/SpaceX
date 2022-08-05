package com.app.spacexapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.spacexapp.model.local.entities.HistoryEventEntity

@Dao
interface HistoryEventsDao {

    @Query("SELECT * FROM history_event_dbo")
    fun getAll(): PagingSource<Int, HistoryEventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rockets: List<HistoryEventEntity>)

    @Query("DELETE FROM history_event_dbo")
    suspend fun clearAll()

    @Query("SELECT * FROM history_event_dbo ORDER BY id ASC LIMIT 1")
    suspend fun getFirst(): HistoryEventEntity
}