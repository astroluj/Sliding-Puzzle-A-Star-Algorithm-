package com.ai.slidingpuzzle;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class BoardImage extends Board {

	private Bitmap[] bitImgs ;
	private Bitmap bitImg ;
	
	public BoardImage (BoardSize size, Bitmap img) {
		super (size) ;
		
		this.bitImg =img ;
		
		setBoardImage(this.bitImg) ;
	}
	
	// Image Slice
	private void setImageSlice (Bitmap img) {
		
		// Block Image Size
		int width =(int)(img.getWidth() /getSize ().getCol()), 
				height =(int)(img.getHeight() /getSize ().getRow()),
				row =getSize ().getRow(),
				col =getSize ().getCol() ;
		
		this.bitImgs =new Bitmap[row *col] ;
		for (int i =0, index =0 ; i < row ; i++) {
			for (int j =0 ; j < col ; j++) {
			
				// 1부터 넣어서 0 index는 마지막 사진 포함
				try {
					index =(i *col +(j+1))%(row*col) ;
					this.bitImgs[index] =setRectSide (img, width *j, height *i, width, height) ;
				} catch (IndexOutOfBoundsException e) {}
			}
		}
		// 0 Index is Empty
		this.bitImgs[super.EMPTY_BLOCK] =Bitmap.createBitmap(
				width, height, Config.ARGB_8888) ;
	}
	
	// 테두리 친 그림
	private Bitmap setRectSide (Bitmap img, int x, int y, int width, int height) {
		
		img =Bitmap.createBitmap(img, x, y, width, height) ;
		Canvas canvas =new Canvas (img) ;
		
		Rect rect =new Rect (0, 0, img.getWidth(), img.getHeight()) ;
		Paint paint =new Paint () ;
		paint.setColor(Color.RED) ;
		paint.setAntiAlias(true) ;
		paint.setStyle(Paint.Style.STROKE) ;
		paint.setStrokeWidth(2) ;
		// 테두리 그리기
		canvas.drawRect(rect, paint) ;
	
		return img ;
	}
	
	// Set this
	public void setBoardImage (Bitmap img) {
		
		setImageSlice (img) ;
	}
	// Return this
	public Bitmap[] getBoardImage () {
		return this.bitImgs ;
	}
}
