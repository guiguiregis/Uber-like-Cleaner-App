package com.wolfpackdigital.kliner.partner.shared.utils.extensions

import androidx.lifecycle.MutableLiveData

operator fun <T> MutableLiveData<List<T>>.plusAssign(item: T) {
    val value = this.value ?: listOf()
    postValue(value + item)
}

operator fun <T> MutableLiveData<List<T>>.minusAssign(item: T) {
    val value = this.value ?: listOf()
    postValue(value - item)
}

fun <T> MutableLiveData<List<T>>.replaceLastItem(item: T) {
    val value = this.value?.toMutableList() ?: mutableListOf()
    value[value.size.dec()] = item
    postValue(value)
}
