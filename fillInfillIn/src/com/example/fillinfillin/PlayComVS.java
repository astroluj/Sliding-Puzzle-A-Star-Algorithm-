package com.example.fillinfillin;

import java.nio.ByteBuffer;
import java.util.Arrays;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PlayComVS extends Activity {

	MainActivity mainAct ;
	ArrayFill2D arrFill ;
	TimerThread timerThread ;
	ReplayThread replayThread ;
	TimerComThread timerComThread ;
	Etc etc ;
	
	private final int WIDTH =mainAct.FILL_WIDTH, HEIGHT =mainAct.FILL_HEIGHT,
			MATRIX =mainAct.FILL_MATRIX /2, COUNT =mainAct.FILL_CLICK_COUNT,
			TIME =mainAct.FILL_PLAY_TIME, SHAPE =mainAct.FILL_SHAPE_COUNT ;
	private int[][] colorCnt ={{0, R.drawable.blue}, {0, R.drawable.red}, {0, R.drawable.green},	// com 어라운드 체크
			{0, R.drawable.cyan}, {0, R.drawable.gray}, {0, R.drawable.purple}, {0, R.drawable.yellow}} ;
	private ProgressBar timebar ;	// 타이머
	private int SECOND =2000, clickCnt =COUNT ; 	// 난이도 별 초와 클릭 제한수
	private ImageButton btnReplay, btnBGsound, btnRed, btnBlue, btnGreen, btnCyan, btnGray, btnPurple, btnYellow ;
	private TextView textCnt ;
	private ImageView animTarget ;
	private RelativeLayout relativeMidLay ;
	private boolean colorFlag ;

	private Handler playHandler =new Handler () {
		public void handleMessage (Message msg) 
		{
			super.handleMessage(msg) ;
			switch (msg.what) {
			case 0 :
				timebar.incrementProgressBy(-1) ;
				if (timebar.getProgress() == 0) {
					replayClick (btnReplay) ;
				}
				break ;
			case 1 :
				clickCnt = COUNT; // 카운트 초기화

				arrFill.setRandomImg();
				relativeMidLay.invalidate(); // 화면을 다시 온드로우함

				textCnt.setText(clickCnt + "");

				timerCancel();
				timerStart();
				mainAct.replayFlag =false ;
				mainAct.playFlag =false ;
				mainAct.cancelFlag =false ;
				break ;
			case 2 :
				timerCancel();
				finish(); // 현재 Activity를 종료
				mainAct.playFlag =false ;
				mainAct.cancelFlag =false ;
				break ;
			case 3:
				comChangeFill () ;
			}
		}
	} ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView (R.layout.play) ;

		relativeMidLay = (RelativeLayout) findViewById(R.id.playMidLayout);
		arrFill =new ArrayFill2D (PlayComVS.this, true) ;
		arrFill.setRandomImg() ;
		relativeMidLay.addView(arrFill) ;	// fillBase를 채운다.

		//Bundle extras =getIntent ().getExtras() ;	// MainActivity에서 Intent로 넘겨받은 값을 받음	
		textCnt =(TextView) findViewById (R.id.cnt) ;
		btnReplay =(ImageButton) findViewById (R.id.replay) ;
		btnRed =(ImageButton) findViewById (R.id.red) ;
		btnBlue =(ImageButton) findViewById (R.id.blue) ;
		btnGreen =(ImageButton) findViewById (R.id.green) ;
		btnCyan =(ImageButton) findViewById (R.id.cyan) ;
		btnGray =(ImageButton) findViewById (R.id.gray) ;
		btnPurple =(ImageButton) findViewById (R.id.purple) ;
		btnYellow =(ImageButton) findViewById (R.id.yellow) ;
		btnBGsound =(ImageButton) findViewById (R.id.bgSound2) ;
		timebar =(ProgressBar) findViewById (R.id.timebar) ;	
		animTarget =(ImageView) findViewById (R.id.time) ;
		
		mainAct.replayFlag =false ;
		
		etc =new Etc (PlayComVS.this) ;
		etc.Clicked(textCnt, btnGray, btnPurple, btnYellow) ;
		etc.Sounds(btnBGsound) ;
		
		if (SHAPE == 5) SECOND =1800 ;
		else if (SHAPE == 7) SECOND =1500 ;
		
		replayThread =new ReplayThread (playHandler) ;
		replayThread.setDaemon(true) ;	
		replayThread.start() ;
		
		timerThread =new TimerThread (PlayComVS.this, playHandler, TIME) ;
		timerThread.setDaemon(true) ;
		timerThread.start() ;
		
		timerComThread =new TimerComThread (playHandler, SECOND) ;
		timerComThread.setDaemon (true) ;
		timerComThread.start() ;
		
		timerStart () ;
	}
	
	// Bitmap과 id 비교
	public boolean compareFill(Bitmap orgImg, int cmpImg)
	{
		/*Drawable temp = orgImg.getDrawable();
		Bitmap tempBit = ((BitmapDrawable) temp).getBitmap();
		
		Drawable temp1 = Play.this.getResources().getDrawable(cmpImg);
		Bitmap tempBit1 = ((BitmapDrawable) temp1).getBitmap();

		if (orgImg.equals(tempBit1)) {
			Toast.makeText(Play.this, "버튼일치", Toast.LENGTH_SHORT).show () ;
			return true;
		} else {
			Toast.makeText(Play.this, "버튼불일치", Toast.LENGTH_SHORT).show () ;
			return false;
		}*/
		
		ByteBuffer buffer1 =ByteBuffer.allocate (orgImg.getHeight() *orgImg.getRowBytes()) ;
		orgImg.copyPixelsToBuffer(buffer1) ;
		
		Drawable temp1 = PlayComVS.this.getResources().getDrawable(cmpImg);
		Bitmap tempBit1 = ((BitmapDrawable) temp1).getBitmap();
		ByteBuffer buffer2 =ByteBuffer.allocate (tempBit1.getHeight() *tempBit1.getRowBytes()) ;
		tempBit1.copyPixelsToBuffer(buffer2) ;
		
		return Arrays.equals (buffer1.array (), buffer2.array ()) ;
	}

	// Bitmap 끼리비교
	public boolean compareFill(Bitmap orgImg, Bitmap cmpImg) 
	{
		// orgImg가 이미지뷰일때
		/*Drawable temp = orgImg.getDrawable();
		Bitmap tempBit = ((BitmapDrawable) temp).getBitmap();
		
		Drawable temp1 = cmpImg.getDrawable();
		Bitmap tempBit1 = ((BitmapDrawable) temp1).getBitmap();

		if (orgImg.equals(cmpImg)) {
			return true;
		} else {
			Toast.makeText(Play.this, "다름",  Toast.LENGTH_SHORT).show () ;
			return false;
		}*/
		
		ByteBuffer buffer1 =ByteBuffer.allocate (orgImg.getHeight() *orgImg.getRowBytes()) ;
		orgImg.copyPixelsToBuffer(buffer1) ;
		
		ByteBuffer buffer2 =ByteBuffer.allocate (cmpImg.getHeight() *cmpImg.getRowBytes()) ;
		cmpImg.copyPixelsToBuffer(buffer2) ;
		
		return Arrays.equals (buffer1.array (), buffer2.array ()) ;
	}


	// 색 버튼 클릭 시
	public void changeClick(final View v) 
	{	
		if (mainAct.soundFlag) mainAct.soundpool.play(mainAct.sound[3], 1, 1, 1, 0, 1) ;
		if (!colorFlag && clickCnt > 0) {
			colorFlag =true ;
			etc.buttonDisabled(btnReplay, btnBGsound, btnRed, btnBlue, 
					btnGreen, btnCyan, btnGray, btnPurple, btnYellow) ;
		playHandler.postDelayed (new Runnable () {
				public void run ()
				{
					textCnt.setText (--clickCnt +"") ;	// 카운트 수를 1줄여서 설정
					
					switch (v.getId()) {	// 각버튼 아이디에 대해 실행
					case R.id.red:
						arrFill.changeFill(R.drawable.red) ;
						break;
					case R.id.blue:
						arrFill.changeFill(R.drawable.blue) ;
						break;
					case R.id.green:
						arrFill.changeFill(R.drawable.green) ;
						break;
					case R.id.cyan:
						arrFill.changeFill(R.drawable.cyan) ;
						break;
					case R.id.gray:
						arrFill.changeFill(R.drawable.gray) ;
						break;
					case R.id.purple:
						arrFill.changeFill(R.drawable.purple) ;
						break;
					case R.id.yellow:
						arrFill.changeFill(R.drawable.yellow) ;
						break;
					}					
					relativeMidLay.invalidate() ;
					arrFill.checkFill() ;
					// 카운트가 0이고 flood가 안 되었을 때 
					if (clickCnt <= 0 && arrFill.checkCnt < MATRIX) replayClick (v) ;
					else if (clickCnt >= 0 && arrFill.checkCnt == MATRIX) {
						if (mainAct.soundFlag) mainAct.soundpool.play(mainAct.sound[1], 1, 1, 1, 0, 1) ;
						
						clickCnt =COUNT ;	// 카운트 초기화
						arrFill.setRandomImg() ;
						textCnt.setText (clickCnt +"") ;
						
						timerCancel () ;
						timerStart () ;
					}
					
					etc.buttonEnabled(btnReplay, btnBGsound, btnRed, btnBlue, 
							btnGreen, btnCyan, btnGray, btnPurple, btnYellow) ;
					colorFlag =false ;
				}
			}, mainAct.D_TAB) ;
		}
		else return ;
	}
	
	protected void comCompareFill (int img, int r, int c)
	{		
		for (int i =0 ; i < SHAPE ; i++) 
			if (img == colorCnt[i][1]) {
				colorCnt[i][0]++ ;
				break ;
			}
	}
	
	protected int comCheckFill () 
	{
		int index =0 ;
		Data data =new Data (), tempData =new Data () ;
		for (int i =0 ; i < SHAPE ; i++) colorCnt[i][0] =0 ;
		
		while (arrFill.comDataList.size() > 0) {
	
			int cnt =0, row, col ;
			data =arrFill.comDataList.get(index) ;		
			row =data.getRow() ; col =data.getCol() ;

			if (col +1 < WIDTH && !arrFill.coord[row][col +1]) {
				if (arrFill.bitmapId[0][WIDTH /2] == arrFill.bitmapId[row][col +1]) {
					
					arrFill.coord[row][col +1] =true ;
					cnt++ ;
					tempData.setData(row, col +1) ;
					arrFill.comDataList.add(tempData.clone()) ;
				}
				else comCompareFill (arrFill.bitmapId[row][col +1], row, col +1) ;
			} else cnt++ ;
			
			if (row +1 < HEIGHT && !arrFill.coord[row +1][col]) {
				if (arrFill.bitmapId[0][WIDTH /2] == arrFill.bitmapId[row +1][col]) {

					arrFill.coord[row +1][col] =true ;
					cnt++ ;
					tempData.setData(row +1, col) ;
					arrFill.comDataList.add (tempData.clone()) ;
				}
				else comCompareFill (arrFill.bitmapId[row +1][col], row +1, col) ;
			} else cnt++ ;

			if (col > WIDTH /2 && !arrFill.coord[row][col -1]) {	
				if (arrFill.bitmapId[0][WIDTH /2] == arrFill.bitmapId[row][col -1]) {
		
					arrFill.coord[row][col -1] =true ;
					cnt++ ;
					tempData.setData (row, col -1) ;
					arrFill.comDataList.add(tempData.clone()) ;
				}
				else comCompareFill (arrFill.bitmapId[row][col -1], row, col -1) ;
			} else cnt++ ;
	
			if (row > 0 && !arrFill.coord[row -1][col]) {
				if (arrFill.bitmapId[0][WIDTH /2] == arrFill.bitmapId[row -1][col]) {

					arrFill.coord[row -1][col] =true ;
					cnt++ ;
					tempData.setData(row -1, col) ;
					arrFill.comDataList.add(tempData.clone()) ;
				}
				else comCompareFill (arrFill.bitmapId[row -1][col], row -1, col) ;
			} else cnt++ ;

			if (cnt == 4) arrFill.comDataList.remove(index) ;
			else if (index +1 == arrFill.comDataList.size ()) break ;
			else index++ ;
			
			if (index >= arrFill.comDataList.size ()) break ;
		}

		int temp =0 ;
		for (int i =1 ; i < SHAPE ; i++) {
			if (i < 2) {
				if (colorCnt[i -1][0] < colorCnt[i][0]) temp =i ;
				else temp =i -1 ;
			}
			else if (colorCnt[temp][0] < colorCnt[i][0]) temp =i ;
		}
		return colorCnt[temp][1] ;
	}
	

	protected void comChangeFill ()
	{
		int comCheckCnt =0 ;
		arrFill.coord[0][WIDTH /2] =true ;	// (0, 0)은 항상 true
		int aroundImg =comCheckFill () ;	// (0, 0)부터 탐색
		
		for (int i =0 ; i < HEIGHT ; i++) {
			for (int  j =WIDTH /2 ; j < WIDTH ; j++) {
				if (arrFill.coord[i][j]) {	// 좌표가 같은 flood상태 일 때
					arrFill.bitmapId[i][j] =aroundImg ;	// ResourceId로 (0, 0)의 Id로 변경
					comCheckCnt++ ;
				}
			}
		}
		if (comCheckCnt == MATRIX) {
			timerCancel () ;
			replayClick (btnReplay) ;
		}
		relativeMidLay.invalidate () ;	// 바뀐 그림을 새로 그린다.
	}
	
	public void timerCancel ()
	{
		if (timerComThread != null && timerComThread.isAlive()) {
			timerComThread.runFlag =true ;
		}
		if (timerThread != null && timerThread.isAlive()) {
			timerThread.runFlag =true ;
		}	
		if (replayThread != null && replayThread.isAlive())
			replayThread.runFlag =true ;
		timerThread.animTime.cancel() ;
	}
	
	public void timerStart ()
	{		
		timebar.setMax(TIME) ;	// progressbar의 맥스 값설정
		timebar.setProgress(TIME) ;	// progressbar의 채워진 값 설정
		timerThread.runFlag =false ;
		timerComThread.runFlag =false ;
		replayThread.runFlag =false ;
		animTarget.startAnimation (timerThread.animTime) ;
	}
	
	// 다시 하기
	public void replayClick (View v)
	{
		if (mainAct.replayFlag) return ;
		mainAct.replayFlag =true ;
		
		etc.buttonDisabled(btnReplay, btnBGsound, btnRed, btnBlue, btnGreen,
				btnCyan, btnGray, btnPurple, btnYellow);
		if (mainAct.soundFlag) mainAct.soundpool.play(mainAct.sound[2], 1, 1, 1, 0, 1) ;

		Intent replay =new Intent (PlayComVS.this, Replay.class) ;	// Intent 설정
		startActivity (replay) ;	// Activity 호출
		/*AlertDialog.Builder altYN = new AlertDialog.Builder(this); // 대화상자 설정
		if (mainAct.soundFlag) mainAct.soundpool.play(mainAct.sound[2], 1, 1, 1, 0, 1) ;

		// 메세지를 설정하고 대화상자를 지울 수 있는지 설정 Yes버튼의 Text와 그에 따른 리스너 설정
		altYN.setMessage("다시 하시겠습니까?")
				.setCancelable(true)
				.setPositiveButton("도 전",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								clickCnt = COUNT; // 카운트 초기화

								arrFill.setRandomImg();
								relativeMidLay.invalidate(); // 화면을 다시 온드로우함

								textCnt.setText(clickCnt + "");

								timerCancel();
								timerStart();
								
								replayFlag =false ;
							}
							// No버튼의 Text와 그에 따른 리스너 설정
						})
				.setNegativeButton("안 함",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel(); // 대화상자를 닫음
								timerCancel();
								finish(); // 현재 Activity를 종료
							}
						});
		AlertDialog alt = altYN.create(); // 대화상자의 유형
		alt.setTitle("RePlay"); // Title의 Text 설정
		alt.show(); // 화면을 띄움*/

		etc.buttonEnabled(btnReplay, btnBGsound, btnRed, btnBlue, btnGreen,
				btnCyan, btnGray, btnPurple, btnYellow);

	}

	// 배경음 일시정지
	public void bgSounds2Click (View v) 
	{
		mainAct.soundFlag =!mainAct.soundFlag ;
		
		etc.buttonDisabled(btnReplay, btnBGsound, btnRed, btnBlue, 
				btnGreen, btnCyan, btnGray, btnPurple, btnYellow) ;
		
		if (mainAct.soundFlag) mainAct.soundpool.play(mainAct.sound[2], 1, 1, 1, 0, 1) ;
		etc.SoundClick(btnBGsound) ;
		
		etc.buttonEnabled(btnReplay, btnBGsound, btnRed, btnBlue, 
				btnGreen, btnCyan, btnGray, btnPurple, btnYellow) ;
	}
	
	// Activity 불러오기전 레이아웃 크기구하기
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
	}
	
	// 뒤로 가기 버튼클릭시
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (KeyCode == KeyEvent.KEYCODE_BACK) {
				arrFill.recycle() ;
				finish () ;
				timerCancel () ;
				return true ;
			}
		}
		return super.onKeyDown(KeyCode, event);
	}
}
