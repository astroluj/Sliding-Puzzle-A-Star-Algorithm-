package com.ledqrcode.activity;

import com.ledqrcode.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class IntervalActivity extends Activity {
	
 	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView (R.layout.activity_interval) ;
	}
 	
 	public void setClick (View v) {
 		EditText interval =(EditText) findViewById (R.id.interval_edit) ;
 		
 		Intent intent = new Intent (IntervalActivity.this, MainActivity.class) ;
 		intent.putExtra ("interval", interval.toString()) ;
 		
 		// call startActivity Result
 		setResult (RESULT_OK, getIntent ()) ;
 		
 		finish () ;
 	}
}
