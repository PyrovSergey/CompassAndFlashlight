package ru.pyrovsergey.flashlight.presenter

import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.view.animation.RotateAnimation
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.pyrovsergey.flashlight.App
import ru.pyrovsergey.flashlight.model.Compass
import ru.pyrovsergey.flashlight.model.CompassCallback
import ru.pyrovsergey.flashlight.model.Flashlight
import ru.pyrovsergey.flashlight.model.FlashlightCallback

@InjectViewState
class Presenter : MvpPresenter<ViewContract>(), FlashlightCallback, CompassCallback {
    private var flashlight: Flashlight = App.appComponent.getFlashlight()
    fun prepareFlashlight() = flashlight.prepare(this)
    fun onClickStickySwitch() = flashlight.flashlightOnOff()
    fun unregisterListenerFlashlight() = flashlight.unregisterTorchCallback()
    override fun setStickySwitchFlashlightOn() = viewState.setViewStickySwitchFlashlightOn()
    override fun setStickySwitchFlashlightOff() = viewState.setViewStickySwitchFlashlightOff()

    private var compass: Compass = App.appComponent.getCompass()
    fun prepareCompass(listener: SensorEventListener) = compass.prepare(this, listener)
    fun unregisterListenerCompass(listener: SensorEventListener) = compass.unregister(listener)
    fun onSensorChanged(event: SensorEvent) = compass.onSensorChangedListener(event)
    override fun renameViewDirection(directionName: String) = viewState.setNewDirectionName(directionName)
    override fun renameViewDegree(degree: String) = viewState.setNewDegree(degree)
    override fun startViewAnimationCompassImage(animation: RotateAnimation) = viewState.startAnimation(animation)
}