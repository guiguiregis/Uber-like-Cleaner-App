package com.wolfpackdigital.kliner.partner.shared.base

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T : ViewDataBinding> activityBinding(
    @LayoutRes resId: Int
) = ActivityBindingProperty<T>(resId)

class ActivityBindingProperty<out T : ViewDataBinding>(
    @LayoutRes private val resId: Int
) : ReadOnlyProperty<AppCompatActivity, T> {

    private var binding: T? = null

    override operator fun getValue(
        thisRef: AppCompatActivity,
        property: KProperty<*>
    ): T = binding ?: createBinding(thisRef).also { binding = it }

    private fun createBinding(
        activity: AppCompatActivity
    ): T = DataBindingUtil.setContentView(activity, resId)
}
