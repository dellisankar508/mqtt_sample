package com.developer.mqttdemo.data.source.remote

import com.developer.mqttdemo.data.ErrorData

sealed class MqttResult {
    object Loading: MqttResult()
    object Success: MqttResult()
    data class Error(val data: ErrorData): MqttResult()

}
