package com.example.dcamera;

import android.os.Handler;
import android.os.Looper; 

public class AfterImgThread extends Thread {
	
	CameraActivity cameraAct ;
	
	protected static boolean runFlag ;
	private Handler handler ;
	
	public AfterImgThread (Handler handler) {
		this.handler =handler ;
		runFlag =false ;
	}

	public void run () {
		Looper.prepare () ;
		while (!interrupted() && !runFlag) {
			try {
				sleep (100) ;	// 0.1초 간격으로 구동
				if (cameraAct.afterFlag) {	// 잔상 표시 메세지를 얻을 때
					handler.sendEmptyMessage(1) ;
					cameraAct.afterFlag =false ;
				}
				if (cameraAct.captureFlag) {	// 캡쳐 메세지를 얻을 때
					handler.sendEmptyMessage(2) ;
					cameraAct.captureFlag =false ;
				}
			} catch (InterruptedException e) {
				e.printStackTrace() ;
			}
		}
	}
}
