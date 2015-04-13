package com.example.guitarplay;

import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.WindowManager;
import android.content.Intent;

public class MainActivity extends Activity {

	protected static Display dis ; 
	protected static float scaleWidth, scaleHeight, density ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dis = ((WindowManager) MainActivity.this
				.getSystemService(MainActivity.this.WINDOW_SERVICE))
				.getDefaultDisplay();
		scaleWidth =dis.getWidth() ; scaleHeight =dis.getHeight() ;
		
		DisplayMetrics mat =new DisplayMetrics () ;
		getWindowManager ().getDefaultDisplay().getMetrics(mat) ;
		density =mat.density ;
		
		setContentView(R.layout.activity_main);

		Intent intent = new Intent(MainActivity.this, PlayActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
