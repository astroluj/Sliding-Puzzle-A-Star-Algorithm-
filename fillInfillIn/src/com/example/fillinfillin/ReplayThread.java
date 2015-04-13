package com.example.fillinfillin;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

public class ReplayThread extends Thread {
	
	MainActivity mainAct ;
	
	protected static boolean runFlag ;
	
	private Handler handler ;
	
	public ReplayThread (Handler handler) {
		this.handler =handler ;
		runFlag =false ;
	}
	
	public void run () {
		Looper.prepare() ;
		while (!isInterrupted() && !runFlag) {
			try {
				sleep (100) ;
				if (mainAct.playFlag) 
					handler.sendEmptyMessage(1) ;
				else if (mainAct.cancelFlag) 
					handler.sendEmptyMessage(2) ;
			} catch (InterruptedException e) {
				e.printStackTrace() ;
			}
		}
		Looper.loop() ;
	}
}
