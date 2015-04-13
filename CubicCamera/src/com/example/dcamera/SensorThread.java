package com.example.dcamera;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class SensorThread extends Thread{

	protected static boolean runFlag, startSensorFlag, stopSensorFlag ;
	
	// 센서 관리
	private SensorManager sensorM ;
	// 센서 구동시 얻는 값 
	private SensorEventListener sensorListener =new SensorEventListener () {
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

		public void onSensorChanged(SensorEvent event) {		
			//Log.d ("Azimuth(방위)", "" +event.values[0]) ;
			//Log.d ("Pitch(경사도)", "" +event.values[1]) ;
			//Log.d ("Roll(회전)", "" +event.values[2]) ;
			Message msg =new Message () ;
			msg.what =1 ;
			msg.obj =(float) event.values[2] ;
			handler.sendMessage(msg) ;
		}
	} ;
	private Handler handler ;
	private Sensor sensor ;


	public SensorThread (Context context, Handler handler)
	{
		runFlag =false ;	// 스레드 run
		startSensorFlag =false ;	// 스레드 구동
		stopSensorFlag =false ;	// 스레드 중지
		
		this.handler =handler ;
		
		// 센서에 대한 시스템을 얻는다.
		sensorM =(SensorManager) context.getSystemService(Context.SENSOR_SERVICE) ;
		sensor =sensorM.getDefaultSensor(Sensor.TYPE_ORIENTATION) ;		// 회전 관련 센서 설정
	
	}
	
	public void run () {
		Looper.prepare() ;
		while (!isInterrupted() && !runFlag) {
			try {
				sleep (1000) ;	// 1초 간격으로 구동
				if (startSensorFlag) {	// 센서 구동 메세지를 얻으면
					// 센서의 값을 중간 속도로 받는다.
					sensorM.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL) ;
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
	}
}
