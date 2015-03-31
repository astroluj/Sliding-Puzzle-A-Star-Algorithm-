package com.ai.slidingpuzzle;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Stack;

import android.util.Log;

public class SolveHillClimbing extends BoardMove {

	private PriorityQueue<Board> que ;
	private Stack<Board> stack ;
	private Board board ;
	private BoardSize size ;
	
	public SolveHillClimbing (Board board, BoardSize size) {
		super (size) ;
		
		this.board =board ;
		this.size =size ;
	}

	// 큐에서 우선순위로 큐를 빼서
	// 움직일 수 있는 방향으로 움직인 보드를 다시 넣음
	public ArrayList <BlockIndex> startSolve () {
		
		// Direction
		// 직전에 움직인 방향의 반대로 움직이면 안됨
		final int TOP =1, BOTTOM =2, LEFT =3, RIGHT =4 ;
		
		que =new PriorityQueue<Board>() ;
		
		stack =new Stack<Board>() ;
		stack.add(this.board) ;
		
		//while (!que.isEmpty()) {
		while (!stack.isEmpty() || !que.isEmpty()) {
			
			// 우선이 아닌 보드먼저 넣기
			while (!que.isEmpty()) stack.push(que.poll()) ;
			Board board =stack.pop() ;
			
			//Board board =stack.pop() ;
			if (board == null) continue ;
			
			// 블록들이 모두 원래 자리에 있다면
			if (board.getMatchHeuristic(board.getBlock()) == board.getMatchedBlocks()) {
			//if (board.getMoveHeuristic(board.getBlock()) == 0) {
				Log.i ("SolveHillClimbing", "Success") ;
				stack.removeAll(stack) ;
					
				return board.getPath() ;
			}
			
			// Empty Index Add
			board.addPath(board.getEmptyBlockIndex()) ;
			
			// 최대 진행 횟수 동안 성공이 안되면 BackTracking
			if (board.getTotalMovedHeuristic() == board.MAX_MOVED) 
				continue ;
			
			
			try {
				// Get EmptyBlock Index
				BlockIndex blockIndex =board.getEmptyBlockIndex() ;
				// PrevDirection
				int prevDirection =board.getPrevDirection() ;
				// Move Top 
				try {
					if (prevDirection != TOP) {
						BlockIndex newBlockIndex =new BlockIndex (blockIndex.getRow() -1, blockIndex.getCol()) ;
						que.add(moveDirection(board, newBlockIndex, blockIndex, BOTTOM)) ;
						//stack.add(moveDirection(board, blockIndex, newBlockIndex, BOTTOM)) ;
					}
				} catch (NullPointerException e) {}
				
				// Move Bottom
				try {
					if (prevDirection != BOTTOM) {
						BlockIndex newBlockIndex =new BlockIndex (blockIndex.getRow() +1, blockIndex.getCol()) ;
						que.add(moveDirection(board, newBlockIndex, blockIndex, TOP)) ;
						//stack.add(moveDirection(board, blockIndex, newBlockIndex, TOP)) ;
					}
				} catch (NullPointerException e) {}
		
				// Move Left
				try {
					if (prevDirection != LEFT) {
						BlockIndex newBlockIndex =new BlockIndex (blockIndex.getRow(), blockIndex.getCol() -1) ;
						que.add(moveDirection(board, newBlockIndex, blockIndex,  RIGHT)) ;
						//stack.add(moveDirection(board, blockIndex, newBlockIndex, RIGHT)) ;
					}
				} catch (NullPointerException e) {}
			
				// Move Right
				try {
					if (prevDirection != RIGHT) {
						BlockIndex newBlockIndex =new BlockIndex (blockIndex.getRow(), blockIndex.getCol() +1) ;
						que.add(moveDirection(board,  newBlockIndex, blockIndex, LEFT)) ;
						//stack.add(moveDirection(board, blockIndex, newBlockIndex, LEFT)) ;
					}
				} catch (NullPointerException e) {}
			} catch (NullPointerException e) {}
		}
		
		Log.i ("SolveHillClimbing", "Fail") ;
		
		return null ;
	}
	
	// 주어진 방향으로 이동한 보드를 Return
	private Board moveDirection (Board orgBoard, BlockIndex dstBlockIndex, BlockIndex orgBlockIndex, int DIRECTION) {
		
		// 새로운 객체 생성하여 이동후에 정보 저장
		Board board =null ;
					
		// 이동 가능한 방향일 경우
		if (super.isIndexBoundCheck(dstBlockIndex)) {
			
			board =new Board (size) ;
			board.setBoardInitial() ;
			board.setPath(orgBoard.getPath()) ;
			board.setPrevDirection(DIRECTION) ;
			board.setTotalMovedHeuristic(orgBoard.getTotalMovedHeuristic() +1) ;
			super.createMovedBoard(board, orgBoard, super.getSize (), dstBlockIndex, orgBlockIndex) ;
		}

		return board ;
	}
}
