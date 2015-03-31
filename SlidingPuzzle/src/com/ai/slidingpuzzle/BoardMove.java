package com.ai.slidingpuzzle;

public class BoardMove extends Board {

	public BoardMove(BoardSize size) {
		super(size);
	}
	
	// Block Swap
	public static void blockSwap (Block blockA, Block blockB) {
			
		Block tempBlock =new Block () ;
			
		// tempBlock <- blockB
		tempBlock.setBlock(blockB.getState(), blockB.getImage()) ;
		// blockB <- blockA
		blockB.setBlock(blockA.getState(), blockA.getImage()) ;
		// blockA <- tempBlock
		blockA.setBlock(tempBlock.getState(), tempBlock.getImage()) ;
	}
	
	// Block of Board  Swap
	public static void blockSwap (Board boardA, Board boardB, BlockIndex indexA, BlockIndex indexB, BoardSize size) {
		
		Block tempBlock =new Block (),
				blockA =boardA.getBlock(indexA),
				blockB =boardB.getBlock(indexB) ;
		
		// tempBlock <- blockB
		tempBlock.setBlock(blockB.getState(), blockB.getImage()) ;
		// blockB <- blockA
		blockB.setBlock(blockA.getState(), blockA.getImage()) ;
		// blockA <- tempBlock
		blockA.setBlock(tempBlock.getState(), tempBlock.getImage()) ;
	}
		
	// 이동했을 때의 새로운 보드 생성
	public void createMovedBoard (Board dstBoard, Board orgBoard, BoardSize size, BlockIndex dstBlockIndex, BlockIndex orgBlockIndex) {
		
		for (int i =0 ; i < size.getRow() ; i++) {
			for (int j =0 ; j < size.getCol() ; j++) {
				
				if (orgBlockIndex.getRow() == i 
						&& orgBlockIndex.getCol() == j) {
					
					// DeepCopy orgBoard to dstBoard
					dstBoard.getBlock(orgBlockIndex).setBlock(orgBoard.getBlock(dstBlockIndex).getState(),
							orgBoard.getBlock(dstBlockIndex).getImage()) ;
				}
				else if (dstBlockIndex.getRow() == i 
						&& dstBlockIndex.getCol() == j) {
					// DeepCopy orgBoard to dstBoard
					dstBoard.getBlock(dstBlockIndex).setBlock(orgBoard.getBlock(orgBlockIndex).getState(),
							orgBoard.getBlock(orgBlockIndex).getImage()) ;
				}
				else {
					// Deep Copy
					dstBoard.getBlock(i, j).setBlock(orgBoard.getBlock(i, j).getState(),
							orgBoard.getBlock(i, j).getImage()) ;
				}
			}
		}
	}
}
