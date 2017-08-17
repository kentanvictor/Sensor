package com.example.dell.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private ImageView sensor;
    private TextView tv;
    private SensorManager manager;
    private Sensor magneticSensor, accelerometerSensor;
    private SensorEventListener listener = new SensorListener();
    private float[] values, r, gravity, geomagnetic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensor = (ImageView) this.findViewById(R.id.img);
        tv = (TextView) this.findViewById(R.id.tv);
        sensor.setKeepScreenOn(true);
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //获取SensorManager
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        listener = new SensorListener();
        //获取Sensor
        magneticSensor = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometerSensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //初始化数组
        values = new float[3];//用来保存最终的结果
        gravity = new float[3];//用来保存加速度传感器的值
        r = new float[9];//
        geomagnetic = new float[3];//用来保存地磁传感器的值
    }

    @Override
    protected void onResume() {
        manager.registerListener(listener, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener(listener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        manager.unregisterListener(listener);
        super.onPause();
    }

    private final class SensorListener implements SensorEventListener {
        private float predegree = 0;
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                geomagnetic = event.values;
            }
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                gravity = event.values;
                getValue();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
    public void getValue() {
        // r从这里返回
        SensorManager.getRotationMatrix(r, null, gravity, geomagnetic);
        //values从这里返回
        SensorManager.getOrientation(r, values);
        //提取数据
        double azimuth = Math.toDegrees(values[0]);
        if (azimuth<0) {
            azimuth=azimuth+360;
        }
        double pitch = Math.toDegrees(values[1]);
        double roll = Math.toDegrees(values[2]);
        tv.invalidate();
        tv.setText("Azimuth：" + (int)azimuth + "\nPitch：" + (int)pitch + "\nRoll：" + (int)roll);
    }
}
