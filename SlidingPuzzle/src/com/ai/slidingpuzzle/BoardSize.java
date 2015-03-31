package com.ai.slidingpuzzle;

public class BoardSize {

	private int row, col ;
	
	public BoardSize (int row, int col) {
		this.row =row ;
		this.col =col ;
	}
	
	public boolean equalSize (int row, int col) {
		return (this.row == row && this.col == col) ? true : false ;
	}
	public boolean equalSize (BoardSize size) {
		return (this.row == size.row && this.col == size.col) ? true : false ;
	}
	
	public void setSize (BoardSize size) {
		this.row =size.row ;
		this.col =size.col ;
	}
	public BoardSize getSize () {
		return this ;
	}
	public int getRow () {
		return this.row ;
	}
	public int getCol () {
		return this.col ;
	}
}
