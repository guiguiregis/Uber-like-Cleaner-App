package com.wolfpackdigital.kliner.partner.shared.useCases

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.formatOrNull
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.toLocalDateTimeOrNull
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Suppress("MagicNumber")
class GetMissionsUseCase(
    private val repo: MainRepoI
) : BaseUseCase<MissionsRequest, List<Mission>>() {
    override suspend fun run(params: MissionsRequest) = try {
        Result.Success(repo.getMissions(params.month, params.year))
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

private const val SECONDS_IN_MINUTE = 60

data class MissionsRequest(
    val month: Int? = null,
    val year: Int? = null
)

@Keep
data class Mission(
    val id: Int? = null,
    val date: String? = null,
    val duration: Int? = null,
    @SerializedName("started_at")
    val startedAt: String? = null,
    @SerializedName("finished_at")
    val finishedAt: String? = null,
    @SerializedName("cancelled_at")
    val cancelledAt: String? = null,
    val frequency: FrequencyType? = null,
    @SerializedName("service_type")
    val serviceType: ServiceType? = null,
    @SerializedName("work_type")
    val workType: WorkType? = null,
    val status: MissionStatus? = null,
    val instructions: List<String>? = null,
    val address: MissionAddress? = null,
    val customer: Customer? = null,
    @SerializedName("customer_name")
    val customerFullName: String? = null,
    val partner: CleanerProfile? = null,
    @SerializedName("cleaner_profile")
    val cleaner: CleanerProfile? = null,
    val price: String? = null,
    val isEquipmentNeeded: Boolean = false,
    val isSpecialOffer: Boolean = false,
    @SerializedName("confirmed_at")
    val confirmedAt: String? = null,
    @SerializedName("kliner_charges")
    val klinerCharges: String? = null,
    @SerializedName("invoice_urls")
    val invoiceUrls: List<String>? = null,
    @Transient
    val onClick: (Mission) -> Unit = {},
    @Transient
    private val onLocationClicked: (Mission) -> Unit = {},
    @Transient
    private val onAcceptClicked: (Mission) -> Unit = {},
    @Transient
    private val onRefuseClicked: (Mission) -> Unit = {}
) {
    fun onSelect() = onClick(this)
    fun onLocationClicked() = onLocationClicked(this)
    fun onAcceptClicked() = onAcceptClicked(this)
    fun onRefuseClicked() = onRefuseClicked(this)

    val durationFormatted: String
        get() = duration?.formatMissionDuration() ?: Constants.NOT_A_NR

    val dateForMissionDetails: String
        get() {
            val parsed = date?.toLocalDateTimeOrNull()?.truncatedTo(ChronoUnit.MINUTES)
                ?: return Constants.NOT_A_NR
            val day =
                parsed.formatOrNull(DateTimeFormatter.ofPattern(Constants.WEEKDAY_MONTH_FORMAT))
            val hour = parsed.toLocalTime().toString()
            return "$day, $hour"
        }

    val detailsFormatted
        get() = customer?.let { "${customer.firstName} ${customer.lastName}\n" }
            ?: "$customerFullName\n" +
            durationFormatted

    val assignedTo
        get() = cleaner?.let { "${it.lastName} ${it.firstName}" } ?: ""

    val isOnAssignment
        get() = status == MissionStatus.ON_ASSIGNMENT

    val isCancelled
        get() = status == MissionStatus.CANCELLED
    val isAssigned
        get() = status == MissionStatus.ASSIGNED
    val isConfirmed
        get() = status == MissionStatus.ASSIGNED && confirmedAt != null
    val isInProgress
        get() = status == MissionStatus.IN_PROGRESS
    val isFinished
        get() = status == MissionStatus.FINISHED ||
                status == MissionStatus.FINISHED_MONTHLY_PAYMENT ||
                status == MissionStatus.FINISHED_PAYMENT_PENDING

    val isRecurrent: Boolean
        get() = frequency?.let { it != FrequencyType.ONE_TIME } ?: false
}

fun Int.formatMissionDuration(): String {
    val seconds = toLong().times(SECONDS_IN_MINUTE)
    val hours = Duration.ofSeconds(seconds).toHours()
    val minutes =
        Duration.ofSeconds(seconds).toMinutes() - Duration.ofHours(hours).toMinutes()
    return if (hours > 0) {
        if (minutes > 0) {
            "${hours}h${minutes}m"
        } else {
            "${hours}h00"
        }
    } else {
        "${minutes}m"
    }
}

@Keep
enum class ServiceType {
    @SerializedName("home")
    HOME,

    @SerializedName("office")
    OFFICE,

    @SerializedName("bnb")
    BNB
}

@Keep
enum class WorkType {
    @SerializedName("cleaning")
    CLEANING,

    @SerializedName("ironing")
    IRONING,

    @SerializedName("cleaning_and_ironing")
    CLEANING_AND_IRONING
}

@Keep
enum class FrequencyType {
    @SerializedName("one_time")
    ONE_TIME,

    @SerializedName("each_week")
    EACH_WEEK,

    @SerializedName("each_two_weeks")
    EACH_TWO_WEEKS,

    @SerializedName("each_month")
    EACH_MONTH
}

@Keep
enum class MissionStatus {
    @SerializedName("pending")
    PENDING,

    @SerializedName("on_assignment")
    ON_ASSIGNMENT,

    @SerializedName("assigned")
    ASSIGNED,

    @SerializedName("late")
    LATE,

    @SerializedName("in_progress")
    IN_PROGRESS,

    @SerializedName("finished")
    FINISHED,

    @SerializedName("payment_pending")
    FINISHED_PAYMENT_PENDING,

    @SerializedName("monthly_payment")
    FINISHED_MONTHLY_PAYMENT,

    @SerializedName("cancelled")
    CANCELLED,

    @SerializedName("abandoned")
    ABANDONED
}

@Keep
data class Customer(
    val id: Int? = null,
    val email: String? = null,
    @SerializedName("first_name")
    val firstName: String? = null,
    @SerializedName("last_name")
    val lastName: String? = null,
    @SerializedName("photo_url")
    val photoUrl: String? = null
)

@Keep
data class MissionAddress(
    val id: Int? = null,
    val country: String? = null,
    val city: String? = null,
    @SerializedName("street_line_one")
    val streetLineOne: String? = null,
    @SerializedName("street_line_two")
    val streetLineTwo: String? = null,
    val floor: String? = null,
    val door: String? = null,
    val default: Boolean = false,
    val latitude: String? = null,
    val longitude: String? = null,
    @SerializedName("is_business_address")
    val isBusinessAddress: Boolean = false
)
