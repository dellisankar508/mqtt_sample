package com.developer.mqttdemo.utils

sealed interface NetworkStatus {
    object Available: NetworkStatus
    object Unavailable: NetworkStatus
}