package com.mohsenoid.hfpbreaker

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var audioManager: AudioManager

    private val audioChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1)) {
                AudioManager.SCO_AUDIO_STATE_CONNECTING -> {
                    log("I: SCO_AUDIO_STATE_CONNECTING")
                }

                AudioManager.SCO_AUDIO_STATE_CONNECTED -> {
                    log("I: SCO_AUDIO_STATE_CONNECTED")
                }

                AudioManager.SCO_AUDIO_STATE_DISCONNECTED -> {
                    log("I: SCO_AUDIO_STATE_DISCONNECTED")
                }

                AudioManager.SCO_AUDIO_STATE_ERROR -> {
                    log("W: SCO_AUDIO_STATE_ERROR")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        doPermissions()

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val intentFilter = IntentFilter()
        with(intentFilter) {
            addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED)
            priority = Int.MAX_VALUE
        }

        registerReceiver(audioChangeReceiver, intentFilter)

        // set scrolling method for log TextView
        txtLog.movementMethod = ScrollingMovementMethod()
        log("D: Start app")

        btOpenSco.setOnClickListener { startBluetoothSco() }
        btCloseSco.setOnClickListener { stopBluetoothSco() }
    }

    private fun doPermissions(): Boolean {
        val requiredPermissions =
                arrayOf(
                        Manifest.permission.MODIFY_AUDIO_SETTINGS
                )

        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_ALL_PERMISSIONS)
                return false
            }
        }

        return true
    }

    private fun startBluetoothSco() {
        log("D: open SCO")
        if (!audioManager.isBluetoothScoOn) {
            log("D: opening SCO...")
            audioManager.mode = AudioManager.MODE_NORMAL
            audioManager.startBluetoothSco()
            audioManager.isBluetoothScoOn = true
            audioManager.isSpeakerphoneOn = false
        } else {
            log("D: SCO is already open!")
        }
    }

    private fun stopBluetoothSco() {
        log("D: close SCO")
        if (audioManager.isBluetoothScoOn) {
            log("D: closing SCO")
            audioManager.mode = AudioManager.MODE_NORMAL
            audioManager.stopBluetoothSco()
            audioManager.isBluetoothScoOn = false
            audioManager.isSpeakerphoneOn = false
        } else {
            log("D: SCO is already closed!")
        }
    }

    private fun log(messagen: String) {
        txtLog.text = "$messagen\n${txtLog.text}"
    }

    companion object {
        private const val REQUEST_ALL_PERMISSIONS = 1001
    }
}
