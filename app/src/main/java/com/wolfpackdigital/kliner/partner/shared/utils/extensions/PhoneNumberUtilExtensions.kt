package com.wolfpackdigital.kliner.partner.shared.utils.extensions

import android.util.Log
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wolfpackdigital.kliner.partner.shared.utils.Constants

fun PhoneNumberUtil.getValidNumber(prefix: String?, number: String?): String? {
    if (prefix == null || number == null) return null

    return try {
        val parsedPhoneNumber = parse(prefix + number, null)
        if (isValidNumber(parsedPhoneNumber))
            format(parsedPhoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164)
        else null
    } catch (npe: NumberParseException) {
        Log.e("PHONENUMBERUTIL", "NumberParseException was thrown:", npe)
        null
    }
}

fun PhoneNumberUtil.getExampleNumber(prefixInt: Int): String? {
    val exampleNumber =
        getExampleNumberForTypeOrDefault(
            getRegionCode(prefixInt),
            defaultRegionCode = Constants.DEFAULT_COUNTRY_CODE
        )
    return format(exampleNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
}

fun PhoneNumberUtil.getRegionCode(prefixInt: Int) =
    getRegionCodeForCountryCode(prefixInt) ?: Constants.DEFAULT_COUNTRY_CODE

private fun PhoneNumberUtil.getExampleNumberForTypeOrDefault(
    regionCode: String,
    type: PhoneNumberUtil.PhoneNumberType = PhoneNumberUtil.PhoneNumberType.MOBILE,
    defaultRegionCode: String
) = getExampleNumberForType(regionCode, type) ?: getExampleNumberForType(defaultRegionCode, type)
