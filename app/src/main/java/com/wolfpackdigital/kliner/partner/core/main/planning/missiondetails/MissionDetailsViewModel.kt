package com.wolfpackdigital.kliner.partner.core.main.planning.missiondetails

import android.content.Intent
import android.net.Uri
import android.text.format.DateUtils
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.core.main.planning.missiondetails.dialogs.StartMissionBottomSheetDialog
import com.wolfpackdigital.kliner.partner.shared.base.BaseCommand
import com.wolfpackdigital.kliner.partner.shared.base.BaseViewModel
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.useCases.CallCustomerUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.CancelMissionUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.FinishMissionUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.GetMissionByIdUseCase
import com.wolfpackdigital.kliner.partner.shared.useCases.Mission
import com.wolfpackdigital.kliner.partner.shared.useCases.MissionStatus
import com.wolfpackdigital.kliner.partner.shared.useCases.StartMissionUseCase
import com.wolfpackdigital.kliner.partner.shared.utils.IntentProvider
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.toLocalDateTimeOrNull
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TICKER_PERIOD = 1000L
private const val DELTA_MISSION_START = 30L
private const val FINISHED_MISSION_INVOICE_DELAY = 2000L

@Suppress("TooManyFunctions")
class MissionDetailsViewModel(
    private val missionID: Int,
    private val getMissionByIdUseCase: GetMissionByIdUseCase,
    private val startMissionUseCase: StartMissionUseCase,
    private val finishMissionUseCase: FinishMissionUseCase,
    private val cancelMissionUseCase: CancelMissionUseCase,
    private val callCustomerUseCase: CallCustomerUseCase
) : BaseViewModel(), PersistenceService {

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    private val _mission = MutableLiveData<Mission>()
    val mission: LiveData<Mission> = _mission
    val isClientInstructions = _mission.map { !it.instructions.isNullOrEmpty() }

    val missionInProgress = _mission.map {
        it.status == MissionStatus.IN_PROGRESS
    }

    val gradientRes = _mission.map { mission ->
        when {
            mission?.isFinished == true -> R.drawable.gradient_green_white
            mission?.isCancelled == true -> R.drawable.gradient_red_white
            else -> R.drawable.gradient_orange_white
        }
    }

    val titleRes = _mission.map { mission ->
        when {
            mission?.isFinished == true -> R.string.mission_finished
            mission?.isCancelled == true -> R.string.mission_cancelled
            else -> R.string.mission
        }
    }

    val bottomPartVisible = _mission.map { mission ->
        when {
            mission?.isFinished == true || mission?.isCancelled == true -> false
            else -> true
        }
    }

    val actionButtonsEnabled = _mission.map {
        when (it?.status) {
            MissionStatus.CANCELLED -> false
            else -> true
        }
    }

    val isEmployerAccount = cleanerProfile?.isEmployer() == true
    val isEmployeeAccount = cleanerProfile?.isEmployee() == true

    val invoicesVisible = _mission.map {
        (_mission.value?.cleaner?.isIndependent() == true || isEmployerAccount) && it.isFinished
    }

    private val minutesUntilMissionStart = MutableLiveData<Duration>()
    private val missionStartStatus = minutesUntilMissionStart.map {
        val delta = it.toMinutes()
        when {
            delta < -DELTA_MISSION_START ->
                MissionStartStatus.TOO_LATE
            delta >= DELTA_MISSION_START ->
                MissionStartStatus.TOO_EARLY
            else ->
                MissionStartStatus.CAN_START
        }
    }

    val startButtonEnabledMediator = MediatorLiveData<Boolean>()
    private val startButtonObserver = Observer<Any> {
        startButtonEnabledMediator.value =
            missionStartStatus.value == MissionStartStatus.CAN_START ||
                    _mission.value?.status == MissionStatus.IN_PROGRESS
    }
    val cancelButtonEnabled = _mission.map {
        it.status !in listOf(MissionStatus.IN_PROGRESS, MissionStatus.CANCELLED)
    }

    private val missionStartTime = _mission.map {
        it.startedAt?.toLocalDateTimeOrNull()?.truncatedTo(ChronoUnit.MINUTES)
    }
    val missionStartHour = missionStartTime.map {
        it?.toLocalTime()?.truncatedTo(ChronoUnit.MINUTES)
    }

    private val minutesLeftForMission = MutableLiveData<Duration>()
    val minutesLeftForMissionFormatted = minutesLeftForMission.map {
        val sign = if (it.seconds < 0) "-" else ""
        sign + DateUtils.formatElapsedTime(it.seconds.absoluteValue)
    }

    private val ticker = ticker(TICKER_PERIOD, 0L)

    init {
        fetchMissionDetails()
        viewModelScope.launch {
            for (event in ticker) {
                computeMissionTimeLeft()
                minutesUntilMissionShouldStart()
            }
        }
        startButtonEnabledMediator.addSource(missionStartStatus, startButtonObserver)
        startButtonEnabledMediator.addSource(mission, startButtonObserver)
    }

    private fun computeMissionTimeLeft() {
        val duration = TimeUnit.MINUTES.toMillis(_mission.value?.duration?.toLong() ?: return)

        val currentTime = LocalDateTime.now()
        val startTime = missionStartTime.value ?: return

        // MILLIS left until mission ends
        val left = duration - (startTime.until(currentTime, ChronoUnit.MILLIS))

        minutesLeftForMission.value = Duration.ofMillis(left)
    }

    private fun minutesUntilMissionShouldStart() {
        val currentTime = LocalDateTime.now()
        val missionStart = mission.value?.date?.toLocalDateTimeOrNull() ?: return
        minutesUntilMissionStart.value =
            Duration.ofMillis(currentTime.until(missionStart, ChronoUnit.MILLIS))
    }

    // Actions

    fun fetchMissionDetails(delay: Long? = null) {
        performApiCall {
            delay?.let { delay(it) }
            when (val response = getMissionByIdUseCase.executeNow(missionID)) {
                is Result.Success -> _mission.value = response.data
                is Result.Error -> {
                    _baseCmd.value = BaseCommand.ShowSnackbar(response.error)
                    _baseCmd.value = BaseCommand.GoBack
                }
            }
        }
    }

    fun onChangeCleaner() {
        if (!isCleanerEditable()) return
        val isRecurrenceChange =
            mission.value?.isRecurrent == true && mission.value?.cleaner != null
        _baseCmd.value = BaseCommand.PerformNavAction(
            MissionDetailsFragmentDirections.actionMissionDetailsFragmentToAssignEmployeeBottomSheetDialog(
                missionID, mission.value?.isRecurrent == true, isRecurrenceChange
            )
        )
    }

    fun isCleanerEditable() = when {
        !isEmployerAccount -> false
        _mission.value?.isFinished == true -> false
        _mission.value?.isInProgress == true -> false
        else -> true
    }

    fun onStartButtonClick() {
        when (_mission.value?.status) {
            MissionStatus.IN_PROGRESS -> {
                _cmd.value = Command.ShowEndMissionDialog
            }
            else -> checkMissionStartStatus()
        }
    }

    private fun checkMissionStartStatus() {
        when (missionStartStatus.value) {
            MissionStartStatus.TOO_LATE -> {
                _baseCmd.value = BaseCommand.ShowToast("TOO LATE")
            }
            MissionStartStatus.TOO_EARLY -> {
                _cmd.value = Command.ShowTooEarlyDialog
            }
            MissionStartStatus.CAN_START -> {
                _cmd.value = Command.ShowStartMissionDialog
            }
        }
    }

    fun onStartMission(type: StartMissionBottomSheetDialog.DialogType) {
        performApiCall {
            _cmd.value = Command.CloseStartDialog
            if (type == StartMissionBottomSheetDialog.DialogType.TOO_EARLY)
                return@performApiCall

            when (val result = startMissionUseCase.executeNow(missionID)) {
                is Result.Success -> {
                    _mission.value = result.data
                }
                is Result.Error -> _baseCmd.value = BaseCommand.ShowToast(result.error)
            }
        }
    }

    fun finishMission() {
        performApiCall {
            when (val result = finishMissionUseCase.executeNow(missionID)) {
                is Result.Success -> {
                    _cmd.value = Command.CloseFinishDialog
                    _mission.value = result.data
                    fetchMissionDetails(FINISHED_MISSION_INVOICE_DELAY)
                }
                is Result.Error -> _baseCmd.value = BaseCommand.ShowToast(result.error)
            }
        }
    }

    fun onShowCancelMissionDialog() {
        _cmd.value = Command.ShowCancelMissionDialog
    }

    fun onCancelMission() {
        performApiCall {
            when (val result = cancelMissionUseCase.executeNow(missionID)) {
                is Result.Success -> {
                    onCloseCancelDialog()
                    _mission.value = result.data
                }
                is Result.Error -> _baseCmd.value = BaseCommand.ShowToast(result.error)
            }
        }
    }

    fun onCloseStartDialog() {
        _cmd.value = Command.CloseStartDialog
    }

    fun onCloseFinishDialog() {
        _cmd.value = Command.CloseFinishDialog
    }

    fun onCloseCancelDialog() {
        _cmd.value = Command.CloseCancelDialog
    }

    fun onCloseTwillioInfoDialog() {
        _cmd.value = Command.CloseTwillioInfoDialog
    }

    fun openMaps() {
        mission.value?.address?.let { address ->
            _cmd.value = Command.LaunchIntent(
                IntentProvider.getMapsIntent(address.latitude, address.longitude)
            )
        } ?: run {
            _baseCmd.value = BaseCommand.ShowSnackbarById(R.string.error_no_customer_address)
        }
    }

    fun onCallClient() {
        _cmd.value = Command.ShowTwillioInfoDialog
    }

    fun callClient() {
        onCloseTwillioInfoDialog()
        performApiCall {
            when (val result = callCustomerUseCase.executeNow(missionID)) {
                is Result.Success -> {
                }
                is Result.Error -> _baseCmd.value = BaseCommand.ShowToast(result.error)
            }
        }
    }

    fun onClientInstructions() {
        if (isClientInstructions.value == false) return
        mission.value?.instructions?.let {
            _baseCmd.value = BaseCommand.PerformNavAction(
                MissionDetailsFragmentDirections.actionMissionDetailsFragmentToClientInstructionsFragment(
                    it.toTypedArray()
                )
            )
        }
    }

    fun onDownloadRevenueInvoice() {
        mission.value?.invoiceUrls?.firstOrNull()?.toUri()?.let {
            _cmd.value = Command.DownloadFile(it)
        }
    }

    fun onOpenCharges() {
        _baseCmd.value = BaseCommand.PerformNavAction(
            MissionDetailsFragmentDirections.actionMissionDetailsFragmentToActivityFragment()
        )
    }

    override fun onCleared() {
        super.onCleared()
        ticker.cancel()
    }

    sealed class Command {
        data class LaunchIntent(val intent: Intent) : Command()
        object CloseStartDialog : Command()
        object CloseFinishDialog : Command()
        object CloseCancelDialog : Command()
        object CloseTwillioInfoDialog : Command()
        object ShowTooEarlyDialog : Command()
        object ShowStartMissionDialog : Command()
        object ShowEndMissionDialog : Command()
        object ShowCancelMissionDialog : Command()
        object ShowTwillioInfoDialog : Command()
        data class DownloadFile(val uri: Uri) : Command()
    }

    enum class MissionStartStatus {
        TOO_EARLY,
        CAN_START,
        TOO_LATE
    }
}
