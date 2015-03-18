package com.example.mobilelap_02_simpletour;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TourActivity extends ActionBarActivity {

	private TextView idText, prevTourText ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tour);
		
		idText =(TextView) findViewById (R.id.tour_text_id) ;
		prevTourText =(TextView) findViewById (R.id.tour_text_prev_tour) ;
	}
	
	// Tour Click
	public void tourClick (View v) {
		
		int id =v.getId () ;
		double latitude =0, longitude =0 ;
		String tourName ="" ;
		
		switch (id) {
		case R.id.tour_btn_tour_01 :
			
			latitude =37.610907 ;
			longitude =126.997289 ;
			tourName ="Kookmin Univ." ;
			
			break ;
			
		case R.id.tour_btn_tour_02 :
			
			latitude =37.371706 ; 
			longitude =-122.035605 ;
			tourName ="Silicon Valley" ;
			
			break ;
			
		case R.id.tour_btn_tour_03 :
			
			latitude =39.019608 ; 
			longitude =125.752619 ;
			tourName ="Pyongyang" ;
			
			break ;
		}
		
		prevTourText.setText (tourName) ;
		
		try {
			Intent intent =new Intent (TourActivity.this, TourViewActivity.class) ;
			intent.putExtra("latitude", latitude) ;
			intent.putExtra("longitude", longitude) ;
			intent.putExtra("tourName", tourName) ;
			
			// Intent Calls
			startActivity(intent) ;
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "Sorry", Toast.LENGTH_SHORT).show () ;
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
        
		if (hasFocus) {
			
			// 접속한 사람 보여주기
			try {
				idText.setText(getIntent ().getStringExtra("id") +" Tour!!!") ;
			} catch (Exception e) {
				idText.setText("Tour!!!") ;
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case  R.id.action_calls :
			
			// PhoneNumber Settings
			try {
				startActivity(new Intent (TourActivity.this, CallActivity.class)) ;
			} catch (ActivityNotFoundException e) {
				Toast.makeText(this, "Sorry...Activity Error", Toast.LENGTH_SHORT).show () ;
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
