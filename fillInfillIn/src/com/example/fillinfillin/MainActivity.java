package com.example.fillinfillin;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

public class MainActivity extends Activity {

	Etc etc ;
	
	protected static final int EASY_CLICK_COUNT =10, EASY_MATRIX =60, EASY_TIME =60,
			EASY_SHAPE_COUNT =4, EASY_WIDTH =6, EASY_HEIGHT =10, 
			HARD_CLICK_COUNT =20, HARD_MATRIX =140, HARD_TIME =120, 
			HARD_SHAPE_COUNT =5, HARD_WIDTH =10, HARD_HEIGHT =14,
			HELL_CLICK_COUNT =35, HELL_MATRIX =252, HELL_TIME =150, 
			HELL_SHAPE_COUNT =7, HELL_WIDTH =14, HELL_HEIGHT =18 ;
	protected static final int D_TAB =10 ;
	
	protected static int FILL_CLICK_COUNT =EASY_CLICK_COUNT, FILL_MATRIX =EASY_MATRIX,
			FILL_PLAY_TIME =EASY_TIME, FILL_SHAPE_COUNT =EASY_SHAPE_COUNT,	
			FILL_WIDTH =EASY_WIDTH, FILL_HEIGHT =EASY_HEIGHT ;		// 난이도 상수
	protected static int TYPE_MODE =0 ;		// play mode 상수
	protected static int scaleWidth, scaleHeight ;
	protected static MediaPlayer BGmp ;	// 배경음
	protected static boolean soundFlag  =true, playFlag, cancelFlag, replayFlag ;
	protected static int[] sound ;
	protected static SoundPool soundpool ;
	protected static Handler soundHandler ;
	
	private ImageButton btnBGsound, btnOption, btnPlay, btnQuit, btnGuide ;
	private boolean optionFlag, BGsoundFlag, guideFlag ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Display dis =((WindowManager) MainActivity.this.getSystemService(
				MainActivity.this.WINDOW_SERVICE)).getDefaultDisplay () ;
		scaleWidth =dis.getWidth() ;
		scaleHeight =dis.getHeight() ;	// 해상도 가로 세로를 구함
				
		Intent logo =new Intent (MainActivity.this, Logo.class) ;	// Intent를 통해 
		startActivity (logo) ;	// Activity 호출
		
		// 배경음
		BGmp = MediaPlayer.create(getApplicationContext(), R.raw.famous_brothers_full_mix_demo); // 생성
		BGmp.setLooping(true); // 반복
		BGmp.start(); // 시작
				
		// 효과음 로드
		sound = new int[10];
		soundpool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
		sound[0] = soundpool.load(MainActivity.this, R.raw.curious_down, 1);
		sound[1] = soundpool.load(MainActivity.this, R.raw.win3, 1);
		sound[2] = soundpool.load(MainActivity.this, R.raw.beep4, 1);
		sound[3] = soundpool.load(MainActivity.this, R.raw.wood2, 1);

		setContentView(R.layout.activity_main);
		etc =new Etc (MainActivity.this) ;

