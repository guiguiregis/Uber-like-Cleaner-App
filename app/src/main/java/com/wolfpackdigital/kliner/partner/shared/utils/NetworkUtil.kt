package com.wolfpackdigital.kliner.partner.shared.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.MutableLiveData

object NetworkUtil {

    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private val isNetworkConnected = MutableLiveData<Boolean?>(null)

    fun startNetworkCallback(context: Context) {
        if (networkCallback != null) return
        Variables.isNetworkConnectedObservable = isNetworkConnected

        val connManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        getCurrentNetworkStatus(connManager)

        val builder: NetworkRequest.Builder = NetworkRequest.Builder()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                updateNetworkStatus(true)
            }

            override fun onLost(network: Network) {
                updateNetworkStatus(false)
            }
        }
        networkCallback?.let {
            connManager?.registerNetworkCallback(builder.build(), it)
        }
    }

    private fun getCurrentNetworkStatus(connManager: ConnectivityManager?) {
        val capabilities = connManager?.getNetworkCapabilities(connManager.activeNetwork)
        val isConnected =
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        updateNetworkStatus(isConnected)
    }

    private fun updateNetworkStatus(isConnected: Boolean) {
        Variables.isNetworkConnected = isConnected
        isNetworkConnected.postValue(isConnected)
    }

    fun stopNetworkCallback(context: Context) {
        val connManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        networkCallback?.let {
            connManager?.unregisterNetworkCallback(it)
        }
    }
}
