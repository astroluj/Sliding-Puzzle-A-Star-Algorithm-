package com.example.dcamera;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class FocusThread extends Thread {

	CameraActivity cameraAct;
	
	protected static boolean runFlag ;
	
	private Handler handler ;

	public FocusThread(Handler handler) {
		this.handler =handler ;
		runFlag =false ;
	}

	public void run() {
		Looper.prepare() ;
		while (!isInterrupted() && !runFlag) {
			try {
				sleep (100) ;	// 0.1초 간격으로 구동
				if (cameraAct.focusFlag)	// 초점이 맞춰 졌을 때
					handler.sendEmptyMessage(3) ;
				else	// 초점이 맞춰진 상태가 아닐 때
					handler.sendEmptyMessage(4) ;
			} catch (InterruptedException e) {
				e.printStackTrace() ;
			}
		}
	}
}
