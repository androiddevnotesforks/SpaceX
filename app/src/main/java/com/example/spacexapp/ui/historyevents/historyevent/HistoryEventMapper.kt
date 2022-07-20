package com.example.spacexapp.ui.historyevents.historyevent

import com.example.spacexapp.data.historyEvents.HistoryEventResponse
import com.example.spacexapp.util.Mapper

class HistoryEventMapper : Mapper<HistoryEventResponse, HistoryEvent> {
    override fun map(input: HistoryEventResponse) = HistoryEvent(
        link = input.links.article,
        title = input.title,
        date = input.date,
        details = input.details,
    )
}