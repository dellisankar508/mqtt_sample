package com.developer.mqttdemo.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.developer.mqttdemo.data.Message

interface IMqttHelper {

    val connectionStatus: LiveData<MqttResult>
        get() = MutableLiveData()

    val msgList: LiveData<List<Message>>
        get() = MutableLiveData()

    fun connect()
    fun disconnect()
    fun subscribe()
}