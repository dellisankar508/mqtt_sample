package com.developer.mqttdemo.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData

class NetworkMonitor(context: Context): LiveData<NetworkStatus>() {

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val request: NetworkRequest = NetworkRequest.Builder().apply {
        addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
    }.build()

    private val networkCallback: ConnectivityManager.NetworkCallback = object: ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(NetworkStatus.Available)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(NetworkStatus.Unavailable)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            postValue(NetworkStatus.Unavailable)
        }
    }

    override fun onActive() {
        super.onActive()
        connectivityManager.requestNetwork(request, networkCallback)
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

}