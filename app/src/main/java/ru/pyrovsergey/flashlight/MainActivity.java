package ru.pyrovsergey.flashlight;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import io.ghyeok.stickyswitch.widget.StickySwitch;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    @BindView(R.id.arrow_north)
    ImageView arrowNorth;
    @BindView(R.id.imageCompass)
    ImageView imageCompass;
    @BindView(R.id.direction_text_view)
    TextView directionTextView;
    @BindView(R.id.degree_text_view)
    TextView degreeTextView;
    @BindView(R.id.sticky_switch_flashlight)
    StickySwitch stickySwitchFlashlight;
    @BindView(R.id.head_layout)
    RelativeLayout headLayout;
    private static final int CAMERA_REQUEST = 50;
    private boolean flashLightStatus = false;
    private Window window;

    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    private float azimuth = 0f;
    private float currentAzimuth = 0f;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initialization();
    }

    private void initialization() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlackBackground));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.colorBlackBackground));
        arrowNorth.bringToFront();
        final boolean hasCameraFlash = getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        boolean isEnabled = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
        stickySwitchFlashlight.setEnabled(isEnabled);
        stickySwitchFlashlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasCameraFlash) {
                    if (flashLightStatus)
                        flashLightOff();
                    else
                        flashLightOn();
                } else {
                    Toasty.error(getBaseContext(), getString(R.string.no_flash_available_on_your_device), 1, true).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);

    }

    private void flashLightOn() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
            getWhiteUI();
            flashLightStatus = true;
        } catch (CameraAccessException e) {
        }
    }

    private void flashLightOff() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
            getBlackUI();
            flashLightStatus = false;
        } catch (CameraAccessException e) {
        }
    }

    private void getWhiteUI() {
        stickySwitchFlashlight.setTextColor(getColor(R.color.colorBlackTextUI));
        directionTextView.setTextColor(getColor(R.color.colorBlackTextUI));
        degreeTextView.setTextColor(getColor(R.color.colorBlackTextUI));
        arrowNorth.setImageResource(R.drawable.arrow_north_white);
        imageCompass.setImageResource(R.drawable.white_compass);
        headLayout.setBackgroundColor(getColor(R.color.colorWhiteBackground));
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorWhiteBackground));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.colorWhiteBackground));
    }


    private void getBlackUI() {
        stickySwitchFlashlight.setTextColor(getColor(R.color.colorWhiteTextUI));
        directionTextView.setTextColor(getColor(R.color.colorWhiteTextUI));
        degreeTextView.setTextColor(getColor(R.color.colorWhiteTextUI));
        arrowNorth.setImageResource(R.drawable.arrow_north_black);
        imageCompass.setImageResource(R.drawable.black_compass);
        headLayout.setBackgroundColor(getColor(R.color.colorBlackBackground));
        window.getDecorView().setSystemUiVisibility(0);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlackBackground));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.colorBlackBackground));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    stickySwitchFlashlight.setEnabled(true);
                } else {
                    Toasty.error(this, getString(R.string.permission_denied_for_the_camera), 1, true).show();
                }
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;
        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                geomagnetic[0] = alpha * geomagnetic[0] + (1 - alpha) * event.values[0];
                geomagnetic[1] = alpha * geomagnetic[1] + (1 - alpha) * event.values[1];
                geomagnetic[2] = alpha * geomagnetic[2] + (1 - alpha) * event.values[2];
            }

            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = (float) Math.toDegrees(orientation[0]);
                azimuth = (azimuth + 360) % 360;

                getDirectionName(azimuth);

                Animation animation = new RotateAnimation(-currentAzimuth, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                currentAzimuth = azimuth;
                animation.setDuration(500);
                animation.setRepeatCount(0);
                animation.setFillAfter(true);

                imageCompass.startAnimation(animation);
            }
        }
    }

    private void getDirectionName(float azimuth) {
        int range = (int) (azimuth / (360f / 16f));
        String dirTxt = "";
        if (range == 15 || range == 0)
            dirTxt = getString(R.string.north);
        if (range == 1 || range == 2)
            dirTxt = getString(R.string.north_east);
        if (range == 3 || range == 4)
            dirTxt = getString(R.string.east);
        if (range == 5 || range == 6)
            dirTxt = getString(R.string.south_east);
        if (range == 7 || range == 8)
            dirTxt = getString(R.string.south);
        if (range == 9 || range == 10)
            dirTxt = getString(R.string.south_west);
        if (range == 11 || range == 12)
            dirTxt = getString(R.string.west);
        if (range == 13 || range == 14)
            dirTxt = getString(R.string.north_west);
        directionTextView.setText(dirTxt);
        degreeTextView.setText(String.valueOf((int) azimuth) + "°");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}