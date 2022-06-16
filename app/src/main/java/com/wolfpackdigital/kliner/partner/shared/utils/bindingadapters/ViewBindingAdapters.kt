@file:Suppress("TooManyFunctions")

package com.wolfpackdigital.kliner.partner.shared.utils.bindingadapters

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.CircleCropTransformation
import com.wolfpackdigital.kliner.partner.R
import com.wolfpackdigital.kliner.partner.shared.useCases.ActivityInfoType
import com.wolfpackdigital.kliner.partner.shared.useCases.FrequencyType
import com.wolfpackdigital.kliner.partner.shared.useCases.ServiceType
import com.wolfpackdigital.kliner.partner.shared.useCases.WorkType
import com.wolfpackdigital.kliner.partner.shared.utils.Constants
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.formatOrNull
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.toLocalDateOrNull
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.toLocalDateTimeOrNull
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

@BindingAdapter("visibility")
fun View.visibility(visible: Boolean?) {
    this.visibility = if (visible == true) View.VISIBLE else View.GONE
}

@BindingAdapter("hidden")
fun View.hidden(hidden: Boolean?) {
    this.visibility = if (hidden == true) View.INVISIBLE else View.VISIBLE
}

@BindingAdapter("backgroundColor")
fun View.background(resource: Int?) {
    resource ?: return
    background = ColorDrawable(ContextCompat.getColor(context, resource))
}

@BindingAdapter("backgroundRes")
fun View.backgroundRes(res: Int?) {
    res ?: return
    background = ContextCompat.getDrawable(context, res)
}

@BindingAdapter("marginTop")
fun View.marginTop(dimen: Float?) {
    (layoutParams as? ConstraintLayout.LayoutParams)?.let {
        dimen?.toInt()?.let { margin -> it.topMargin = margin }
        this.layoutParams = it
    }
}

@BindingAdapter("src")
fun ImageView.src(@DrawableRes resource: Int?) {
    resource ?: return
    setImageResource(resource)
}

@BindingAdapter("tint")
fun ImageView.tint(@ColorRes resource: Int?) {
    resource ?: return
    setColorFilter(ContextCompat.getColor(context, resource))
}

@BindingAdapter("loadCircleImage", "placeholderDrawable")
fun ImageView.loadCircleImage(url: String?, placeholderDrawable: Drawable?) {
    placeholderDrawable ?: return
    url?.let {
        load(it) {
            placeholder(placeholderDrawable)
            crossfade(true)
            transformations(CircleCropTransformation())
        }
    } ?: load(placeholderDrawable) {
        crossfade(true)
        transformations(CircleCropTransformation())
    }
}

@BindingAdapter("serviceType", "workType")
fun TextView.setMissionTitle(serviceType: ServiceType?, workType: WorkType?) {
    serviceType ?: return
    workType ?: return
    val id = when (serviceType) {
        ServiceType.HOME -> when (workType) {
            WorkType.CLEANING -> R.string.cleaning
            WorkType.IRONING -> R.string.ironing
            WorkType.CLEANING_AND_IRONING -> R.string.cleaning_and_ironing
        }
        ServiceType.OFFICE -> R.string.office_cleaning
        ServiceType.BNB -> R.string.bnb_cleaning
        null -> return
    }
    text = context.getString(id)
}

@BindingAdapter("offerFrequency")
fun TextView.setOfferFrequency(offerFrequency: FrequencyType?) {
    offerFrequency ?: return
    val id = when (offerFrequency) {
        FrequencyType.EACH_WEEK -> R.string.offer_frequency_every_week
        else -> R.string.offer_frequency_every_two_weeks
    }
    text = context.getString(id)
}

@BindingAdapter("textInt")
fun TextView.textInt(textInt: Int?) {
    text = textInt?.toString() ?: ""
}

@BindingAdapter("textColor")
fun TextView.textColor(resource: Int?) {
    resource ?: return
    setTextColor(ContextCompat.getColor(context, resource))
}

