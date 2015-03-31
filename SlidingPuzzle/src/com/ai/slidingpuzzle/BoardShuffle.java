package com.ai.slidingpuzzle;

import java.util.Random;

import com.ai.slidingpuzzle.basic.DIRECT;

public class BoardShuffle extends BoardMove {

	private Board board ;
	
	public BoardShuffle (Board board, BoardSize size) {
		super (size) ;
		
		this.board =board ;
	}
	
	// Block Shuffle
	public BlockIndex[] setBoardShuffle () {
		
		Random rand =new Random () ;
		
		int row =getSize ().getRow(),
				col =getSize ().getCol() ;
		// 난수 발생 (row *col) ~ (row * col) *2
		int moveCnt =rand.nextInt(row *col) +(row *col) ;
		
		// 이동할 방향과 이동했던 방향
		BlockIndex prevIndex =new BlockIndex(),
				moveIndex =new BlockIndex(),
				emptyIndex =board.getEmptyBlockIndex() ;
		// path
		BlockIndex[] pathIndex =new BlockIndex[moveCnt +1] ;
		// 첫 경로에는 공백의 Index
		pathIndex[0] =new BlockIndex (emptyIndex) ;
		
		// MoveSwap
		for (int i =1 ; i <= moveCnt ;) {
			
			int randIndex =rand.nextInt(DIRECT.DIRECT_SIZE) ;
			moveIndex.setIndex(emptyIndex.getRow() +DIRECT.direct[randIndex].getRow(),
					emptyIndex.getCol() +DIRECT.direct[randIndex].getCol()) ;
			
			// 이동할 수 없는 방향일 때
			// 이동할 방향이 이전의 Index라면 다시 방향 정하기
			if (!super.isIndexBoundCheck(moveIndex)
					|| prevIndex.equalIndex(moveIndex)) continue ;
			
			// 이전의 Index가 아니고 이동 가능할 때
			// Block Swap
			super.blockSwap(board.getBlock(emptyIndex), board.getBlock(moveIndex)) ;
			// 이동전의 위치 
			prevIndex.setIndex(emptyIndex) ;
			// 공백 Index Update
			emptyIndex.setIndex(moveIndex) ;
			// 이동 경로 저장
			pathIndex[i++] =new BlockIndex (emptyIndex) ;
		}
		
		return pathIndex ;
	}
}
