package com.example.mobilelap_02_simpletour;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CallActivity extends ActionBarActivity {

	private EditText phoneNumberEdit ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call);
		
		phoneNumberEdit =(EditText) findViewById (R.id.call_edit_phonenumber) ;
	}

	// CallsClick
	public void callsClick (View v) {
		try {
			// Intent.ACTION_DIAL is View, Intent.ACTION.CALL is Calls
			startActivity(new Intent (Intent.ACTION_CALL, 
					Uri.parse("tel:" +phoneNumberEdit.getText ().toString())));
			finish () ;
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "Sorry", Toast.LENGTH_SHORT).show () ;
		}
	}
}
