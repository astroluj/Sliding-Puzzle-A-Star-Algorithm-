package com.ai.slidingpuzzle;

import java.util.ArrayList;
import java.util.PriorityQueue;

import com.ai.slidingpuzzle.basic.DIRECT;

import android.util.Log;

public class SolveAStar extends BoardMove {

	private Board board ;
	private BoardSize size ;

	// 직전에 움직인 방향의 반대로 움직이면 안됨
	private int[] PREV_DIRECT ={2, 1, 4, 3} ;
	private PriorityQueue<Board> que ;
	// closedBoard를 담을 리스트
	private ArrayList<Board> closedList ;
			
	public SolveAStar (Board board, BoardSize size) {
		super (size) ;
		
		this.board =new Board (size) ;
		this.board.setBoardInitial() ;
		super.createMovedBoard(this.board, board, super.getSize (),
				board.getEmptyBlockIndex(), board.getEmptyBlockIndex()) ;
		this.size =this.board.getSize() ;
		
		// Starting Point
		que =new PriorityQueue<Board>() ;
		que.add (this.board) ;
		closedList =new ArrayList<Board> () ;
	}

	// 큐에서 우선순위로 큐를 빼서
	// 움직일 수 있는 방향으로 움직인 보드를 다시 넣음
	public ArrayList <BlockIndex> startSolve () {
		 
		if (que.isEmpty()) return null ;
			
		try {
			// Poll
			Board board =que.poll () ;
					
			// Set ClosedPath
			closedList.add (board) ;
				
			// Empty Index Add
			board.addPath(board.getEmptyBlockIndex()) ;

			// 블록들이 모두 원래 자리에 있다면
			if (board.getMatchHeuristic(board.getBlock()) == board.getMatchedBlocks()) {
			//if (board.getMoveHeuristic(board.getBlock()) == 0) {
				Log.i ("SolveAStar", "Success") ;
				que.removeAll(que) ;
								
				return board.getPath() ;
			}
			
			// Get EmptyBlock Index
			BlockIndex blockIndex =board.getEmptyBlockIndex() ;
			
			// PrevDirection
			int prevDirection =board.getPrevDirection() ;
			for (int i =0 ; i < DIRECT.DIRECT_SIZE ; i++) {
				try {
					
					// 이전에 있던 곳으로 이동하지 않도록
					//PREV_DIRECT ->TOP =1 BOTTOM =2 LEFT =3 RIGHT =4
					if (prevDirection !=  i +1) {
						BlockIndex newBlockIndex =new BlockIndex (
								blockIndex.getRow() +DIRECT.direct[i].getRow(),
								blockIndex.getCol() +DIRECT.direct[i].getCol()) ;
						que.add(moveDirection(board, newBlockIndex, blockIndex, PREV_DIRECT[i])) ;
					}
				} catch (NullPointerException e) {}
			}
		} catch (Exception e) {
			return null ;
		}
		
		return new ArrayList<BlockIndex> () ;
	}
	
	// 주어진 방향으로 이동한 보드를 Return
	private Board moveDirection (Board orgBoard, BlockIndex dstBlockIndex, BlockIndex orgBlockIndex, int DIRECTION) {
		
		// 새로운 객체 생성하여 이동후에 정보 저장
		Board board =null ;
					
		// 이동 가능한 방향일 경우
		if (super.isIndexBoundCheck(dstBlockIndex)) {
			
			board =new Board (size) ;
			board.setBoardInitial() ;
			board.setPrevDirection(DIRECTION) ;
			board.setPath(orgBoard.getPath());

			super.createMovedBoard(board, orgBoard, super.getSize (), dstBlockIndex, orgBlockIndex) ;
			
			// Checking ClosedBoard
			for (Board closeBoard : this.closedList) {
				if (board.isEqualBoard(closeBoard)) {
					board =null ;
					
					break ;
				}
			}
		}

		return board ;
	}
}