package com.example.spacexapp.ui.screens.maintabs.crew

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.spacexapp.data.repository.CrewMembersRepository
import com.example.spacexapp.ui.screens.maintabs.crew.member.CrewMember
import com.example.spacexapp.model.local.mappers.CrewMemberEntityMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CrewMembersViewModel(
    crewMembersRepository: CrewMembersRepository,
    private val mapper: CrewMemberEntityMapper,
) : ViewModel() {

    val crewMembers: Flow<PagingData<CrewMember>> =
        crewMembersRepository.getCrewMembersStream()
            .map { pagingData -> pagingData.map(mapper::map) }.cachedIn(viewModelScope)
}