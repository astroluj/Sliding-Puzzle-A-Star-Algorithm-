package com.example.guitarplay;


import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ScrollView;
import android.util.Log;

public class PlayActivity extends Activity {

	MainActivity mainAct;	// 메인 엑티비티
	ProximitySensorThread proximitySensorThread ;	// 근접 센서
	AccelerometerSensorThread accelerometerSensorThread ;	// 가속도 및 방향 센서

	private final float PRESSURE =0.2f ;
	private final int FRETS =20, NECKS =21, LINES =126, LINE_SOUNDS =132, LINE =6, SLAP_TIME =200 ; 

	private float[] lineHalf ={0.0f, 0.9f, 1.0f, 1.8f, 1.9f, 2.8f, 2.9f, 3.7f, 3.8f, 4.7f, 4.8f, 5.7f} ;	// 각 현의 오차 범위
	
	private static Context c ;
	private Toast toast ;
	private DecimalFormat f ;
	private int scrollCnt, slapSound ;
	private float[] val, pre ;

	
	private long slapTime ;	// 슬랩 일 경우를 체크할 시간
	private long strokeTime ;	// up & down 체크
	private int[] fret ;	// 각 프렛의 높이
	private int[] guitarSound, openSound ;	// 각 현의 소리
	private int[] lines ;	// 각 현에 부여된 소리
	private float[] SCROLL_BY, SCROLL_SET, SCROLL_TO ;	// 넥들의 길이, 넥의 앞뒤 반폭, 넥의 좌표
	private boolean scrollFlag, startFlag, styleFlag, carpoFlag ;	// 스크롤 작동, 맨 처음 작동, 주법 선택, 카포 사용

	private SoundPool soundPool; // 사운드 풀
	private RelativeLayout relativeLay ;
	private Bitmap img ;
	private ImageView[] guitarView ;	// 각 넥들의 이미지
	private ScrollView scrollView ;
	private ScrollView.OnTouchListener scrollListener =new ScrollView.OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			//Log.d ("D", "" +(scrollView.getScrollY() +scrollView.getHeight())) ;
			if (!scrollFlag) return false ;
			
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN :
			case MotionEvent.ACTION_POINTER_DOWN : 	// 스트로크
				//Log.d ("D", "" +(event.getX(event.getPointerCount() -1) *img.getWidth() /mainAct.scaleWidth)) ;
				if (styleFlag && event.getPointerCount() > 1) break ;
				Log.d ("D" ,"down") ;
				float downX =event.getX(event.getPointerCount() -1) *img.getWidth() /mainAct.scaleWidth ;
				float downY =event.getY (event.getPointerCount () -1) +scrollView.getScrollY () ;
				
				int neckNum =setNecks (downY) ;
				if (neckNum < 0) {
					Toast.makeText(c, "오류", Toast.LENGTH_SHORT).show () ;
					break ;
				}
				
				if (event.getPressure(event.getPointerCount () -1) < PRESSURE) 
					setDownLines (downX, (neckNum *LINE)) ;
				
				else {	// F 코드 형식 
					for (int i =0 ; i < LINE ; i++)
						lines[i] =guitarSound[LINE *neckNum +i] ;
					Log.d ("D", "neck -" +neckNum +" F chord") ;
				}
				break ;
			case MotionEvent.ACTION_UP :	// 아르페지오
			case MotionEvent.ACTION_POINTER_UP :	// 스트로크
				if (styleFlag) {
					setBasicSound () ;
					if (event.getPointerCount() > 1) break ;
				}
				Log.d ("D" ,"up") ;
				float upX =event.getX(event.getPointerCount() -1) *img.getWidth() /mainAct.scaleWidth ;

				if (event.getPressure(event.getPointerCount () -1) < PRESSURE) 
					setUpLines (upX) ;
				
