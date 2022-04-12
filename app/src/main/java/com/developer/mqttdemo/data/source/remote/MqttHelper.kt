package com.developer.mqttdemo.data.source.remote

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.developer.mqttdemo.data.ErrorData
import com.developer.mqttdemo.data.Message
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MqttHelper(
    private val context: Context,
    private val serverURI: String,
    private val clientID: String
) : IMqttHelper {

    private val mClient: MqttAndroidClient by lazy {
        MqttAndroidClient(context, serverURI, clientID).also {
            it.setCallback(mMqttCallback)
        }
    }
    private val _connectionStatus: MutableLiveData<MqttResult> = MutableLiveData()
    override val connectionStatus: LiveData<MqttResult>
        get() = _connectionStatus

    private val _msgMutableList: MutableList<String> = mutableListOf()
    private val _msgList: MutableLiveData<List<Message>> = MutableLiveData()
    override val msgList: LiveData<List<Message>>
        get() = _msgList

    private val mMqttCallback: MqttCallback = object: MqttCallback {
        override fun connectionLost(cause: Throwable?) {
            Log.d("Testing", "connection lost.")
        }

        override fun messageArrived(topic: String?, message: MqttMessage?) {
            Log.d("Testing", "Message: ${message?.toString()} arrived from topic: $topic,")
            _msgMutableList.add(message.toString())
            _msgList.postValue(MutableList(_msgMutableList.size) { idx ->
                Message(idx, topic!!, _msgMutableList[idx])
            })
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {
            Log.d("Testing", "message delivery.")
        }
    }

    private val mMqttActionListener: IMqttActionListener = object: IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            Log.d("Testing", "successfully connected to mqtt.")
            _connectionStatus.postValue(MqttResult.Success)
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            Log.d("Testing", "unable to connect to mqtt. $exception")
            _connectionStatus.postValue(MqttResult.Error(
                data = ErrorData(exception?.message!!, exception.cause!!)
            ))
        }
    }

    private val mMqttDisconnectListener: IMqttActionListener = object: IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            Log.d("Testing", "successfully disconnected from mqtt.")
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            Log.d("Testing", "Unable to disconnect from mqtt. $exception")
        }
    }

    override fun connect() {
        try {
            val options: MqttConnectOptions = MqttConnectOptions().apply {
                userName = USER_NAME
                password = PASSWORD.toCharArray()
            }
            mClient.connect(options, null, mMqttActionListener)
        } catch (exception: Exception) {
            Log.d("Testing", "exception while connecting to mqtt. $exception")
        }
    }

    override fun disconnect() {
        if (mClient.isConnected) {
            mClient.disconnect(0L, null, mMqttDisconnectListener)
        }
    }

    override fun subscribe() {
        mClient.subscribe(TOPIC, QOS)
    }

    companion object {
        const val USER_NAME = "DS25689"
        const val PASSWORD = "Ds@Hive1"
        const val PORT = "8883"
        const val HOST = "933b3989c05a4ad381cd36ec481d70f8.s2.eu.hivemq.cloud"
        const val QOS = 2
        const val TOPIC = "testtopic/1"
    }
}