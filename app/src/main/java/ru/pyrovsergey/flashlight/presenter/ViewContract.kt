package ru.pyrovsergey.flashlight.presenter

import android.view.animation.RotateAnimation
import com.arellomobile.mvp.MvpView

interface ViewContract : MvpView {
    fun setViewStickySwitchFlashlightOn()
    fun setViewStickySwitchFlashlightOff()
    fun setNewDirectionName(directionName: String)
    fun setNewDegree(degree: String)
    fun startAnimation(animation: RotateAnimation)
}