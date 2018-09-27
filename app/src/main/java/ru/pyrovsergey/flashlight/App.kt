package ru.pyrovsergey.flashlight

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import ru.pyrovsergey.flashlight.presenter.AppComponent
import ru.pyrovsergey.flashlight.presenter.DaggerAppComponent

class App : Application() {

    companion object {
        lateinit var appComponent: AppComponent
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: App
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = applicationContext
        appComponent = DaggerAppComponent.create()
    }
}