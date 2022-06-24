package io.moxd.rpc_client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.moxd.IRemoteService
import io.moxd.rpc_client.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var remoteService: IRemoteService? = null

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            remoteService = IRemoteService.Stub.asInterface(service)
            toast("service connected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            remoteService = null
            toast("service disconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.runSingleThreadedBtn.setOnClickListener {
            runBlocking { // single threaded: 100_000
                repeat(100_000) {
                    launch {
                        remoteService?.incrementCounter()
                    }
                }
            }
        }

        binding.runMultithreadedBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Default) { // race condition :(
                repeat(100_000) {
                    launch {
                        remoteService?.incrementCounter()
                    }
                }
            }
        }

        binding.resetCounterBtn.setOnClickListener {
            remoteService?.resetCounter()
        }

        binding.refreshCounterBtn.setOnClickListener {
            updateCounterView()
        }

    }

    private fun updateCounterView() {
        binding.counterTv.text = "Counter: ${remoteService?.count ?: "unknown"}"
    }

    override fun onStart() {
        super.onStart()

        val intent = Intent("io.moxd.remote_service") // name from intent filter of service
        val packageName = "io.moxd.rpc_service"
        val className = "$packageName.RemoteService"
        intent.setClassName(packageName, className)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
        remoteService = null
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}