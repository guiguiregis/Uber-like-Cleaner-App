package com.wolfpackdigital.kliner.partner.core.main.planning

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.GetMissionsUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.Mission
import com.wolfpackdigital.kliner.partner.shared.useCases.MissionsRequest
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.toCalendarDay
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.toInstant
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService

class PlanningViewModel(
    private val getMissionsUseCase: GetMissionsUseCase
) : BaseViewModel(), PersistenceService {

    // Properties

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val today: CalendarDay
        get() = CalendarDay.today()

    private val missionsFilter = MutableLiveData(today)

    private val _missions = MutableLiveData<List<Mission>>()
    val pastMissionsDates: LiveData<List<CalendarDay>> = _missions.map { m ->
        m.mapNotNull {
            it.date?.toInstant()?.toCalendarDay()
        }.filter {
            it.isBefore(today)
        }.distinct()
    }
    val upcomingMissionsDate: LiveData<List<CalendarDay>> = _missions.map { m ->
        m.filter {
            !it.isRecurrent
        }.mapNotNull {
            it.date?.toInstant()?.toCalendarDay()
        }.filter {
            it == today || it.isAfter(today)
        }.distinct()
    }
    val upcomingRecurrentMissionsDate: LiveData<List<CalendarDay>> = _missions.map { m ->
        m.filter { it.isRecurrent }.mapNotNull {
            it.date?.toInstant()?.toCalendarDay()
        }.filter {
            it == today || it.isAfter(today)
        }.distinct()
    }
    val upcomingMixedMissionsDateMediator: MediatorLiveData<List<CalendarDay>> =
        MediatorLiveData<List<CalendarDay>>()
    val allMissionsDatesMediator: MediatorLiveData<List<CalendarDay>> =
        MediatorLiveData<List<CalendarDay>>()
    val firstDateWithMissionsMediator: MediatorLiveData<CalendarDay> =
        MediatorLiveData<CalendarDay>()
    val upcomingMissionsMediator = missionsFilter.map { filterUpcomingMissions(it) }
    val finishedMissionsMediator = missionsFilter.map { filterFinishedMissions(it) }

    val onMissionSelected: (Mission) -> Unit = onSelected@{
        if (cleanerProfile?.isEmployee() == true && it.confirmedAt.isNullOrEmpty()) {
            openOffers(it.id)
        } else {
            _baseCmd.value = BaseCommand.PerformNavAction(
                PlanningFragmentDirections.actionPlanningFragmentToMissionDetailsFragment(
                    it.id ?: return@onSelected
                )
            )
        }
    }
    private val dateWithEventObserver = Observer<List<CalendarDay>> {
        allMissionsDatesMediator.value = allMissionsDatesMediator.value?.plus(it) ?: it
    }

    private fun filterUpcomingMissions(calendarDay: CalendarDay) = when {
        calendarDay.isAfter(today) -> getMissionsByDay(calendarDay) ?: listOf()
        calendarDay == today -> getMissionsByDay(calendarDay)?.filterNot { it.isFinished || it.isCancelled }
            ?: listOf()
        else -> listOf()
    }

    private fun filterFinishedMissions(calendarDay: CalendarDay) = when {
        calendarDay.isBefore(today) -> getMissionsByDay(calendarDay) ?: listOf()
        calendarDay == today -> getMissionsByDay(calendarDay)?.filter { it.isFinished || it.isCancelled }
            ?: listOf()
        else -> listOf()
    }

    private fun getMissionsByDay(calendarDay: CalendarDay?) =
        _missions.value?.filter { m ->
            m.date?.toInstant()?.toCalendarDay() == calendarDay
        }

    private val firstDateWithEventObserver = Observer<List<CalendarDay>> {
        val missionDate = upcomingMissionsDate.value?.firstOrNull()
        val recurrentMissionDate = upcomingRecurrentMissionsDate.value?.firstOrNull()

        firstDateWithMissionsMediator.value =
            if (recurrentMissionDate?.let { rmd -> missionDate?.isBefore(rmd) } == true)
                missionDate else recurrentMissionDate ?: today
    }
    private val upcomingMixedMissionsObserver = Observer<List<CalendarDay>> {
        upcomingMixedMissionsDateMediator.value =
            upcomingRecurrentMissionsDate.value?.asIterable()?.let {
                upcomingMissionsDate.value?.intersect(it)?.toList()
            }
    }

    init {
        allMissionsDatesMediator.apply {
            addSource(pastMissionsDates, dateWithEventObserver)
            addSource(upcomingMissionsDate, dateWithEventObserver)
        }
        firstDateWithMissionsMediator.apply {
            addSource(upcomingMissionsDate, firstDateWithEventObserver)
            addSource(upcomingRecurrentMissionsDate, firstDateWithEventObserver)
        }
        upcomingMixedMissionsDateMediator.apply {
            addSource(upcomingMissionsDate, upcomingMixedMissionsObserver)
            addSource(upcomingRecurrentMissionsDate, upcomingMixedMissionsObserver)
        }
        loadMissions(today.month, today.year)
    }

    fun loadMissions(month: Int, year: Int) {
        performApiCall {
            when (val result = getMissionsUseCase.executeNow(MissionsRequest(month, year))) {
                is Result.Success -> {
                    _missions.value = result.data
                }
                else -> {
                    _missions.value = listOf()
                }
            }
        }
    }

    fun onFilterSelectedDate(date: CalendarDay) {
        missionsFilter.value = date
    }

// Actions

    fun onSearchServices() {
        openOffers(null)
    }

    private fun openOffers(offerId: Int? = null) {
        _cmd.value = Command.OpenOffers(offerId)
    }

// Command

    sealed class Command {
        data class OpenOffers(val offerId: Int?) : Command()
    }
}
