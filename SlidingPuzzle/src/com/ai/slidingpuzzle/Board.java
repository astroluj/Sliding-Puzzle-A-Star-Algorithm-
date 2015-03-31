package com.ai.slidingpuzzle;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class Board extends Block implements Comparable<Board> {

	// EmptyBlock State
	public final int EMPTY_BLOCK =0 ;
	// 최대 이동 횟수
	public int MAX_MOVED ;
	// 이전 블록에서 이동했었던 방향
	private int prevDirection ;
	// 전체 이동 횟수
	private int totalMovedCnt ;
	// SuccessMatchBlocks
	private int MATCH ;
	// Size
	private BoardSize size ;
	// row X col Blocks
	private Block[][] blocks ;
	// Board Image
	private Bitmap[] bitImgs ;
	// Path
	private ArrayList<BlockIndex> pathList ;
	
	// 생성자
	public Board (BoardSize size) {
		super () ;
		
		this.size =size.getSize() ;
		MATCH =size.getRow() *size.getCol() ;
		MAX_MOVED =getSize().getRow() *getSize().getCol() *2 ;
	}
	
	// Block Connect
	protected void setBlockConnect (Block[][] blocks) {

		int row =getSize ().getRow(),
				col =getSize ().getCol() ;
		
		for (int i =0 ; i < row ; i++) {	
			for (int j=0 ; j < col ; j++) {
				
				// Top
				blocks[i][j].setTopBlock(
						(isIndexBoundCheck (new BlockIndex (i -1, j))) ? blocks[i-1][j] : null) ;
				// Bottom
				blocks[i][j].setBottomBlock(
						(isIndexBoundCheck (new BlockIndex (i +1, j))) ? blocks[i+1][j] : null) ;
				// Left
				blocks[i][j].setLeftBlock(
						(isIndexBoundCheck (new BlockIndex (i, j -1))) ? blocks[i][j-1] : null) ;
				// Right
				blocks[i][j].setRightBlock(
						(isIndexBoundCheck (new BlockIndex (i, j +1))) ? blocks[i][j+1] : null) ;
			}
		}
	}
	
	// 현재 일치하는 블록의 갯수
	protected int getMatchHeuristic (Block[][] blocks) {
		
		int heuristicCnt =0 ;
		
		for (int i =0 ; i < getSize ().getRow() ; i++) {
			for (int j =0 ; j < getSize ().getCol() ; j++) {
				
				if (blocks[i][j].isGoalThisMatched()) heuristicCnt++ ;
			}
		}
		
		return heuristicCnt ;
	}
	
	// 현재 일치하지 않는 블록의 갯수
	protected int getUnMatchHeuristic (Block[][] blocks) {
		
		return this.MATCH -getMatchHeuristic(blocks) ;
	}
	
	// 각 블록들이 원래 자리로 이동하기 위한 최소 이동 횟수와 일치 하지 않은 블록의 합
	protected int getHeuristic (Block[][] blocks) {
		
		int heuristicCnt =0 ;
		
		// 가는데 이동할 블록의 갯수알아내기
		BlockIndex goalIndex =new BlockIndex () ;
		
		for (int i =0, state =0 ; i < getSize ().getRow() ; i++) {
			for (int j =0 ; j < getSize ().getCol() ; j++) {
				
				state =blocks[i][j].getState() -1;
				if (state < 0) state =this.MATCH -1 ;
				
				goalIndex.setIndex(state /getSize ().getCol(), state %getSize ().getCol()) ;
				heuristicCnt +=goalIndex.getIndexToIndexCost(i,  j) ;
				if (!blocks[i][j].isGoalThisMatched()) heuristicCnt++ ;
			}
		}
		
		return heuristicCnt ;
	}

	// 공백 블록의 위치	
	public BlockIndex getEmptyBlockIndex () {
		
		BlockIndex blockIndex =null ;
		
		for (int i =0 ; i < getSize().getRow() ; i++) {
			for (int j =0 ; j < getSize ().getCol() ; j++) {
				
				if (this.blocks[i][j].getState() == EMPTY_BLOCK) {
					blockIndex =new BlockIndex (i, j) ;
					
					return blockIndex ;
				}
			}
		}
		
		return blockIndex ;
	}
	
	// Block Initial
	public void setBoardInitial () {
				
		int row =this.size.getRow(),
				col =this.size.getCol() ;
				
		// SetGoalState
		this.blocks =new Block[row][col] ;
		for (int i =0, goalState =0 ; i < row ; i++) {
			// goalState is 1,2,3,4...(0)
			for (int j =0 ; j < col ; j++) {
						
				goalState =(i *col +(j+1))%(row*col) ;
					
				if (this.bitImgs != null)
					this.blocks[i][j] =new Block (goalState, goalState, getImage(goalState)) ;
				else this.blocks[i][j] =new Block (goalState, goalState) ;
			}
		}
				
		// 블록의 4방향 연결
		setBlockConnect (blocks) ;
	}
		
	// Indexing Bound Check
	public boolean isIndexBoundCheck (BlockIndex blockIndex) {
			
		return (0 <= blockIndex.getRow() && blockIndex.getRow() < getSize ().getRow()
				&& 0 <= blockIndex.getCol() && blockIndex.getCol() < getSize ().getCol()) ? true : false ;
	}
	
	// Compare Closed Board
	public boolean isEqualBoard (Board board) {
		
		// 현재 보드가 Closed 되었던 Board 였는지 검사
		for (int i =0 ; i < getSize().getRow() ; i++) {
			for (int j =0 ; j < getSize().getCol() ; j++) {
				
				// 블록이 같지 않으면 openBoard라고 알림
				if (this.blocks[i][j].getState() != board.blocks[i][j].getState())
					return false ;
			}
		}
		
		return true ;
	}
	
	// Empty Block Path DeepCopy
	public void setPath (ArrayList<BlockIndex> pathList) {

		// pathList가 NULL이면 생성
		if (this.pathList == null) this.pathList =new ArrayList <BlockIndex> () ;
		// DeepCopy
		this.pathList.addAll (pathList) ;
	}
		
	// Empty Block Path Add
	public void addPath (BlockIndex blockIndex) {
			
		// pathList가 NULL이면 생성
		if (this.pathList == null) this.pathList =new ArrayList <BlockIndex> () ;
		this.pathList.add (blockIndex) ;
	}
		
	public ArrayList<BlockIndex> getPath () {
		
		ArrayList<BlockIndex> newPathList =new ArrayList<BlockIndex> () ;
		newPathList.addAll(this.pathList) ;
		
		return newPathList ;
	}
		
	// Image
	public void setImage (Bitmap[] bitImgs) {
		this.bitImgs =bitImgs ;
	}
	public Bitmap getImage (int index) {
		return this.bitImgs[index] ;
	}
	
	// Block
	public Block[][] getBlock () {
		return this.blocks ;
	}
	public Block getBlock (int row, int col) {
		return blocks[row][col] ;
	}
	public Block getBlock (BlockIndex blockIndex) {
		return blocks[blockIndex.getRow()][blockIndex.getCol()] ;
	}
	
	// Size
	public void setSize (BoardSize size) {
		this.size.setSize (size.getSize()) ;
	}
	public BoardSize getSize () {
		return this.size ;
	}
	
	// Previous Direction
	public void setPrevDirection (int prevDirection) {
		this.prevDirection =prevDirection ;
	}
	public int getPrevDirection () {
		return this.prevDirection ;
	}
	
	// Total Moved Count
	public void setTotalMovedHeuristic (int totalMovedCnt) {
		this.totalMovedCnt =totalMovedCnt ;
	}
	public int getTotalMovedHeuristic () {
		return this.totalMovedCnt ;
	}
	
	// MATCHED Blocks
	public int getMatchedBlocks () {
		return this.MATCH ;
	}
	// CompareTo
	public int compareTo (Board board) {
		
		// 내림 차순 > 오름차순 <
		return (getHeuristic(this.blocks)
				<= board.getHeuristic(board.getBlock())) 
				? -1 : 1 ;
	}
}
