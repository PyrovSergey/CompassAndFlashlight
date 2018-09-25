package ru.pyrovsergey.flashlight

import android.app.Application
import android.content.Context
import ru.pyrovsergey.flashlight.presenter.AppComponent
import ru.pyrovsergey.flashlight.presenter.DaggerAppComponent

class App : Application() {
    private lateinit var context: Context

    companion object {
        lateinit var appComponent: AppComponent
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = applicationContext
        appComponent = DaggerAppComponent.create()
    }

    fun getInstance(): App {
        return instance
    }

    fun getContex(): Context {
        return context
    }

    fun getComponent(): AppComponent {
        return appComponent
    }
}