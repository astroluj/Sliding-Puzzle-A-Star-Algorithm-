package com.ai.slidingpuzzle.thread;

import java.util.ArrayList;

import com.ai.slidingpuzzle.BlockIndex;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class SolveMovingThread extends Thread {

	final long SLEEP =250 ;
	// Handler Case
	final int MOVE =0, STOP =1 ;
	
	private Context context ;
	private Handler handler ;
	private ArrayList<BlockIndex> pathList ;
	private boolean runFlag ;
	
	public SolveMovingThread (Context context, ArrayList<BlockIndex> pathList, Handler handler) {
		
		this.context =context ;
		this.pathList =pathList ;
		this.handler =handler ;
	}
	
	public void run () {
		
		while (runFlag) {
			
			try {
				
				// Move (Empty Block Index)
				if (pathList.size() > 0) {
					Message msg =handler.obtainMessage() ;
					msg.what =MOVE ;
					msg.obj =(BlockIndex) pathList.get(0) ;
					pathList.remove(0) ;
					
					handler.sendMessage(msg) ;
				}
				else handler.sendEmptyMessage(STOP) ;
				
				sleep (SLEEP) ;
			} catch (Exception e) {}
		}
	}
	
	synchronized public void setRun (boolean runFlag) {
		this.runFlag =runFlag ;
	}
}
