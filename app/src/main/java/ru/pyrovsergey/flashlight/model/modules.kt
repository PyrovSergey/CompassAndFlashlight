package ru.pyrovsergey.flashlight.model

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CompassModule {
    @Singleton
    @Provides
    fun provideCompass(): Compass {
        return Compass()
    }
}

@Module
class FlashlightModule {
    @Singleton
    @Provides
    fun provideFlashlight(): Flashlight {
        return Flashlight()
    }
}