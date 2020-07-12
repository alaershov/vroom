package com.alaershov.vroom

import android.animation.ValueAnimator
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.alaershov.vroom.datasource.SpeedDataSource
import com.alaershov.vroom.datasource.VehicleDataCallback
import com.alaershov.vroom.datasource.VehicleDataSourceService
import com.alaershov.vroom.datasource.VehicleDataSourceServiceInterface


class MainActivity : AppCompatActivity() {

    private val handler: Handler = Handler(Looper.getMainLooper())

    private val valueMin = 0.0
    private val valueMax = 220.0

    private val speedDataSource = SpeedDataSource(
        valueMin,
        valueMax
    )

    private var valueAnimator: ValueAnimator? = null

    private lateinit var speedometerView: MeterView

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        speedometerView = findViewById(R.id.view_speedometer)
        speedometerView.setup(
            valueMin = valueMin,
            valueMax = valueMax,
            minorTickValue = 10.0,
            majorTickValue = 20.0
        )
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
        val previousValue = speedometerView.value

        animateSpeed(previousValue, speed.toDouble())
    }

    private fun animateSpeed(previousValue: Double, newValue: Double) {
        val valueDifference: Double = newValue - previousValue

        valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 100
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                val animatedFloat = valueAnimator.animatedValue as Float
                speedometerView.value = previousValue + (animatedFloat * valueDifference)
            }
            start()
        }
    }
}
