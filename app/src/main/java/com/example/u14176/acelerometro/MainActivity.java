package com.example.u14176.acelerometro;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private GameView canvas;

    private int raio = 30;
    private float coordX, coordY;

    private Timer timer;
    private Handler handler;

    private SensorManager sensorManager;
    private Sensor acelerometro;

    private float sensorX, sensorY, sensorZ;

    private long ultimaAtualizacaoSensor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);

        Display display = getWindowManager().getDefaultDisplay();
        Point tamanho = new Point();
        display.getSize(tamanho);

        final int largura = tamanho.x;
        final int altura = tamanho.y;

        coordX = largura / 2;
        coordY = altura / 2;

        canvas = new GameView(MainActivity.this);
        setContentView(canvas);

        handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                canvas.invalidate();
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                coordX +=sensorX * -3;
                coordY += sensorY * 3;

                if (coordX > largura - raio)
                    coordX = largura - raio;
                else
                    if (coordX < raio)
                        coordX = raio;

                if (coordY > altura - raio)
                    coordY = altura - raio;
                else
                    if (coordY < raio)
                        coordY = raio;


                handler.sendEmptyMessage(0);
            }
        }, 0, 50);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mSensor = event.sensor;

        if (mSensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long tAtual = System.currentTimeMillis();

            if((tAtual - ultimaAtualizacaoSensor) > 50 ) {
                ultimaAtualizacaoSensor = tAtual;

                sensorX = event.values[0];
                sensorY = event.values[1];
                sensorZ = event.values[2];
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public class GameView extends View {
        private Paint fundoPaint, bolaPaint;

        public GameView(Context context) {
            super(context);
            setFocusable(true);

            bolaPaint = new Paint();
            bolaPaint.setColor(Color.WHITE);
        }

        public void onDraw(Canvas canvas) {
            bolaPaint.setStyle(Paint.Style.FILL);
            bolaPaint.setAntiAlias(true);
            canvas.drawCircle(coordX, coordY, raio, bolaPaint);
        }
    }
}
