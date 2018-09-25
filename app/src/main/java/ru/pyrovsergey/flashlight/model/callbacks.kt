package ru.pyrovsergey.flashlight.model

import android.view.animation.RotateAnimation

interface CompassCallback {
    fun renameViewDirection(directionName: String)
    fun renameViewDegree(degree: String)
    fun startViewAnimationCompassImage(animation: RotateAnimation)
}

interface FlashlightCallback {
    fun setStickySwitchFlashlightOn()
    fun setStickySwitchFlashlightOff()
}