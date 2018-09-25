package ru.pyrovsergey.flashlight.model

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import ru.pyrovsergey.flashlight.App
import ru.pyrovsergey.flashlight.presenter.Presenter


class Flashlight {

    private var cameraManager: CameraManager = App.instance.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    lateinit var presenterCallback: FlashlightCallback
    lateinit var torchCallback: CameraManager.TorchCallback
    private var flashLightStatus = false
    private val hasCameraFlash = App.instance.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

    fun prepare(presenter: Presenter) {
        presenterCallback = presenter
        torchCallback = object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(cameraId: String?, enabled: Boolean) {
                super.onTorchModeChanged(cameraId, enabled)
                flashLightStatus = enabled
                when(enabled) {
                    true -> presenter.setStickySwitchFlashlightOn()
                    false-> presenter.setStickySwitchFlashlightOff()
                }
            }
        }
        cameraManager.registerTorchCallback(torchCallback, null)
    }

    fun flashlightOnOff() {
        if (hasCameraFlash) {
            when (flashLightStatus) {
                true -> off()
                else -> on()
            }
        }
    }

    fun on() {
        val cameraId = cameraManager.cameraIdList[0]
        cameraManager.setTorchMode(cameraId, true)
        flashLightStatus = true
    }

    fun off() {
        val cameraId = cameraManager.cameraIdList[0]
        cameraManager.setTorchMode(cameraId, false)
        flashLightStatus = false
    }

    fun unregisterTorchCallback() {
        cameraManager.unregisterTorchCallback(torchCallback)
    }
}