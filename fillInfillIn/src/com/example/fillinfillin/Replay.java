package com.example.fillinfillin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

public class Replay extends Activity {
	
	MainActivity mainAct ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.replay);
	}
	
	public void Play (View v) 
	{
		mainAct.playFlag =true ;
		finish () ;
	}
	
	public void Cancel (View v) 
	{
		mainAct.cancelFlag =true ;
		finish () ;
	}
	
	// 뒤로 가기 버튼클릭시
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (KeyCode == KeyEvent.KEYCODE_BACK) {
				mainAct.replayFlag =false ;
				finish () ;
				return true ;
			}
		}
		return super.onKeyDown(KeyCode, event);
	}
}