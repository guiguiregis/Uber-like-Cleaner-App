package com.wolfpackdigital.kliner.partner.shared.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.properties.Delegates

object Variables {
    var isNetworkConnected: Boolean by Delegates.observable(false) { _, _, _ -> }

    var isNetworkConnectedObservable: LiveData<Boolean?> by Delegates.observable(
        MutableLiveData(null)
    ) { _, _, _ -> }
}