@BindingAdapter(
    value = ["drawableStartCompat", "drawableEndCompat", "drawableTopCompat"],
    requireAll = false
)
fun TextView.setDrawableENd(
    drawableStartCompat: Drawable?,
    drawableEndCompat: Drawable?,
    drawableTopCompat: Drawable?
) {
    setCompoundDrawablesWithIntrinsicBounds(
        drawableStartCompat,
        drawableTopCompat,
        drawableEndCompat,
        null
    )
}

@BindingAdapter(value = ["drawableStartCompatById", "drawableEndCompatById"], requireAll = false)
fun TextView.setDrawableEndById(drawableStartCompat: Int?, drawableEndCompat: Int?) {
    val leftDrawable = drawableStartCompat?.let {
        ContextCompat.getDrawable(this.context, it)
    }
    val rightDrawable = drawableEndCompat?.let {
        ContextCompat.getDrawable(this.context, it)
    }
    setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, rightDrawable, null)
}

@BindingAdapter("missionDate")
fun TextView.setMissionDate(date: String?) {
    date ?: return
    text = run {
        val parsed =
            date.toLocalDateTimeOrNull()?.truncatedTo(ChronoUnit.MINUTES)
                ?: return@run Constants.NOT_A_NR
        val day = parsed.toLocalDate()
            ?.formatOrNull(DateTimeFormatter.ofPattern(Constants.DAY_MONTH_SPACE_FORMAT))
        val hour = parsed.toLocalTime().toString()
        context.getString(R.string.mission_date, day, hour)
    }
}

@BindingAdapter(value = ["updatedAt", "description", "isPartnershipContract"])
fun TextView.setUpdatedAt(
    updatedAt: String?,
    description: String?,
    isPartnershipContract: Boolean? = false
) {
    text = if (updatedAt == null) description ?: ""
    else {
        val parsed = updatedAt.toLocalDateOrNull()
        val date = parsed?.formatOrNull(DateTimeFormatter.ISO_LOCAL_DATE) ?: Constants.NOT_A_NR
        if (isPartnershipContract == true)
            context.getString(R.string.partnership_contract_description, date)
        else
            context.getString(R.string.document_updated_at, date)
    }
}

@BindingAdapter(value = ["transactionPeriodFromDate", "transactionPeriodToDate"])
fun TextView.setTransactionPeriodDate(dateFrom: String?, dateTo: String?) {
    dateFrom ?: return
    dateTo ?: return
    text = run {
        val from = dateFrom.toLocalDateTimeOrNull()?.toLocalDate()
            ?.format(DateTimeFormatter.ofPattern(Constants.DAY_MONTH_DOT_FORMAT))
            ?: Constants.NOT_A_NR
        val to =
            dateTo.toLocalDateTimeOrNull()?.toLocalDate()
                ?.format(DateTimeFormatter.ofPattern(Constants.FULL_DATE_SHORT_FORMAT))
                ?: Constants.NOT_A_NR
        context.getString(R.string.transfer_period_date, from, to)
    }
}

@BindingAdapter("onlyDay")
fun TextView.setOnlyDay(date: String?) {
    date ?: return
    text = run {
        val parsed = date.toLocalDateTimeOrNull()
        parsed?.dayOfMonth?.toString() ?: Constants.NOT_A_NR
    }
}

@BindingAdapter("onlyMonth")
fun TextView.setOnlyMonth(date: String?) {
    date ?: return
    val parsed = date.toLocalDateTimeOrNull()
    text = parsed?.month?.getDisplayName(TextStyle.SHORT, Locale.getDefault()) ?: Constants.NOT_A_NR
}

@BindingAdapter("textEuroPrice")
fun TextView.setTextEuroPrice(price: Double?) {
    price ?: return
    text = context.getString(
        R.string.price_euros,
        DecimalFormat(Constants.PRICE_FORMAT).format(price)
    )
}

