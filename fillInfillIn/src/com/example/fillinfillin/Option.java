package com.example.fillinfillin;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

public class Option extends Activity {
	
	MainActivity mainAct ;	// 메인 엑티비티 객체 생성
	Etc etc ;
	
	private static final int EASY =R.drawable.easy, HARD =R.drawable.hard, HELL =R.drawable.hell ;	// 난이도
	private static final int SOUND_1 =R.drawable.sound1, SOUND_2 =R.drawable.sound2,
			SOUND_3 =R.drawable.sound3, SOUND_4 =R.drawable.sound4 ;	// 배경음
	private static final int ARCADE =R.drawable.arcade, COM_VS =R.drawable.comvs, CRAZY =R.drawable.cube ;
	private static int differ =EASY, sounder =SOUND_1, typer =ARCADE ;	// 난이도와 배경음 링크
	private ImageButton sound, type, diff ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.option);
		
		etc =new Etc (Option.this) ;

		// Activity 실행시 초기 이미지 설정
		sound =(ImageButton) findViewById (R.id.selBGsound) ;
		type =(ImageButton) findViewById (R.id.type) ;
		diff =(ImageButton) findViewById (R.id.diff) ;
		
		sound.setImageResource (sounder) ;
		if (sounder == SOUND_1) sound.setTag(0) ;
		else if (sounder == SOUND_2) sound.setTag (1) ;
		else if (sounder == SOUND_3) sound.setTag (2) ;
		else sound.setTag(3) ;
		
		
		type.setImageResource (typer) ;
		if (typer == ARCADE) type.setTag(0) ;
		else if (typer == COM_VS) type.setTag(1) ;
		else type.setTag(2) ;
		
		
		diff.setImageResource (differ) ;
		if (differ == EASY) diff.setTag (0) ;
		else if (differ == HARD) diff.setTag(1) ;
		else diff.setTag (2) ;
	}

	// 뒤로가기 버튼 클릭시
	public boolean onKeyDown (int KeyCode, KeyEvent event)
	{		
		if (event.getAction () == KeyEvent.ACTION_DOWN) {
			if (KeyCode == KeyEvent.KEYCODE_BACK) {
				// 현재 이미지의 정보를 static 변수에 넣어서 다시 Activity 실행시 종료 시 이미지 보여줌
				if (sound.getTag () == (Object)0) sounder =SOUND_1 ;
				else if (sound.getTag () == (Object)1) sounder =SOUND_2 ;
				else if (sound.getTag () == (Object)2) sounder =SOUND_3 ;
				else sounder =SOUND_4 ;
				
				if (type.getTag() == (Object)0) typer =ARCADE ;
				else if  (type.getTag () == (Object)1) typer =COM_VS ;
				else typer =CRAZY ;
				
				if (diff.getTag () == (Object)0) {
					differ =EASY ;
					etc.setEasyDifferent() ;
				}
				else if (diff.getTag () == (Object)1) {
					differ =HARD ;
					etc.setHardDifferent() ;
				}
				else {
					differ =HELL ;
					etc.setHellDifferent() ;
				}

				if (mainAct.TYPE_MODE == 2) {
					mainAct.FILL_HEIGHT =mainAct.FILL_WIDTH ;
					mainAct.FILL_MATRIX =mainAct.FILL_WIDTH *mainAct.FILL_WIDTH ;
					mainAct.FILL_PLAY_TIME *=2 ;
					mainAct.FILL_CLICK_COUNT *=2 ;
				}
				finish () ;
				return true ;
			}
		}
		return super.onKeyDown (KeyCode, event) ;
	}
	
	// 배경음 변경
	public void selBGsoundClick (View v)
	{
		etc.buttonDisabled(sound, diff, type, null, null, null, null, null, null) ;
		if (mainAct.soundFlag) mainAct.soundpool.play(mainAct.sound[0], 1, 1, 1, 0, 1) ;
		
		boolean TF =mainAct.BGmp.isPlaying() ;
		mainAct.BGmp.release() ;

		if (sound.getTag() == (Object)0) {
			sound.setImageResource (SOUND_2) ;
			sound.setTag(1) ;		
			mainAct.BGmp =MediaPlayer.create(Option.this, R.raw.freakish_in_nature_full_mix_demo) ;
			
		}
		else if (sound.getTag () == (Object)1) {	
			sound.setImageResource (SOUND_3) ;
			sound.setTag(2) ;
			mainAct.BGmp =MediaPlayer.create(Option.this, R.raw.stomp_hop_full_mix_demo) ;
		}
		else if (sound.getTag () == (Object)2) {
			sound.setImageResource (SOUND_4) ;
			sound.setTag(3) ;
			mainAct.BGmp =MediaPlayer.create(Option.this, R.raw.the_legend_continues_full_mix_demo) ;
		}
		else {
			sound.setImageResource (SOUND_1) ;
			sound.setTag(0) ;
			mainAct.BGmp =MediaPlayer.create(Option.this, R.raw.famous_brothers_full_mix_demo) ;
		}
		mainAct.BGmp.setLooping(true) ;
		if (TF) mainAct.BGmp.start() ;

		//Bundle extras =getIntent ().getExtras() ;
		
		etc.buttonEnabled(sound, diff, type, null, null, null, null, null, null) ;
	}
	
	// 게임 모드에 따라서 속성 변경
	public void typeClick (View v)
	{
		etc.buttonDisabled(sound, diff, type, null, null, null, null, null, null) ;
		if (mainAct.soundFlag) mainAct.soundpool.play(mainAct.sound[0], 1, 1, 1, 0, 1) ;
		
		if (type.getTag () == (Object)0) {
			type.setImageResource (COM_VS) ;
			type.setTag (1) ;
			
			mainAct.TYPE_MODE =1 ;
		}
		else if (type.getTag () == (Object)1) {
			type.setImageResource (CRAZY) ;
			type.setTag (2) ;
			
			mainAct.TYPE_MODE =2 ;
		}
		else {
			type.setImageResource (ARCADE) ;
			type.setTag (0) ;
			
			mainAct.TYPE_MODE =0 ;
		}		
		etc.buttonEnabled(sound, diff, type, null, null, null, null, null, null) ;
	}
	
	// 난이도에 따라서 속성 변경
	public void diffClick (View v)
	{
		etc.buttonDisabled(sound, diff, type, null, null, null, null, null, null) ;
		if (mainAct.soundFlag) mainAct.soundpool.play(mainAct.sound[0], 1, 1, 1, 0, 1) ;
	
		if (diff.getTag () == (Object)0) {	
			diff.setImageResource (HARD) ;
			diff.setTag (1) ;
		}
		else if (diff.getTag () == (Object)1) {	
			diff.setImageResource (HELL) ;
			diff.setTag (2) ;
		}
		else {
			diff.setImageResource (EASY) ;
			diff.setTag (0) ;
		}	
		
		etc.buttonEnabled(sound, diff, type, null, null, null, null, null, null) ;
	}
}
