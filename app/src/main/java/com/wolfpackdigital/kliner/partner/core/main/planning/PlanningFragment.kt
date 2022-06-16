package com.wolfpackdigital.kliner.partner.core.main.planning

import androidx.lifecycle.ViewModelStoreOwner
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener
import com.wolfpackdigital.kliner.partner.PlanningBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.main.MainActivityViewModel
import com.wolfpackdigital.kliner.partner.core.main.dashboard.MissionsAdapter
import com.wolfpackdigital.kliner.partner.shared.base.BaseFragment
import com.wolfpackdigital.kliner.partner.shared.customs.CalendarDisableDayWithNoEventDecorator
import com.wolfpackdigital.kliner.partner.shared.customs.CalendarEventDecorator
import com.wolfpackdigital.kliner.partner.shared.customs.CalendarFutureDayWithNoEventDecorator
import com.wolfpackdigital.kliner.partner.shared.customs.CalendarMixedEventsDecorator
import com.wolfpackdigital.kliner.partner.shared.customs.CalendarPastDayDecorator
import com.wolfpackdigital.kliner.partner.shared.customs.CalendarSelectedDayDecorator
import com.wolfpackdigital.kliner.partner.shared.customs.CalendarTodayDecorator
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getColorCompat
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getDrawableCompat
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlanningFragment : BaseFragment<PlanningBinding, PlanningViewModel>(R.layout.fr_planning) {

    // Properties

    override val viewModel by viewModel<PlanningViewModel>()
    private val mainActivityViewModel
            by sharedViewModel<MainActivityViewModel>(from = { activity as ViewModelStoreOwner })
    private val upcomingMissionsAdapter by lazy { MissionsAdapter(viewModel.onMissionSelected) }
    private val finishedMissionsAdapter by lazy { MissionsAdapter(viewModel.onMissionSelected) }

    private val dateListener =
        OnDateSelectedListener { widget, date, _ ->
            viewModel.onFilterSelectedDate(date)
            widget.selectedDate = null
            calendarSelectedDayDecorator?.date = date
            widget.invalidateDecorators()
        }

    private val monthListener =
        OnMonthChangedListener { _, date ->
            viewModel.loadMissions(
                date?.month ?: CalendarDay.today().month,
                date?.year ?: CalendarDay.today().year
            )
        }
    private val calendarTodayDecorator by lazy {
        requireContext().getDrawableCompat(R.drawable.bg_orange_circle_stroke)?.let {
            CalendarTodayDecorator(
                requireContext().getColorCompat(R.color.colorAccent),
                it
            )
        }
    }
    private val calendarSelectedDayDecorator by lazy {
        requireContext().getDrawableCompat(R.drawable.bg_orange_circle_solid)
            ?.let { decorator ->
                CalendarSelectedDayDecorator(drawable = decorator)
            }
    }
    private val calendarPastEventDecorator by lazy {
        CalendarEventDecorator(requireContext().getColorCompat(R.color.colorOpacityGray))
    }
    private val calendarUpcomingEventDecorator by lazy {
        CalendarEventDecorator(requireContext().getColorCompat(R.color.colorAccent))
    }

    private val calendarUpcomingRecurrentEventDecorator by lazy {
        CalendarEventDecorator(requireContext().getColorCompat(R.color.colorBlue))
    }
    private val calendarUpcomingMixedEventsDecorator by lazy {
        CalendarMixedEventsDecorator(
            requireContext().getColorCompat(R.color.colorAccent),
            requireContext().getColorCompat(R.color.colorBlue)
        )
    }
    private val calendarDisableDayWithNoEventDecorator by lazy {
        CalendarDisableDayWithNoEventDecorator()
    }
    private val calendarPastDayDecorator by lazy {
        CalendarPastDayDecorator(requireContext().getColorCompat(R.color.colorOpacityGray))
    }
    private val calendarFutureDayWithNoEventDecorator by lazy {
        CalendarFutureDayWithNoEventDecorator(requireContext().getColorCompat(R.color.colorGrayOpacity80))
    }

    // Lifecycle

    override fun setupViews() {
        binding?.rvUpcomingMissions?.adapter = upcomingMissionsAdapter
        binding?.rvFinishedMissions?.adapter = finishedMissionsAdapter
        binding?.cvPlanning?.addDecorators(
            calendarPastDayDecorator,
            calendarFutureDayWithNoEventDecorator,
            calendarTodayDecorator,
            calendarSelectedDayDecorator,
            calendarDisableDayWithNoEventDecorator,
            calendarPastEventDecorator,
            calendarUpcomingEventDecorator,
            calendarUpcomingRecurrentEventDecorator,
            calendarUpcomingMixedEventsDecorator
        )
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.loadMissions(
            binding?.cvPlanning?.selectedDate?.month ?: CalendarDay.today().month,
            binding?.cvPlanning?.selectedDate?.year ?: CalendarDay.today().year
        )
        viewModel.upcomingMissionsMediator.observe(
            viewLifecycleOwner,
            upcomingMissionsAdapter::submitList
        )
        viewModel.finishedMissionsMediator.observe(
            viewLifecycleOwner,
            finishedMissionsAdapter::submitList
        )
        viewModel.pastMissionsDates.observe(viewLifecycleOwner) {
            calendarPastEventDecorator.dates = it
            binding?.cvPlanning?.invalidateDecorators()
        }
        viewModel.upcomingMissionsDate.observe(viewLifecycleOwner) {
            calendarUpcomingEventDecorator.dates = it
            binding?.cvPlanning?.invalidateDecorators()
        }
        viewModel.upcomingRecurrentMissionsDate.observe(viewLifecycleOwner) {
            calendarUpcomingRecurrentEventDecorator.dates = it
            binding?.cvPlanning?.invalidateDecorators()
        }
        viewModel.upcomingMixedMissionsDateMediator.observe(viewLifecycleOwner) { dates: List<CalendarDay>? ->
            calendarUpcomingMixedEventsDecorator.dates = dates
            dates?.let {
                calendarUpcomingEventDecorator.dates =
                    calendarUpcomingEventDecorator.dates?.minus(it)
                calendarUpcomingRecurrentEventDecorator.dates =
                    calendarUpcomingRecurrentEventDecorator.dates?.minus(it)
            }
            binding?.cvPlanning?.invalidateDecorators()
        }
        viewModel.allMissionsDatesMediator.observe(viewLifecycleOwner) {
            calendarDisableDayWithNoEventDecorator.dates = it
            calendarFutureDayWithNoEventDecorator.dates = it
            binding?.cvPlanning?.invalidateDecorators()
        }
        viewModel.firstDateWithMissionsMediator.observe(viewLifecycleOwner) {
            calendarSelectedDayDecorator?.date = it
            binding?.cvPlanning?.invalidateDecorators()
            viewModel.onFilterSelectedDate(it)
        }
        viewModel.cmd.observe(viewLifecycleOwner) {
            when (it) {
                is PlanningViewModel.Command.OpenOffers -> {
                    mainActivityViewModel.selectedUnconfirmedMission.value = it.offerId
                    mainActivityViewModel.selectTab(R.id.nav_tab_offers)
                }
            }
        }
        binding?.cvPlanning?.setOnDateChangedListener(dateListener)
        binding?.cvPlanning?.setOnMonthChangedListener(monthListener)
    }
}
