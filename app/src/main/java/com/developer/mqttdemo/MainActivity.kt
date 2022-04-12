package com.developer.mqttdemo

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import com.developer.mqttdemo.data.source.remote.MqttHelper
import com.developer.mqttdemo.data.source.remote.MqttResult
import com.developer.mqttdemo.databinding.ActivityMainBinding
import com.developer.mqttdemo.utils.NetworkMonitor
import com.developer.mqttdemo.utils.NetworkStatus
import com.google.android.material.snackbar.Snackbar
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mMqttHelper: MqttHelper
    private lateinit var mMessageAdapter: MessageAdapter
    private val networkMonitor: LiveData<NetworkStatus> by lazy {
        NetworkMonitor(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        networkMonitor.observe(this) {
            when (it) {
                is NetworkStatus.Available -> {
                    Log.d("Testing", "network state:- $it")
                    if (!this::mMqttHelper.isInitialized) {
                        Log.d("Testing", "yet to initialize.")
                        mMessageAdapter = MessageAdapter()
                        mMqttHelper = MqttHelper(this,
                            "ssl://" + MqttHelper.HOST + ":" + MqttHelper.PORT,
                            UUID.randomUUID().toString()).also { helper ->
                            observe(helper)
                        }
                        binding.msgList.adapter = mMessageAdapter
                    } else {
                        Log.d("Testing", "already initialized.")
                        mMqttHelper.connect()
                    }
                }
                is NetworkStatus.Unavailable -> {
                    Log.d("Testing", "network state:- $it")
                    showSnackBar()
                }
            }
        }
    }

    private fun observe(helper: MqttHelper) {
        helper.apply {
            connect()
            connectionStatus.observe(this@MainActivity) {
                when (it) {
                    is MqttResult.Success -> {
                        mMqttHelper.subscribe()
                        binding.apply {
                            this.msgList.visibility = View.VISIBLE
                            this.errorText.visibility = View.GONE
                            this.loader.visibility = View.GONE
                        }
                    }
                    is MqttResult.Error -> {
                        binding.apply {
                            this.msgList.visibility = View.GONE
                            this.errorText.visibility = View.VISIBLE
                            this.loader.visibility = View.GONE
                        }
                    }
                    is MqttResult.Loading -> {
                        binding.apply {
                            this.msgList.visibility = View.GONE
                            this.errorText.visibility = View.GONE
                            this.loader.visibility = View.VISIBLE
                        }
                    }
                }
            }

            msgList.observe(this@MainActivity) {
                Log.d("Testing", "${it.size}")
                mMessageAdapter.submitList(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    if (this::mMqttHelper.isInitialized) {
                        mMqttHelper.disconnect()
                    } else {
                        showSnackBar()
                    }
                    true
                }
                else -> {
                    Log.d("Testing", "unknown menu item.")
                    false
                }
            }
        }
    }

    private fun showSnackBar() {
        Snackbar.make(binding.parent,
            "please check the internet connection.",
            Snackbar.LENGTH_LONG).show()
    }

}