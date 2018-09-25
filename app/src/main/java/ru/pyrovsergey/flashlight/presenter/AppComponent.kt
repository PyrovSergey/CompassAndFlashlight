package ru.pyrovsergey.flashlight.presenter

import dagger.Component
import ru.pyrovsergey.flashlight.model.Compass
import ru.pyrovsergey.flashlight.model.CompassModule
import ru.pyrovsergey.flashlight.model.Flashlight
import ru.pyrovsergey.flashlight.model.FlashlightModule
import javax.inject.Singleton

@Singleton
@Component(modules = [FlashlightModule::class, CompassModule::class])
interface AppComponent {
    fun getFlashlight(): Flashlight
    fun getCompass(): Compass
}