package ru.pyrovsergey.flashlight

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.animation.RotateAnimation
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import es.dmoral.toasty.Toasty
import io.ghyeok.stickyswitch.widget.StickySwitch
import kotlinx.android.synthetic.main.activity_main.*
import ru.pyrovsergey.flashlight.presenter.Presenter
import ru.pyrovsergey.flashlight.presenter.ViewContract


class MainActivity : MvpAppCompatActivity(), ViewContract, SensorEventListener {
    companion object {
        const val CAMERA_REQUEST = 50
    }

    @InjectPresenter
    lateinit var presenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        arrowNorth.bringToFront()

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST)
        setEnabledViewStickySwitchFlashlight(ContextCompat.checkSelfPermission(App.context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    override fun onResume() {
        super.onResume()
        presenter.prepareFlashlight()
        presenter.prepareCompass(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.unregisterListenerFlashlight()
        presenter.unregisterListenerCompass(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_REQUEST -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setClickListenerOnStickySwitch()
                setEnabledViewStickySwitchFlashlight(true)
            } else {
                Toasty.error(this, getString(R.string.permission_denied_for_the_camera), 1, true).show()
            }
        }
    }

    private fun setClickListenerOnStickySwitch() {
        stickySwitchFlashlight.setOnClickListener {
            presenter.onClickStickySwitch()
        }
    }

    private fun setEnabledViewStickySwitchFlashlight(permission: Boolean) {
        stickySwitchFlashlight.isEnabled = permission
    }

    override fun setViewStickySwitchFlashlightOff() {
        stickySwitchFlashlight.setDirection(StickySwitch.Direction.LEFT)
        getBlackUI()
    }

    override fun setViewStickySwitchFlashlightOn() {
        stickySwitchFlashlight.setDirection(StickySwitch.Direction.RIGHT)
        getWhiteUI()
    }

    private fun getWhiteUI() {
        stickySwitchFlashlight!!.textColor = getColor(R.color.colorBlackTextUI)
        directionTextView!!.setTextColor(getColor(R.color.colorBlackTextUI))
        degreeTextView!!.setTextColor(getColor(R.color.colorBlackTextUI))
        arrowNorth!!.setImageResource(R.drawable.arrow_north_white)
        imageCompass!!.setImageResource(R.drawable.white_compass)
        headLayout!!.setBackgroundColor(getColor(R.color.colorWhiteBackground))
        window!!.statusBarColor = ContextCompat.getColor(this, R.color.colorWhiteBackground)
        window!!.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window!!.navigationBarColor = ContextCompat.getColor(this, R.color.colorWhiteBackground)
    }

    private fun getBlackUI() {
        stickySwitchFlashlight!!.textColor = getColor(R.color.colorWhiteTextUI)
        directionTextView!!.setTextColor(getColor(R.color.colorWhiteTextUI))
        degreeTextView!!.setTextColor(getColor(R.color.colorWhiteTextUI))
        arrowNorth!!.setImageResource(R.drawable.arrow_north_black)
        imageCompass!!.setImageResource(R.drawable.black_compass)
        headLayout!!.setBackgroundColor(getColor(R.color.colorBlackBackground))
        window!!.decorView.systemUiVisibility = 0
        window!!.statusBarColor = ContextCompat.getColor(this, R.color.colorBlackBackground)
        window!!.navigationBarColor = ContextCompat.getColor(this, R.color.colorBlackBackground)
    }

    override fun onSensorChanged(event: SensorEvent) {
        presenter.onSensorChanged(event)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun setNewDirectionName(directionName: String) {
        directionTextView.text = directionName
    }

    override fun setNewDegree(degree: String) {
        degreeTextView.text = degree
    }

    override fun startAnimation(animation: RotateAnimation) {
        imageCompass.startAnimation(animation)
    }
}
