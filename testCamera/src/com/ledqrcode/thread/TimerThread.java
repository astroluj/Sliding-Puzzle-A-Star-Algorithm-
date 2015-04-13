package com.ledqrcode.thread;

import android.os.Looper;
import android.os.Handler;

public class TimerThread extends Thread {

	private Handler handler;
	
	private boolean runFlag ;
	private long SLEEP = 1 ;
	

	public TimerThread(Handler handler, long SLEEP) {
		runFlag = false;

		this.handler = handler;
		this.SLEEP = SLEEP ;
	}

	public void run() {
		Looper.prepare();
		try {
			while (runFlag && !isInterrupted()) {
				try {
					handler.sendEmptyMessage(0);
					// Capture
					sleep(SLEEP);
				} catch (Exception e) {}
			}
		} catch (Exception e) {}
		// Looper.loop() ;
	}
	
	public synchronized void setRun (boolean runFlag) {
		this.runFlag = runFlag ;
	}
	public synchronized boolean getRun () {
		return this.runFlag ;
	}
}
