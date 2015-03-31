package com.ai.slidingpuzzle.thread;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.ai.slidingpuzzle.BlockIndex;
import com.ai.slidingpuzzle.Board;
import com.ai.slidingpuzzle.SolveAStar;

public class AStarThread extends Thread {
	
	final long SLEEP =2;
	// Handler Case
	final int START =0, STOP =1, IS_SHOW =2 ;
	
	SolveAStar solAstar ;
	
	private Context context ;
	private Handler handler ;
	// solvePath
	private ArrayList<BlockIndex> pathList ;
	// BroadCast Action
	private String solve, fail ;
	// runFlag
	private boolean runFlag ;

	
	public AStarThread (Context context, Board board, String solve, String fail, Handler handler) {

		solAstar =new SolveAStar(board, board.getSize()) ;
		
		this.context =context ;
		this.handler =handler ;
		this.solve =solve ;
		this.fail =fail ;
		this.runFlag =false ;
	}
	
	public void run () {

		// Progress Show
		handler.sendEmptyMessage(START) ;
		
		while (runFlag) {
		 
			try {
				pathList =solAstar.startSolve() ;
				
				// Check Progress
				handler.sendEmptyMessage(IS_SHOW) ;
				
				// Solve Case
				try {
					if (pathList.size() > 0) {
						
						// Progress DisMiss
						handler.sendEmptyMessage(STOP) ;
						// Thread Stop
						this.runFlag =false ;
						// Send SolveBroadCast
						this.context.sendBroadcast(new Intent (solve));
					}
				} catch (NullPointerException e) {
					
					// Fail Case
					// Progress DisMiss
					handler.sendEmptyMessage(STOP) ;
					// Thread Stop
					this.runFlag =false ;
					// Send FailBoardCast
					this.context.sendBroadcast(new Intent (fail)); 
				}
				
				sleep (SLEEP) ;
			} catch (Exception e) {}
		}
	}
	
	synchronized public ArrayList <BlockIndex> getPathList () {
		return this.pathList ;
	}
	
	synchronized public void setRun (boolean runFlag) {
		this.runFlag =runFlag ;
	}
}
