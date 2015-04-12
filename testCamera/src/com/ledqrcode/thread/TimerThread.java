package com.ledqrcode.thread;

import android.os.Looper;
import android.os.Handler;

public class TimerThread extends Thread {

	private boolean runFlag ;
	private Handler handler;

	public TimerThread(Handler handler) {
		runFlag = false;

		this.handler = handler;
	}

	public void run() {
		Looper.prepare();
		try {
			while (runFlag && !isInterrupted()) {
				try {
					// Capture
					sleep(10);
					handler.sendEmptyMessage(0);
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
