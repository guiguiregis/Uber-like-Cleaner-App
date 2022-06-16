@file:Suppress("TooManyFunctions")

package com.wolfpackdigital.kliner.partner.shared.utils.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.LiveData
import com.wolfpackdigital.kliner.partner.BaseEditTextBinding
import com.wolfpackdigital.kliner.partner.shared.utils.bindingadapters.visibility
import java.util.Locale

class BaseEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val binding =
        BaseEditTextBinding.inflate(LayoutInflater.from(context), this, true)

    var text: String?
        get() = binding.etContent.text?.toString() ?: ""
        set(value) {
            binding.etContent.setText(value)
        }

    fun focus() {
        binding.etContent.requestFocus()
    }
}

@BindingAdapter("klText")
fun BaseEditText.setKlText(content: LiveData<String>?) {
    if (text != content?.value) {
        text = content?.value ?: ""
    }
}

@InverseBindingAdapter(attribute = "klText")
fun BaseEditText.getKlText() = text

@BindingAdapter("klTextAttrChanged")
fun BaseEditText.setListener(attrChange: InverseBindingListener) {
    binding.etContent.doOnTextChanged { _, _, _, _ ->
        attrChange.onChange()
    }
}

@BindingAdapter("klTextCapitalize")
fun BaseEditText.setKlTextCapitalize(content: LiveData<String>?) {
    setKlText(content)
}

@InverseBindingAdapter(attribute = "klTextCapitalize")
fun BaseEditText.getKlTextCapitalize() = text

@Suppress("DefaultLocale")
@BindingAdapter("klTextCapitalizeAttrChanged")
fun BaseEditText.setCapitalizeListener(attrChange: InverseBindingListener) {
    binding.etContent.doOnTextChanged { text, _, _, _ ->
        attrChange.onChange()
        val stringText = text.toString()
        val stringTextCapitalized = stringText.capitalize(Locale.getDefault())
        if (stringText != stringTextCapitalized) {
            val cursorPosition = binding.etContent.selectionStart
            binding.etContent.setText(stringTextCapitalized)
            binding.etContent.setSelection(cursorPosition)
        }
    }
}

@BindingAdapter("klString")
fun BaseEditText.setKlString(content: LiveData<String>?) {
    text = content?.value ?: ""
    binding.etContent.requestFocus()
}

@BindingAdapter("inputType")
fun BaseEditText.inputType(type: Int?) {
    binding.etContent.inputType = type ?: InputType.TYPE_CLASS_TEXT
}

@BindingAdapter("klEditTextHintLD")
fun BaseEditText.klEditTextHintLD(content: LiveData<String>) {
    binding.etContent.hint = content.value ?: ""
}

@BindingAdapter("klEditTextHint")
fun BaseEditText.klEditTextHint(res: String?) {
    res ?: return
    binding.etContent.hint = res
}

@BindingAdapter("maxChars")
fun BaseEditText.maxChars(max: Int?) {
    max ?: return
    binding.etContent.apply {
        filters = filters.toMutableList().also { it.add(InputFilter.LengthFilter(max)) }
            .toTypedArray()
    }
}

@BindingAdapter("klAllCaps")
fun BaseEditText.setAllCaps(isAllCaps: Boolean?) {
    isAllCaps ?: return
    binding.etContent.apply {
        filters = filters.toMutableList().also { it.add(InputFilter.AllCaps()) }.toTypedArray()
    }
}

@BindingAdapter("textSize")
fun BaseEditText.textSize(size: Float?) {
    size ?: return
    binding.etContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
}

@BindingAdapter("textGravity")
fun BaseEditText.textGravity(gravity: Int?) {
    binding.etContent.gravity = gravity ?: Gravity.START
}

@BindingAdapter("imeOptions")
fun BaseEditText.imeOptions(options: Int?) {
    binding.etContent.imeOptions = options ?: EditorInfo.IME_ACTION_DONE
}

@BindingAdapter("klError")
fun BaseEditText.klError(klError: Int?) {
    val error = klError?.let(context::getString) ?: ""
    binding.tvError.text = error
    binding.tvError.visibility(error.isNotEmpty())
}

@BindingAdapter(value = ["klDrawableStart", "klDrawableEnd"], requireAll = false)
fun BaseEditText.klDrawable(start: Drawable?, end: Drawable?) {
    start?.let {
        binding.ivDrawableStart.visibility = View.VISIBLE
        binding.ivDrawableStart.setImageDrawable(it)
    }
    end?.let {
        binding.ivDrawableEnd.visibility = View.VISIBLE
        binding.ivDrawableEnd.setImageDrawable(it)
    }
}

@BindingAdapter("klDrawableStartClick")
fun BaseEditText.klDrawableStartClick(callback: () -> Unit?) {
    binding.ivDrawableStart.setOnClickListener {
        callback()
    }
}

@BindingAdapter("klDrawableEndClick")
fun BaseEditText.klDrawableEndClick(callback: () -> Unit?) {
    binding.ivDrawableEnd.setOnClickListener {
        callback()
    }
}

@BindingAdapter("klWidthMatchParent")
fun BaseEditText.klWidthMatchParent(isMatchParent: Boolean?) {
    isMatchParent?.let {
        val layoutParams = binding.etContent.layoutParams
        layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        binding.etContent.layoutParams = layoutParams
    }
}

@BindingAdapter("klFocusable")
fun BaseEditText.klFocusable(isFocusable: Boolean?) {
    binding.etContent.isFocusableInTouchMode = isFocusable ?: true
}

@BindingAdapter("klClick")
fun BaseEditText.klClick(callback: () -> Unit?) {
    binding.etClickView.setOnClickListener {
        callback()
    }
}

@BindingAdapter("klEnabled")
fun BaseEditText.klEnabled(enabled: Boolean?) {
    binding.etContent.isEnabled = enabled ?: true
    binding.etClickView.isEnabled = enabled ?: true
}

@BindingAdapter("klTextColor")
fun BaseEditText.klTextColor(textColor: Int?) {
    textColor?.let {
        binding.etContent.setTextColor(it)
    }
}
