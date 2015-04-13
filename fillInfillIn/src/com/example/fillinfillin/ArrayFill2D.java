package com.example.fillinfillin;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.BitmapFactory.Options;
import android.graphics.Paint.Cap;
import android.util.Log;
import android.view.View;

// fillBase에 대한 클래스
public class ArrayFill2D extends View {
	
	MainActivity mainAct ;
	
	
	protected int[][] bitmapId ;	// fillBase Resource 배열
	protected static ArrayList<Data> dataList, comDataList ;
	protected static int checkCnt ;
	protected static boolean[][] coord ;	// flood 상태
	
	private final int WIDTH =mainAct.FILL_WIDTH, HEIGHT =mainAct.FILL_HEIGHT,
			SCALE_W =mainAct.scaleWidth, SCALE_H =mainAct.scaleHeight,
			MATRIX =mainAct.FILL_MATRIX, SHAPE =mainAct.FILL_SHAPE_COUNT ;
	private int[] imgId = { R.drawable.blue, R.drawable.red, R.drawable.green, R.drawable.cyan,
			R.drawable.gray, R.drawable.purple, R.drawable.yellow };	// fill그림 
	private int w =WIDTH, fillCnt, width, height ;
	private BitmapFactory.Options resizeOption ;
	private Data[] fillList ;
	private Bitmap[] img ;
	private Bitmap pattern ;
	private boolean comFlag ;
	
	// 생성자
	public ArrayFill2D(Context context, boolean comFlag)
	{
		super(context);
		width =SCALE_W /WIDTH ;
		height =(int)(SCALE_H /10 *7.5 /HEIGHT) ;
		resizeOption =new Options () ;
		resizeOption.inSampleSize =2 ;
		this.comFlag =comFlag ;
		
		if (comFlag) {
			height =(int)((SCALE_H -SCALE_H /8) /10 *7.5 /HEIGHT) ;
			w =WIDTH /2 ;
		}
		
		img =new Bitmap[SHAPE] ;
		for (int i =0 ; i < SHAPE ; i++) {
			img[i] = BitmapFactory.decodeResource(getResources (),
					imgId[i], resizeOption) ;
				img[i] =Bitmap.createScaledBitmap(img[i], width, height, true) ;
		}
		pattern =BitmapFactory.decodeResource(getResources(), R.drawable.pattern, resizeOption) ;
		pattern =Bitmap.createScaledBitmap(pattern, SCALE_W, height *HEIGHT, true) ;
	}
	
	public void setRandomImg ()
	{
		bitmapId =new int[HEIGHT][WIDTH] ;
		coord =new boolean[HEIGHT][WIDTH] ;
		coord[0][0] =true ;	// (0, 0)은 항상 true
		dataList =new ArrayList<Data> () ;
		fillList =new Data[MATRIX] ;
		comDataList =new ArrayList<Data> () ;
		Data data =new Data () ;
		dataList.add(data.clone()) ;
		data.setData(0, w) ;
		comDataList.add(data.clone()) ;
		fillCnt =1 ;
		checkCnt =1 ;

		// 불러올 이미지를 4배 축소하여 불러오도록 설정
		for (int i =0 ; i < HEIGHT ; i++) {
			for (int j =0 ; j < WIDTH ; j++) {
				fillList[(i *WIDTH) +j] =new Data () ;
				// 이미지를 랜덤하게 넣음
				bitmapId[i][j] =imgId[(int)(Math.random () *SHAPE)] ;
			}
		}
	}
	
	// 비교하여 바꾸기
	protected void changeFill(int orgImg)
	{	
		checkFill () ;	// (0, 0)부터 탐색
		for (int i =0 ; i < fillCnt ; i++) 
			bitmapId[fillList[i].getRow()][fillList[i].getCol()] =orgImg ;	// ResourceId로 (0, 0)의 Id로 변경
	}
		
	// (0, 0)부터 같은색의 범위 찾기
	protected void checkFill () 
	{	
		int index =0 ;
		Data data =new Data (), tempData =new Data () ;
		
		while (dataList.size() > 0) {
			int cnt =0, row, col ;
			data =dataList.get(index) ;		
			row =data.getRow() ; col =data.getCol() ;
				
			if (col +1 < w && !coord[row][col +1]) {
				if (bitmapId[0][0] == bitmapId[row][col +1]) {

					coord[row][col +1] =true ;
					cnt++ ;
					tempData.setData(row, col +1) ;
					dataList.add(tempData.clone()) ;
					fillList[fillCnt++].setData(tempData.clone()) ;
					checkCnt++ ;
				}
			} else cnt++ ;
			
			if (row +1 < HEIGHT && !coord[row +1][col]) {
				if (bitmapId[0][0] == bitmapId[row +1][col]) {

					coord[row +1][col] =true ;
					cnt++ ;
					tempData.setData(row +1, col) ;
					dataList.add (tempData.clone()) ;
					fillList[fillCnt++].setData(tempData.clone()) ;
					checkCnt++ ;
				}
			} else cnt++ ;

			if (col > 0 && !coord[row][col -1]) {	
				if (bitmapId[0][0] == bitmapId[row][col -1]) {
				
					coord[row][col -1] =true ;
					cnt++ ;
					tempData.setData (row, col -1) ;
					dataList.add(tempData.clone()) ;
					fillList[fillCnt++].setData(tempData.clone()) ;
					checkCnt++ ;
				}
			} else cnt++ ;
		
			if (row > 0 && !coord[row -1][col]) {
				if (bitmapId[0][0] == bitmapId[row -1][col]) {
		
					coord[row -1][col] =true ;
					cnt++ ;
					tempData.setData(row -1, col) ;
					dataList.add(tempData.clone()) ;
					fillList[fillCnt++].setData(tempData.clone()) ;
					checkCnt++ ;
				}
			} else cnt++ ;

			if (cnt == 4) dataList.remove(index) ;
			else if (index +1 == dataList.size ()) break ;
			else index++ ;
				
			if (index >= dataList.size ()) break ;
		}
	}
	
	public void recycle ()
	{
		for (int i =0 ; i < SHAPE ; i++)
			img[i].recycle() ;
		pattern.recycle() ;
	}
	
	public void onDraw (Canvas canvas)
	{
		super.onDraw(canvas) ;
		
		for (int i = 0; i < HEIGHT; i++) {
			for (int j = 0; j < WIDTH; j++) {
				for (int k =0 ; k < SHAPE ; k++) {
					if (bitmapId[i][j] == imgId[k]) {
						canvas.drawBitmap (img[k], j *width, i *height, null) ; // 들어갈 좌표를 입력하여 그림
						break ;
					}
				}
			}
		}
		canvas.drawBitmap(pattern, 0, 0, null);

		if (comFlag) {
			Paint paint = new Paint();
			paint.setColor(0xcc9933ff);
			paint.setStrokeWidth(7);
			paint.setAlpha(0xff);
			// 세로 선
			canvas.drawLine(SCALE_W / 2, 0, SCALE_W / 2,
					(int)(SCALE_H /10 *7.5 /HEIGHT) * HEIGHT, paint);
			paint.setStrokeCap(Cap.ROUND);
			// 가로 선
			canvas.drawLine(0, height * HEIGHT, SCALE_W, height
					* HEIGHT, paint);
		}
	}
}