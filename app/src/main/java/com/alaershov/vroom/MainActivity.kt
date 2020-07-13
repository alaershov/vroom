package com.alaershov.vroom

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.RemoteException
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.alaershov.vroom.datasource.VehicleDataCallback
import com.alaershov.vroom.datasource.VehicleDataSourceService
import com.alaershov.vroom.datasource.VehicleDataSourceServiceInterface
import com.alaershov.vroom.meter.MeterValueAnimator
import com.alaershov.vroom.meter.MeterView


class MainActivity : AppCompatActivity() {

    private val handler: Handler = Handler(Looper.getMainLooper())

    private lateinit var speedometerView: MeterView
    private lateinit var speedometerValueAnimator: MeterValueAnimator

    private var vehicleService: VehicleDataSourceServiceInterface? = null

    private val callback: VehicleDataCallback = object : VehicleDataCallback.Stub() {

        override fun onDataChanged(speed: Float, rpm: Float) {
            handler.post {
                showVehicleData(speed, rpm)
            }
        }
    }

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            vehicleService = VehicleDataSourceServiceInterface.Stub.asInterface(service).also {
                try {
                    it.registerCallback(callback)
                } catch (e: RemoteException) {
                    // In this case the service has crashed before we could even
                    // do anything with it; we can count on soon being
                    // disconnected (and then reconnected if it can be restarted)
                    // so there is no need to do anything here.
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            vehicleService = null
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUi()
        }
    }

    private fun hideSystemUi() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        speedometerView = findViewById(R.id.view_speedometer)
        speedometerView.configure(
            MeterView.Config(
                valueMin = 0.0,
                valueMax = 220.0,
                minorTickValue = 10.0,
                majorTickValue = 20.0
            )
        )
        speedometerValueAnimator = MeterValueAnimator(speedometerView)
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, VehicleDataSourceService::class.java)
        intent.action = VehicleDataSourceServiceInterface::class.java.name
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        try {
            vehicleService?.unregisterCallback(callback)
        } catch (e: RemoteException) {
            // There is nothing special we need to do if the service
            // has crashed.
        }
        unbindService(serviceConnection)
    }

    private fun showVehicleData(speed: Float, rpm: Float) {
        speedometerValueAnimator.animateValue(speed.toDouble())
    }
}
