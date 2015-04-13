package com.example.dcamera;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class PreViewThread extends Thread {

	protected static boolean autoFlag, runFlag ;
	private Handler handler ;
	
	public PreViewThread (Handler handler) {
		this.handler =handler ;
		autoFlag =false ;
		runFlag =false ;
	}
	
	public void run () {
		Looper.prepare() ;
		while (!isInterrupted() && !runFlag) {
			try {
				sleep (100) ;	// 0.1초 간격으로 구동
				if (autoFlag) {	// play 메세지를 얻으면
					handler.sendEmptyMessage(0) ;	// 그에 따른 메세지 전달
				}
			} catch (InterruptedException e) {
				e.printStackTrace() ;
			}
		}
	}
}
