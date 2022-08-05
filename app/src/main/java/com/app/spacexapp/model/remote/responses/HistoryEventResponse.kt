package com.app.spacexapp.model.remote.responses

import com.app.spacexapp.util.ResponseField
import com.google.gson.annotations.SerializedName

data class HistoryEventsResponse(
    @SerializedName("docs")
    val historyEvents: List<HistoryEventResponse>,
    val totalPages: Int,
    val page: Int,
)

data class HistoryEventResponse(
    val id: String,
    val links: Links,
    val title: String,
    @SerializedName(ResponseField.eventDateUnix)
    val date: Long,
    val details: String,
)

data class Links(
    val article: String? = null,
    val reddit: String? = null,
    val wikipedia: String? = null,
)