				break ;
			}
			return true ;
		}
	};
	private Handler playHandler =new Handler () {
		public void handleMessage (Message msg) {
			switch (msg.what) {
			case 0 :
				if ((Float)msg.obj == 0.0f) 
					slapTime =System.currentTimeMillis() ;
				else {	
					long endTime =System.currentTimeMillis() ;
					if (endTime -slapTime > SLAP_TIME) 	// 슬랩 인식
						soundPool.play(slapSound, 1, 1, 1, 0, 1) ;
					else 	// 일반 
						for (int i =5; i >= 0  ; i--) 
						soundPool.play(lines[i], 1, 1, 1, 0, 1) ;
				}
				break ;
			case 1 :
				val =(float[])msg.obj ;
				val[1] =(-val[1] *1000.0f) *(1.0f/1000.0f) ;
				/*if (toast == null)
					toast =Toast.makeText(c, "val :  " +f.format(val[1]), Toast.LENGTH_SHORT) ;
				else toast.setText ("val :  " +f.format(val[1])) ;
				toast.show () ;
				val[1] =(-val[1] *1000.0f) *(1.0f/100.0f) ;*/
				if (val[1] -pre[1] > 2) {
					if (scrollView.getScrollY() > 0 && scrollCnt < 17)
						scrollView.smoothScrollTo(0, (int)SCROLL_TO[scrollCnt++]) ;
					if (toast == null)
						toast =Toast.makeText(c, "val :  " +f.format(val[1] -pre[1]), Toast.LENGTH_SHORT) ;
					else toast.setText ("val :  " +f.format(val[1] -pre[1])) ;
					toast.show () ;
					pre[1] =val[1] =0 ;
					break ;
				}
				else if (val[1] -pre[1] < -2) {
					if (scrollView.getScrollY() < scrollView.getHeight() -scrollView.getBottom() && scrollCnt >= 0)
						scrollView.smoothScrollTo(0, (int)SCROLL_TO[scrollCnt--]) ;
					if (toast == null)
						toast =Toast.makeText(c, "val :  " +f.format(val[1] -pre[1]), Toast.LENGTH_SHORT) ;
					else toast.setText ("val :  " +f.format(val[1] -pre[1])) ;
					toast.show () ;
					pre[1] =val[1] =0 ;
					break ;
				}
				pre[1] =val[1] ;
				break ;
				
			case 2 :
				
				break ;
			}
		}
	} ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		val =new float[3] ; 
		pre =new float[3] ;
		lines =new int[LINE] ;
		openSound =new int[LINE] ;
		
		SCROLL_BY =new float[NECKS] ;
		SCROLL_TO =new float[NECKS] ;
		SCROLL_SET =new float[NECKS] ;
		
		guitarView =new ImageView[NECKS] ;
		fret =new int[FRETS] ;
		guitarSound =new int[LINE_SOUNDS] ;

		soundPool =new SoundPool(LINE, AudioManager.STREAM_MUSIC, 0) ; 
		
		f =new DecimalFormat () ;
		f.applyLocalizedPattern("0.####") ;
		c =getApplicationContext() ;
		toast =null ;
	
		setContentView(R.layout.activity_play);
		relativeLay =(RelativeLayout) findViewById (R.id.play_guitar) ;
		
		for (int i =0 ; i < lineHalf.length ; i++) {
			lineHalf[i] =lineHalf[i] *94.7f *mainAct.density ;
			//Log.d ("D", "" +lineHalf[i]) ;
		}
		
		// 소리 설정
		slapSound =soundPool.load(this, R.raw.slap, 1) ;
		for (int i =0 ; i < LINE ; i++)
			lines[i] =openSound[i] =soundPool.load(this, 
					getResources ().getIdentifier("open_" +(i +1), "raw", "com.example.guitarplay"), 1) ;
		for (int i =0 ; i < LINES ; i++) 
			guitarSound[i] =soundPool.load(this, 
					getResources().getIdentifier("lines_" +(i +1), "raw", "com.example.guitarplay"), 1) ;
		
		// 넥 설정
		RelativeLayout.LayoutParams params ;
		for (int i =0 ; i < NECKS ; i++) {	
			img =BitmapFactory.decodeResource(getResources(),
					getResources().getIdentifier("guitars_" +(NECKS -i), "drawable", "com.example.guitarplay")) ;
			img =Bitmap.createScaledBitmap(img, 
					(int)(img.getWidth() *mainAct.density ),
					(int) (img.getHeight() *mainAct.density), true) ;		
			params =new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT, 
					RelativeLayout.LayoutParams.WRAP_CONTENT) ;
			if (i > 0) params.addRule(RelativeLayout.BELOW, guitarView[NECKS -i].getId()) ;
			
			guitarView[FRETS -i] =new ImageView (this) ;
			guitarView[FRETS -i].setId (NECKS -i) ;
			guitarView[FRETS -i].setLayoutParams(params) ;
			guitarView[FRETS -i].setScaleType(ScaleType.FIT_XY) ;
			guitarView[FRETS -i].setAdjustViewBounds(true) ;
			guitarView[FRETS -i].setImageBitmap(img) ;
			relativeLay.addView(guitarView[FRETS -i]) ;
		}

		//	스크롤 뷰
		scrollView =(ScrollView) findViewById (R.id.play_scrollView) ;
		scrollView.setOnTouchListener(scrollListener) ;
		
		// 접근센서
		proximitySensorThread =new ProximitySensorThread (this, playHandler) ;
		proximitySensorThread.setDaemon (true) ;
		proximitySensorThread.start() ;
		
		// 가속도, 방향 센서
		accelerometerSensorThread =new AccelerometerSensorThread (this, playHandler) ;
		accelerometerSensorThread.setDaemon(true) ;
		accelerometerSensorThread.start () ;
	}
	
	// 현 소리 초기화
	public void setBasicSound () {
		if (styleFlag) 
			for (int i =0 ; i < 6 ; i++)
				lines[i] =0 ;
		else 
			for (int i =0 ; i < 6 ; i++)
				lines[i] =openSound[i] ;
	}
	
	// 터치된 Neck 번호
	public int setNecks (float y) {
		for (int i =0 ; i < FRETS ; i++) {
			if (fret[i] < y) {
				//Log.d ("D", "" +"neck -" +i) ;
				return i ;
			}
		}
		return -1 ;
	}
	
	// 터치된 현 번호
	public void setDownLines (float x, int neckNum) {
		for (int i =0 ; i < LINE ; i++) {
			if (x > lineHalf[i *2] && x < lineHalf[(i *2) +1]) {
				lines[i] =guitarSound[neckNum +i] ;
				Log.d ("D", "" +(i+1)) ;
				break ;
			}
		}
	}
	
	// 띄어진 현 번호
	public void setUpLines (float x) {
		for (int i =0 ; i < LINE ; i++) {
			if (x > lineHalf[i *2] && x < lineHalf[(i *2) +1]) {
				if (!styleFlag) lines[i] =openSound[i] ;
				else lines[i] =0 ;
				Log.d ("D", "" +(i +1)) ;
				break ;
			}
		}
	}
	
	// 스크롤 뷰 정리
	public void setScroll () {
		float scrollY =scrollView.getScrollY() +scrollView.getHeight() ;
	
		for (int i =0 ; i < SCROLL_TO.length ; i++) {
			if (SCROLL_SET[i] <= scrollY) {
				//Log.d ("D" , "[" +i +"] " +SCROLL_SET[i] +"   " +scrollY) ;
				//Log.d ("D", "" +SCROLL_TO[i]) ;
				scrollView.scrollTo(0, (int)SCROLL_TO[i]);
				break ;
			}
		}
	}
	
	// 뒤로 가기 버튼클릭시
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (KeyCode == KeyEvent.KEYCODE_BACK) {
				proximitySensorThread.stopSensorFlag =true ;				
				accelerometerSensorThread.stopSensorFlag =true ;
				finish () ;
			}
		}
		return super.onKeyDown(KeyCode, event);
	}
	
	// 화면이 보여지기 시작할 때
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if (hasFocus && !startFlag) {
			scrollView.scrollTo(0, relativeLay.getHeight());
			startFlag =true ;

			for (int i =0 ; i < FRETS ; i++)
				fret[i] =guitarView[i +1].getBottom() ;
			for (int i =0 ; i < NECKS ; i++) {		
				SCROLL_BY[FRETS -i] =guitarView[FRETS -i].getHeight()  ;
				SCROLL_TO[FRETS -i] =guitarView[FRETS -i].getBottom() -scrollView.getHeight() ;
				SCROLL_SET[FRETS -i] =guitarView[FRETS-i].getBottom() -(SCROLL_BY[FRETS -i] /2) ;
			}
		}	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu) ;
		getMenuInflater().inflate(R.menu.play_menu, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected (MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_play :
			scrollFlag =!scrollFlag ;
			if (scrollFlag) {
				item.setTitle("연주 그만") ;
				setScroll () ;
				
				proximitySensorThread.startSensorFlag =true ;
				accelerometerSensorThread.startSensorFlag =true ;
			}
			else {
				item.setTitle("연주 시작") ;
				proximitySensorThread.stopSensorFlag =true ;
				accelerometerSensorThread.stopSensorFlag =true ;
			}
			
			return true ;
		case R.id.menu_arpeggio :
			if (!styleFlag) {
				styleFlag =true ;
				setBasicSound () ;
			}
			
			return true ;
		case R.id.menu_stroke :
			if (styleFlag) {
				styleFlag =false ;
				setBasicSound () ;
			}
			
			return true ;
		case R.id.menu_carpo :
			if (!carpoFlag) {
				item.setTitle ("카포 제거") ;
			}
			else item.setTitle("카포 사용") ;
			carpoFlag =!carpoFlag ;
			
			return true ;
		}
		return false ;
	}
	
	public void onResume () {
		super.onResume () ;
		if (scrollFlag) { ;
			proximitySensorThread.startSensorFlag =true ;
			accelerometerSensorThread.startSensorFlag =true ;
		}
	}
	
	public void onPause () {
		super.onPause() ;
		proximitySensorThread.stopSensorFlag =true ;
		accelerometerSensorThread.stopSensorFlag =true ;
	}
	
	public void onStop () {
		super.onStop () ;
		img.recycle() ;
		proximitySensorThread.interrupt() ;
		proximitySensorThread.runFlag =true ;
		accelerometerSensorThread.interrupt() ;
		accelerometerSensorThread.runFlag =true ;
	}
}
