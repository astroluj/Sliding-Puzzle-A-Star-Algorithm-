package com.ai.slidingpuzzle.layout;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ai.slidingpuzzle.Block;
import com.ai.slidingpuzzle.Board;
import com.ai.slidingpuzzle.basic.Scale;

public class ImageLayout {

	// View Setting
	public static void initImageView (Scale scale, final Board board, 
			RelativeLayout imgRelativeLayout, ImageView[][] imgView) {
		
		// ImgRelativeLayout 
		// width 60%, height 100% 
		int row =board.getSize().getRow(), col =board.getSize().getCol(),
				width =(int)((scale.getScaleWidht() /100 *60) -(imgRelativeLayout.getLeft() *2)),
				height =(int)(scale.getScaleHeight() -(imgRelativeLayout.getTop() *2));

		// Remove All ChildView
		imgRelativeLayout.removeAllViews() ;
	
		for (int i =0 ; i < row ; i++) {
			for (int j =0 ; j < col ; j++) {
				
				// 보드에 이미지 삽입
				Block block =board.getBlock(i,  j) ;
				
				// 블록의 이미지를 가져와서 원하는 사이즈로 ReSize
				Bitmap img =block.getImage() ;
				img =Bitmap.createScaledBitmap(img, width /col, height /row, true) ;
				// 블록에 ReSizing한 이미지를 재 삽입
				block.setImage(img);
				
				imgView[i][j].setImageBitmap(img) ;
				
				// set ChildView
				imgRelativeLayout.addView(imgView[i][j]) ;
			}
		}
		imgRelativeLayout.invalidate() ;
	}
	
	// View Shuffle
	public static void setImageView (Board board, RelativeLayout imgRelativeLayout, ImageView[][] imgView) {
		
		try {
			
			// Set ImageView
			for (int i =0, row =board.getSize().getRow(), col =board.getSize().getCol() ; i < row ; i++) 
				for (int j =0 ; j < col ; j++) 
					imgView[i][j].setImageBitmap(board.getBlock(i, j).getImage());
				
			imgRelativeLayout.invalidate();
		} catch (Exception e) {}
	}
}
