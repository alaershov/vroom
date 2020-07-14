package com.alaershov.vroom.datasource

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.RemoteCallbackList
import android.os.RemoteException
import kotlin.random.Random

class VehicleDataSourceService : Service() {

    private val callbackList = RemoteCallbackList<VehicleDataCallback>()

    private val binder: VehicleDataSourceServiceInterface.Stub =
        object : VehicleDataSourceServiceInterface.Stub() {

            override fun registerCallback(callback: VehicleDataCallback?) {
                if (callback != null) {
                    callbackList.register(callback)
                }
            }

            override fun unregisterCallback(callback: VehicleDataCallback?) {
                if (callback != null) {
                    callbackList.unregister(callback)
                }
            }
        }

    private val runnable: Runnable = object : Runnable {

        override fun run() {

            val time = System.currentTimeMillis()
            val speed = speedDataSource.getSpeed(time).toFloat()
            val rpm = rpmDataSource.getSpeed(time).toFloat()

            val callbackAmount = callbackList.beginBroadcast()
            for (i in 0 until callbackAmount) {
                try {
                    callbackList.getBroadcastItem(i).onDataChanged(speed, rpm)
                } catch (e: RemoteException) {
                    // The RemoteCallbackList will take care of removing
                    // the dead object for us.
                }
            }
            callbackList.finishBroadcast()

            // random interval between new data for more realistic simulation
            handler.postDelayed(this, Random.Default.nextLong(100, 150))
        }
    }

    private val handler = Handler()

    private val speedDataSource = SpeedDataSource(
        valueMin = 0.0,
        valueMax = 220.0
    )

    private val rpmDataSource = SpeedDataSource(
        valueMin = 0.0,
        valueMax = 8000.0,
        offset = 1000.0
    )

    override fun onCreate() {
        super.onCreate()
        handler.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        callbackList.kill()
        handler.removeCallbacks(runnable)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }
}
