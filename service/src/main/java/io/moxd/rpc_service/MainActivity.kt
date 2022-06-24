package io.moxd.rpc_service

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.moxd.rpc_service.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val app: App by lazy {
        this.application as App
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        updateUi()

        binding.toggleButton.setOnClickListener {
            app.isRunning = !app.isRunning
            if (app.isRunning) start() else stop()
            updateUi()
        }
    }

    private fun updateUi() {
        val (buttonText, textViewText) = if (app.isRunning) {
            "Stop" to "Service is running."
        } else
            "Start" to "Service not running."

        binding.toggleButton.text = buttonText
        binding.serviceTv.text = textViewText

    }

    private fun start() {
        val intent = Intent(this, RemoteService::class.java)
        this.applicationContext.startForegroundService(intent)
    }

    private fun stop() {
        val intent = Intent(this, RemoteService::class.java)
        this.applicationContext.stopService(intent)
    }
}