package com.example.mobilelap_02_simpletour;

import android.support.v7.app.ActionBarActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private EditText idEdit, passwordEdit ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		idEdit =(EditText) findViewById (R.id.main_edit_id) ;
		passwordEdit =(EditText) findViewById (R.id.main_edit_password) ;
	}
	
	// Login Click
	public void loginClick (View v) {
		
		// NextActivity put Intent data
		try {
			startActivity(new Intent (MainActivity.this, TourActivity.class)
				.putExtra("id", idEdit.getText ().toString()));
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "Sorry...Activity Error", Toast.LENGTH_SHORT).show () ;
		}
	}
}

