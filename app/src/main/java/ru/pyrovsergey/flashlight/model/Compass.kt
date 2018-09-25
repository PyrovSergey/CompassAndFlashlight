package ru.pyrovsergey.flashlight.model

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import ru.pyrovsergey.flashlight.App
import ru.pyrovsergey.flashlight.R
import ru.pyrovsergey.flashlight.presenter.Presenter

class Compass {
    private lateinit var presenterCallback: CompassCallback
    private lateinit var sensorManager: SensorManager
    private val context = App.instance
    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)
    private var azimuth = 0f
    private var currentAzimuth = 0f

    fun prepare(presenter: Presenter, listener: SensorEventListener) {
        presenterCallback = presenter
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(listener, sensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(listener, sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME)
    }

    fun onSensorChangedListener(event: SensorEvent) {
        val alpha = 0.97f
        synchronized(this) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]
            }
            if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                geomagnetic[0] = alpha * geomagnetic[0] + (1 - alpha) * event.values[0]
                geomagnetic[1] = alpha * geomagnetic[1] + (1 - alpha) * event.values[1]
                geomagnetic[2] = alpha * geomagnetic[2] + (1 - alpha) * event.values[2]
            }

            val R = FloatArray(9)
            val I = FloatArray(9)

            val success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)
            if (success) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)
                azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                azimuth = (azimuth + 360) % 360

                presenterCallback.renameViewDirection(getDirectionName(azimuth))
                presenterCallback.renameViewDegree(azimuth.toInt().toString() + "°")

                val animation = RotateAnimation(-currentAzimuth, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                currentAzimuth = azimuth
                animation.duration = 500
                animation.repeatCount = 0
                animation.fillAfter = true

                presenterCallback.startViewAnimationCompassImage(animation)
            }
        }
    }

    private fun getDirectionName(azimuth: Float): String {
        val range = (azimuth / (360f / 16f)).toInt()
        var dirTxt = ""
        when (range) {
            15, 0 -> dirTxt = context.getString(R.string.north)
            1, 2 -> dirTxt = context.getString(R.string.north_east)
            3, 4 -> dirTxt = context.getString(R.string.east)
            5, 6 -> dirTxt = context.getString(R.string.south_east)
            7, 8 -> dirTxt = context.getString(R.string.south)
            9, 10 -> dirTxt = context.getString(R.string.south_west)
            11, 12 -> dirTxt = context.getString(R.string.west)
            13, 14 -> dirTxt = context.getString(R.string.north_west)
        }
        return dirTxt
    }

    fun unregister(listener: SensorEventListener) {
        sensorManager.unregisterListener(listener)
    }
}