@BindingAdapter(value = ["numberToText", "valueType"])
fun TextView.setNumberToText(value: Number?, isPrice: ActivityInfoType) {
    value ?: return
    text = when (isPrice) {
        ActivityInfoType.Revenue -> context.getString(
            R.string.price_total_euros,
            DecimalFormat(Constants.PRICE_FORMAT).format(value.toDouble())
        )
        ActivityInfoType.Missions -> value.toInt().toString()
        ActivityInfoType.Hours -> value.toString()
    }
}

@BindingAdapter("valueText")
fun TextView.setValueText(type: ActivityInfoType?) {
    type ?: return
    val res = when (type) {
        ActivityInfoType.Revenue -> R.string.revenue
        ActivityInfoType.Missions -> R.string.missions
        ActivityInfoType.Hours -> R.string.hours
    }
    text = context.getString(res)
}

@BindingAdapter("valueIcon")
fun ImageView.setValueIcon(type: ActivityInfoType?) {
    type ?: return
    val res = when (type) {
        ActivityInfoType.Missions -> R.drawable.ic_missions_done
        ActivityInfoType.Revenue -> R.drawable.ic_money_bag
        ActivityInfoType.Hours -> R.drawable.ic_hours
    }
    setImageDrawable(ContextCompat.getDrawable(context, res))
}

@BindingAdapter(value = ["serviceTypeIconLarge", "isRecurrent"], requireAll = false)
fun ImageView.setMissionIconLarge(serviceType: ServiceType?, isRecurrent: Boolean? = null) {
    serviceType ?: return
    val res = when (serviceType) {
        ServiceType.HOME -> getHomeLargeIcon(isRecurrent)
        ServiceType.OFFICE -> getOfficeLargeIcon(isRecurrent)
        ServiceType.BNB -> getBnbLargeIcon(isRecurrent)
    }
    setImageDrawable(ContextCompat.getDrawable(context, res))
}

private fun getHomeLargeIcon(isRecurrent: Boolean?) =
    if (isRecurrent == true) R.drawable.ic_home_recurring else R.drawable.ic_home_large

private fun getOfficeLargeIcon(isRecurrent: Boolean?) =
    if (isRecurrent == true) R.drawable.ic_office_recurring else R.drawable.ic_office_large

private fun getBnbLargeIcon(isRecurrent: Boolean?) =
    if (isRecurrent == true) R.drawable.ic_bnb_recurring else R.drawable.ic_bnb_large

@BindingAdapter("serviceTypeIcon")
fun ImageView.setMissionIcon(serviceType: ServiceType?) {
    serviceType ?: return
    val res = when (serviceType) {
        ServiceType.HOME -> R.drawable.ic_home
        ServiceType.OFFICE -> R.drawable.ic_office
        ServiceType.BNB -> R.drawable.ic_bnb
    }
    setImageDrawable(ContextCompat.getDrawable(context, res))
}

@BindingAdapter(value = ["hyperlinkStripe", "openUrl"], requireAll = true)
fun TextView.setHyperlinkText(specialText: String?, openUrl: (() -> Unit)?) {
    specialText ?: return

    val clickableSpan: ClickableSpan = object : ClickableSpan() {
        override fun onClick(p: View) {
            openUrl?.invoke()
        }
    }
    text = SpannableString(specialText).apply {
        setSpan(
            UnderlineSpan(),
            0,
            specialText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        setSpan(
            clickableSpan,
            0,
            specialText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    context,
                    R.color.colorBlue
                )
            ),
            0,
            specialText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    movementMethod = LinkMovementMethod.getInstance()
}

@BindingAdapter(value = ["ibanFormat", "visibleCharacters"], requireAll = true)
fun TextView.ibanFormat(iban: String?, visibleCharacters: Int?) {
    iban ?: return
    visibleCharacters ?: return
    text = context.getString(R.string.iban_format, iban.substring(iban.length - visibleCharacters))
}

@BindingAdapter("textRes")
fun TextView.textRes(res: Int?) {
    res ?: return
    text = context.getString(res)
}
