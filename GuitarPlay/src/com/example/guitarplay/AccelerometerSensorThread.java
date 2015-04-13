package com.example.guitarplay;

import android.content.Context;
import android.os.Looper;
import android.os.Handler;
import android.os.Message;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class AccelerometerSensorThread extends Thread {
	
	protected static boolean runFlag, startSensorFlag, stopSensorFlag ;
	
	private SensorManager sensorM ;
	private Sensor sensorAccel, sensorPitch ;
	private Handler handler ;
	private SensorEventListener sensorListener =new SensorEventListener () {
		public void onAccuracyChanged (Sensor sensor, int accuracy) {}
		public void onSensorChanged (SensorEvent event) {
			// 낮은 측정값 일때 버림
			if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {}
			if (event.sensor.getType () == Sensor.TYPE_ACCELEROMETER) {
				long c = System.currentTimeMillis();
				long t = c - last;
				if (t > 600) {
					last = c;
					// Log.d ("X", "" +event.values[0]) ;
					// Log.d ("Y", "" +event.values[1]) ;
					// Log.d ("Z", "" +event.values[2]) ;
					Message msg = new Message();
					msg.what = 1;
					val[0] = event.values[0];
					val[1] = event.values[1] +pitch ;
					val[2] = event.values[2];
					msg.obj = (float[]) val;
					handler.sendMessage(msg);

					float alpha = 0.8f;
					// alpha =t /(t+dT)
					// 중력 데이터
					gra[0] = alpha * gra[0] + (1 - alpha) * event.values[0];
					gra[1] = alpha * gra[1] + (1 - alpha) * event.values[1];
					gra[2] = alpha * gra[2] + (1 - alpha) * event.values[2];
					// msg.obj =(float[]) gra ;
					// handler.sendMessage(msg) ;
					// 가속도
					accel[0] = event.values[0] - gra[0];
					accel[1] = event.values[2] - gra[1];
					accel[2] = event.values[1] - gra[2];
					// msg.obj =(float[]) accel ;
					// handler.sendMessage(msg) ;
				}
			}
			else if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
				if (event.values[1] < 90 && event.values[1] > -90)
					pitch =event.values[1] /9 ;
				Message msg =new Message () ;
				msg.what =2 ;
				msg.obj =event.values[1] ;
				handler.sendMessage(msg) ;
				//Log.d ("D", "" +pitch) ;
			}
		}
	};
	private long last ;
	private float pitch ;
	private float[] val, accel, gra ;
	public AccelerometerSensorThread (Context context, Handler handler) {
		val =new float[3] ;
		accel =new float[3] ;
		gra =new float[3] ;
		
		runFlag =false ;	// 스레드 run
		startSensorFlag =false ;	// 스레드 구동
		stopSensorFlag =false ;	// 스레드 중지
		this.handler =handler ;
		
		// 센서에 대한 시스템을 얻는다.
		sensorM =(SensorManager) context.getSystemService(context.SENSOR_SERVICE) ;
		sensorAccel =sensorM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) ;
		sensorPitch =sensorM.getDefaultSensor(Sensor.TYPE_ORIENTATION) ;
	}
	
	public void run () {
		Looper.prepare () ;
		while (!isInterrupted() && !runFlag) {
			try {
				sleep (1000) ;	// 1초 간격으로 구동
				if (startSensorFlag) {	// 센서 구동 메세지를 얻으면
					// 센서의 값을 사용자조작 속도로 받는다.
					sensorM.registerListener(sensorListener, sensorAccel, SensorManager.SENSOR_DELAY_UI) ;
					sensorM.registerListener(sensorListener, sensorPitch, SensorManager.SENSOR_DELAY_UI) ;
					startSensorFlag =false ;
				}
				else if (stopSensorFlag) {	// 센서 중지 메세지를 얻으면
					// 센서 탐색 중지
					sensorM.unregisterListener(sensorListener) ;
					stopSensorFlag =false ;
				}
			} catch (InterruptedException e) {
				e.printStackTrace() ;
			}
		}
		//Looper.loop () ;
	}
}
