package com.ai.slidingpuzzle;


import android.graphics.Bitmap;

public class Block {

	// 원래 가져야 할 State
	private int goalState ;
	// 현재 가지고 있는 State 
	private int thisState ;
	// 현재 가지고 있는 ImageInfo
	private Bitmap thisImg ;
	// 4방향 Block Info
	private Block leftBlock, rightBlock, topBlock, bottomBlock ;
	
	// 생성자
	public Block () {}
	public Block (int goalState) {
		this.goalState =goalState ;
		this.thisState =0 ;
		this.thisImg =null ;
	}
	public Block (int goalState, int thisState) {
		this.goalState =goalState ;
		this.thisState =thisState ;
		this.thisImg =null ;
	}
	public Block (int goalState, int thisState, String text) {
		this.goalState =goalState ;
		this.thisState =thisState ;
		this.thisImg =null ;
	}
	public Block (int goalState, int thisState, Bitmap img) {
		this.goalState =goalState ;
		this.thisState =thisState ;
		this.thisImg =img ;
	}
	
	// GoalState와 ThisState가 일치하는지 여부
	public boolean isGoalThisMatched () {
		return (this.goalState == this.thisState) ;
	}
	
	// Goal State
	public void setGoalState (int goalState) {
		this.goalState =goalState ;
	}
	public int getGoalState () {
		return this.goalState ;
	}
	
	// This Imgage
	public void setImage (Bitmap thisImg) {
		this.thisImg =thisImg ;
	}
	public Bitmap getImage () {
		return this.thisImg ;
	}
	
	// This State
	public void setState (int thisState) {
		this.thisState =thisState ;
	}
	public int getState () {
		return this.thisState ;
	}
	
	// SetBlock Info
	public void setBlock (int thisState) {
		this.thisState =thisState ;
	}
	public void setBlock (int thisState, Bitmap img) {
		this.thisState =thisState ;
		this.thisImg =img ;
	}

	// Return Blocks
	// LeftBlock
	public void setLeftBlock (Block block) {
		this.leftBlock =block ;
	}
	public Block getLeftBlock () {
		return this.leftBlock ;
	}
	
	// RightBlock
	public void setRightBlock (Block block) {
		this.rightBlock =block ;
	}
	public Block getRightBlock () {
		return this.rightBlock ;
	}
	
	// TopBlock
	public void setTopBlock (Block block) {
		this.topBlock =block ;
	}
	public Block getTopBlock () {
		return this.topBlock ;
	}
	
	// BottomBlock
	public void setBottomBlock (Block block) {
		this.bottomBlock =block ;
	}
	public Block getBottomBlock () {
		return this.bottomBlock ;
	}
}
