package com.ai.slidingpuzzle.basic;

import com.ai.slidingpuzzle.BlockIndex;

public final class DIRECT {
	public final static int DIRECT_SIZE =4 ;
	public final static BlockIndex[] direct ={
		// TOP
		new BlockIndex (-1, 0), 
		// BOTTOM
		new BlockIndex (1, 0),
		// LEFT
		new BlockIndex (0, -1),
		// RIGHT
		new BlockIndex (0, 1)} ;
}
