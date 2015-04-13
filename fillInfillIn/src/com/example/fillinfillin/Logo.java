package com.example.fillinfillin;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.app.Activity;

public class Logo extends Activity {
	
	MainActivity mainAct ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logo);
		new Intro ().start() ;
	}
	class Intro extends Thread {

		public void run () {

			SystemClock.sleep( 3000 );
			finish();
		}
	}
	
	// 뒤로 가기 버튼클릭시
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (KeyCode == KeyEvent.KEYCODE_BACK) {
				mainAct.BGmp.release() ;	// 리소스 해지
				finish () ;	// Activity를 종료하고
				System.exit (0) ;	// 어플 종료
				
				return true ;
			}
		}
		return super.onKeyDown(KeyCode, event);
	}
}