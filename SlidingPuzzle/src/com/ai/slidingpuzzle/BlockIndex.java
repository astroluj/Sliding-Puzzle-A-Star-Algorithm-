package com.ai.slidingpuzzle;

public class BlockIndex {

	private int row, col ;
	
	// 생성자
	public BlockIndex () {
		this.row =0 ;
		this.col =0 ;
	}
	public BlockIndex (int row, int col) {
		this.row =row ;
		this.col =col ;
	}
	public BlockIndex (BlockIndex blockIndex) {
		this.row =blockIndex.getRow() ;
		this.col =blockIndex.getCol() ;
	}
	
	// isEqual
	public boolean equalIndex (BlockIndex index) {
		return (this.row == index.row
				&& this.col == index.col)  ? true : false ;
	}
	public boolean equalIndex (int row, int col) {
		return (this.row == row
				&& this.col == col)  ? true : false ;
	}
	
	// Set
	public void setIndex (BlockIndex index) {
		this.row =index.row ;
		this.col =index.col ;
	}
	public void setIndex (int row, int col) {
		this.row =row ;
		this.col =col ;
	}
	public BlockIndex getIndex () {
		return this ;
	}
	
	// Block to Blocks cost
	public int getIndexToIndexCost (int row, int col) {
		return Math.abs(this.row -row) +Math.abs(this.col -col) ;
	}
	public int getIndexToIndexCost (BlockIndex blockIndex) {
		return Math.abs(this.row -blockIndex.getRow()) +Math.abs(this.col -blockIndex.getCol()) ;
	}
	
	public void setRow (int row) {
		this.row =row ;
	}
	public void setCol (int col) {
		this.col =col ;
	}
	public int getRow () {
		return this.row ;
	}
	public int getCol () {
		return this.col ;
	}
}