		btnBGsound =(ImageButton) findViewById (R.id.bgSound) ;
		btnPlay =(ImageButton) findViewById (R.id.playFillIn) ;
		btnQuit =(ImageButton) findViewById (R.id.quit) ;
		btnOption =(ImageButton) findViewById (R.id.option) ;
		btnGuide =(ImageButton) findViewById (R.id.guide) ;
	}
	
	// 시작 버튼 클릭시 엑티비티소환
	public void playClick (View v)
	{
		if (!BGsoundFlag) {
			BGsoundFlag =true ;
			etc.buttonDisabled (btnBGsound, btnPlay, btnQuit, btnOption, btnGuide, null, null, null, null) ;

			if (soundFlag) soundpool.play(sound[2], 1, 1, 1, 0, 1) ;
					
				Intent play =null ;
				// Intent를 통한 값 넘기기
				if (TYPE_MODE == 0)play =new Intent (MainActivity.this, PlayArcade.class) ;
				else if (TYPE_MODE == 1) play =new Intent (MainActivity.this, PlayComVS.class) ;
				else play =new Intent (MainActivity.this, PlayCrazy.class) ;
				//play.putExtra("STATE", (Boolean) btnBGsound.getTag()) ;	// STATE란 이름표에 상태를 넣음
				startActivity (play) ;
				
				etc.buttonEnabled(btnBGsound, btnPlay, btnQuit, btnOption, btnGuide, null, null, null, null) ;
				BGsoundFlag =false ;
		}
	}
	
	// 옵션버튼 클릭시 엑티비티 소환
	public void optionClick (View v)
	{
		if (!optionFlag) {
			optionFlag =true ;
			etc.buttonDisabled (btnBGsound, btnPlay, btnQuit, btnOption, btnGuide, null, null, null, null) ;
			
			if (soundFlag) soundpool.play(sound[2], 1, 1, 1, 0, 1) ;
			// Intent를 통한 값 넘기기
			Intent option =new Intent (MainActivity.this, Option.class) ;
			//option.putExtra("STATE", (Boolean) btnBGsound.getTag()) ;
			startActivity (option) ;
					
			etc.buttonEnabled(btnBGsound, btnPlay, btnQuit, btnOption, btnGuide, null, null, null, null) ;
			optionFlag =false ;
		}
	}
	
	// 종료버튼 클릭시 엑티비티소환
	public void quitClick (View v)
	{	
		etc.buttonDisabled (btnBGsound, btnPlay, btnQuit, btnOption, btnGuide, null, null, null, null) ;
		if (soundFlag) soundpool.play(sound[2], 1, 1, 1, 0, 1) ;
		BGmp.release() ;	// 리소스 해지
		
		finish () ;	// Activity를 종료하고
		System.exit (0) ;	// 어플 종료
	}	
	
	// 배경음 일시정지 플레이
	public void bgSoundsClick (View v) 
	{
		soundFlag =!soundFlag ;
		
		etc.buttonDisabled (btnBGsound, btnPlay, btnQuit, btnOption, btnGuide, null, null, null, null) ;
		if (soundFlag) soundpool.play(sound[2], 1, 1, 1, 0, 1) ;
		etc.SoundClick(btnBGsound) ;
		etc.buttonEnabled (btnBGsound, btnPlay, btnQuit, btnOption, btnGuide, null, null, null, null) ;
	}
	
	// 플레이 방법 설명
	public void guideClick (View c)
	{
		if (!guideFlag) {
			guideFlag =true ;
			etc.buttonDisabled (btnBGsound, btnPlay, btnQuit, btnOption, btnGuide, null, null, null, null) ;
			
			if (soundFlag) soundpool.play(sound[2], 1, 1, 1, 0, 1) ;
			// Intent를 통한 값 넘기기
			Intent guide =new Intent (MainActivity.this, Guide.class) ;
			//option.putExtra("STATE", (Boolean) btnBGsound.getTag()) ;
			startActivity (guide) ;
					
			etc.buttonEnabled(btnBGsound, btnPlay, btnQuit, btnOption, btnGuide, null, null, null, null) ;
			guideFlag =false ;
		}
	}
	
	// 뒤로 가기 버튼클릭시
	public boolean onKeyDown (int KeyCode, KeyEvent event)
	{
		if (event.getAction () == KeyEvent.ACTION_DOWN) {
			if (KeyCode == KeyEvent.KEYCODE_BACK) {
				BGmp.release() ;	// 리소스 해지
				finish () ;	// Activity를 종료하고
				System.exit (0) ;	// 어플 종료
				
				return true ;
			}
		}
		return super.onKeyDown (KeyCode, event) ;
	}
	
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
	}
	
	public void onPause ()
	{
		super.onPause() ;
	}
	
	// Activity가 focus를 찾을 때
	public void onRestart ()
	{	
		etc.Sounds(btnBGsound) ;
		super.onRestart () ;
	}
}
