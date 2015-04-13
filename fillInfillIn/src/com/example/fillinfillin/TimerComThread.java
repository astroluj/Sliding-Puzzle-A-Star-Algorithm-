package com.example.fillinfillin;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class TimerComThread extends Thread {

	protected static boolean runFlag ;
	
	private Handler handler ;
	private final int TIME ;
	
	public TimerComThread (Handler handler, int time)
	{
		this.handler =handler ;
		this.TIME =time ;
		runFlag =false ;
	}
	
	public void run ()
	{
		Looper.prepare() ;
		
		int i =0 ;
		while (!isInterrupted() && !runFlag) {
			try {
				Thread.sleep(TIME) ;
				handler.sendEmptyMessage(3) ;
			} catch (InterruptedException e) {
				e.printStackTrace() ;
			}
		}
		//Looper.loop() ;
	}
}
