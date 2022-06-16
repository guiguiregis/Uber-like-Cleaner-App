package com.wolfpackdigital.kliner.partner.core.main.offers

import android.graphics.Typeface.BOLD
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.wolfpackdigital.kliner.partner.OfferBinding
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.base.BaseAdapter
import com.wolfpackdigital.kliner.partner.shared.useCases.Mission
import com.wolfpackdigital.kliner.partner.shared.useCases.WeekDayItem
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.bindingadapters.visibility
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.formatOrNull
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getWeekday
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.toLocalDateTimeOrNull
import com.wolfpackdigital.kliner.partner.shared.utils.persistence.PersistenceService
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

class OffersAdapter : BaseAdapter<Mission, OfferBinding>(
    R.layout.item_offer, object : DiffUtil.ItemCallback<Mission>() {
        override fun areItemsTheSame(oldItem: Mission, newItem: Mission) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Mission, newItem: Mission) =
            oldItem == newItem
    }
), PersistenceService {
    @SuppressWarnings("MagicNumber")
    override fun bind(binding: OfferBinding, item: Mission) {
        val days: MutableList<WeekDayItem> = mutableListOf()
        val itemDate = item.date?.toLocalDateTimeOrNull()?.truncatedTo(ChronoUnit.MINUTES) ?: return
        val day = itemDate.formatOrNull(
            DateTimeFormatter.ofPattern(Constants.WEEKDAY_MONTH_FORMAT)
        ) ?: Constants.NOT_A_NR
        val hour = itemDate.toLocalTime().toString()
        DayOfWeek.values().forEach {
            val dayFormatted = itemDate.getWeekday().toUpperCase(Locale.getDefault())
            val displayName = it.getDisplayName(TextStyle.FULL, Locale.getDefault())
                .toUpperCase(Locale.getDefault())
            days.add(
                WeekDayItem(
                    displayName,
                    hour,
                    displayName[0].toString(),
                    displayName.substring(0, 3),
                    dayFormatted == displayName
                )
            )
        }

        binding.rvWeekdays.apply {
            layoutManager = GridLayoutManager(context, days.size.inc()).apply {
                spanSizeLookup = object : SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (days[position].isSelected) {
                            true -> 2
                            else -> 1
                        }
                    }
                }
            }
            adapter = WeekDayAdapter().apply { submitList(days) }
        }

        val offerLocation =
            binding.offerRoot.context.getString(
                R.string.offer_location,
                item.address?.city ?: ""
            )
        binding.tvAppointmentPlace.text = SpannableString(offerLocation).apply {
            setSpan(
                StyleSpan(BOLD), offerLocation.indexOf(':'),
                offerLocation.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        val offerDate = binding.offerRoot.context.getString(R.string.offer_meeting_date, day, hour)
        binding.tvAppointmentDate.text = SpannableString(offerDate).apply {
            setSpan(
                StyleSpan(BOLD), offerDate.indexOf(':'),
                offerDate.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.groupPrice.visibility(cleanerProfile?.isEmployee() != true)
    }
}
