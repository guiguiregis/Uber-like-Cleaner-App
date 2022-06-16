package com.wolfpackdigital.kliner.partner.shared.base

import android.app.Application
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.wolfpackdigital.kliner.partner.shared.utils.LiveEvent
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.minusAssign
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.plusAssign
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseViewModel : ViewModel() {
    @Suppress("PropertyName", "VariableNaming")
    protected val _apiCallsCount = MutableLiveData<Int>()
    val apiCallsCount: LiveData<Int>
        get() = _apiCallsCount

    @Suppress("PropertyName", "VariableNaming")
    protected val _baseCmd = LiveEvent<BaseCommand>()
    val baseCmd: LiveData<BaseCommand>
        get() = _baseCmd

    protected fun performApiCall(block: suspend CoroutineScope.() -> Unit) {
        _apiCallsCount += 1
        viewModelScope.launch {
            block()
            _apiCallsCount -= 1
        }
    }

    @Suppress("MagicNumber")
    protected fun checkInternetAvailable(callback: (isInternet: Boolean) -> Unit) {
        performApiCall {
            try {
                val timeoutMs = 1500
                val socket = Socket()
                val socketAddress = InetSocketAddress("8.8.8.8", 53)

                withContext(Dispatchers.IO) {
                    socket.connect(socketAddress, timeoutMs)
                    socket.close()
                }

                callback(true)
            } catch (e: IOException) {
                callback(false)
            }
        }
    }

    fun back() {
        _baseCmd.value = BaseCommand.GoBack
    }
}

abstract class BaseAndroidViewModel(application: Application) : AndroidViewModel(application) {
    @Suppress("PropertyName", "VariableNaming")
    protected val _apiCallsCount = MutableLiveData<Int>()
    val apiCallsCount: LiveData<Int>
        get() = _apiCallsCount

    @Suppress("PropertyName", "VariableNaming")
    protected val _baseCmd = LiveEvent<BaseCommand>()
    val baseCmd: LiveData<BaseCommand>
        get() = _baseCmd

    protected fun performApiCall(block: suspend CoroutineScope.() -> Unit) {
        _apiCallsCount += 1
        viewModelScope.launch {
            block()
            _apiCallsCount -= 1
        }
    }
}

sealed class BaseCommand {
    data class ShowToastById(val stringId: Int) : BaseCommand()
    data class ShowToast(val message: String) : BaseCommand()
    data class ShowSnackbarById(val stringId: Int) : BaseCommand()
    data class ShowSnackbar(val message: String) : BaseCommand()
    data class PerformNavAction(
        val direction: NavDirections,
        val extras: Navigator.Extras? = null
    ) : BaseCommand()

    data class PerformNavById(
        val destinationId: Int,
        val bundle: Bundle = bundleOf(),
        val options: NavOptions? = null,
        val extras: Navigator.Extras? = null
    ) : BaseCommand()

    object GoBack : BaseCommand()
}
