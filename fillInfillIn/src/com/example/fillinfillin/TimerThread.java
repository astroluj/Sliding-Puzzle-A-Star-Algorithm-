package com.example.fillinfillin;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class TimerThread extends Thread {
	
	PlayCrazy playC ;
	MainActivity mainAct ;
	
	protected static Animation animTime ;
	protected static boolean runFlag ;
	
	private Context context ;
	private Handler handler ;
	private final int TIME ;
	
	
	public TimerThread (Context nContext, Handler handler, int time)
	{
		this.context =nContext ;
		this.handler =handler ;
		this.TIME =time ;
		
		runFlag =false ;
		animTime =AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.timer_anim) ;
		animTime.setDuration(TIME *1000) ;
	}
	
	public void run ()
	{
		Looper.prepare() ;
		while (!isInterrupted() && !runFlag) {		
			try {
				sleep(1000) ;
				handler.sendEmptyMessage(0) ;
			} catch (InterruptedException e) {
				e.printStackTrace() ;
			}
		}
		//Looper.loop() ;
	}
}
