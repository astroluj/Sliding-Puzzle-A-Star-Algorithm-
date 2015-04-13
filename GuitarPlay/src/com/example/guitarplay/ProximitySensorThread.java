package com.example.guitarplay;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class ProximitySensorThread extends Thread {

	protected static boolean runFlag, startSensorFlag, stopSensorFlag;
	
	private SensorManager sensorM;
	private Handler handler;
	private Sensor sensor;
	// 센서 구동시 얻는 값
	private SensorEventListener sensorListener = new SensorEventListener() {
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
		public void onSensorChanged(SensorEvent event) {
			// 낮은 측정값 일때 버림
			if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {}
			if (event.sensor.getType () != Sensor.TYPE_PROXIMITY) return ;
			Message msg = new Message();
			msg.what = 0;
			msg.obj = (float) event.values[0];
			// Log.d ("D", "" +event.values[0]) ;
			// Log.d ("D", "" +sensor.getMaximumRange()) ;
			handler.sendMessage(msg);

		}
	};

	public ProximitySensorThread(Context context, Handler handler) {
		runFlag =false ;	// 스레드 run
		startSensorFlag =false ;	// 스레드 구동
		stopSensorFlag =false ;	// 스레드 중지
		this.handler =handler ;
		
		// 센서에 대한 시스템을 얻는다.
		sensorM =(SensorManager) context.getSystemService(Context.SENSOR_SERVICE) ;
		sensor =sensorM.getDefaultSensor(Sensor.TYPE_PROXIMITY) ;		// 근접 관련 센서 설정
	}

	public void run() {
		Looper.prepare();
		while (!isInterrupted() && !runFlag) {
			try {
				sleep (1000) ;	// 1초 간격으로 구동
				if (startSensorFlag) {	// 센서 구동 메세지를 얻으면
					// 센서의 값을 사용자조작 속도로 받는다.
					sensorM.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_UI) ;
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
		//Looper.loop();
	}
}
