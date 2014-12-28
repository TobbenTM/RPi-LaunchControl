package com.tobbentm.rpi_launchcontrol_android;

import android.app.Activity;
import android.graphics.drawable.ClipDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;


public class MainActivity extends Activity implements SensorEventListener {

    private static final String URL = "192.168.10.2";

    TextView tv;
    ImageView ivPlungerBox, ivPlungerPlunger, ivCharge;
    ClipDrawable cfill;
    Button btn;
    SensorManager sm;
    Sensor s;
    float g = 0, m = 0;
    boolean up = true;
    int c = 0; long lastts;
    AsyncHttpClient cli;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lastts = System.nanoTime();

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        s = sm.getDefaultSensor(s.TYPE_ACCELEROMETER);

        tv = (TextView) this.findViewById(R.id.maintext);

        ivPlungerBox = (ImageView) this.findViewById(R.id.plunger_box);
        ivPlungerPlunger = (ImageView) this.findViewById(R.id.plunger_plunger);
        ivCharge = (ImageView) this.findViewById(R.id.charge_lvl_filled);

        ivPlungerBox.setImageResource(R.drawable.plunger_box);
        ivPlungerPlunger.setImageResource(R.drawable.plunger_plunger);// 150 px descent
        cfill = (ClipDrawable) ivCharge.getDrawable();
        cfill.setLevel(725);

        btn = (Button) this.findViewById(R.id.mainbtn);
        btn.setEnabled(false);

        cli = new AsyncHttpClient();
    }

    @Override
    protected void onResume(){
        super.onResume();
        sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    protected void onStart(){
        super.onStart();
        tv.setText(R.string.status_connecting);
        cli.get(URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                tv.setText(R.string.status_ready);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                tv.setText(R.string.status_disconnected);
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        final float a = 0.8f;
        float diff = (event.timestamp - lastts)/200000000F;
        lastts = event.timestamp;

        g = (float)(a*g+0.2*event.values[1]);

        float y = (event.values[1]-g)*diff;
        if(y<2&&y>-2) y=0;
        //Log.d("LAUNCH", "Diff: " + diff);
        if(up)
            m += (y<0)?0:(y>15)?15:y;
        else
            m += (y>0)?0:(y<-15)?-15:y;

        if(m>100||m<0){
            m = (m>100)?100:0;
            up = !up;
            //Log.d("LAUNCH", "Incrementing, m: " + m + ", y: " + y + ", c: " + c + ", up: " + up);
            c++;
            cfill.setLevel(cfill.getLevel()+1266);
        }

        ivPlungerPlunger.setY(m*2.0f);
        if(c>=7)
            btn.setEnabled(true);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void fire(View view) {
        btn.setEnabled(false);
        cli.get(URL + "/fire", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                tv.setText(R.string.status_fired);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                tv.setText(R.string.status_failed);
            }
        });
    }
}
