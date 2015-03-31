package com.ai.slidingpuzzle.activity;


import com.ai.slidingpuzzle.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;

public class BoardSizeActivity extends ActionBarActivity {

	final int MAX=5, MIN =2 ;
	
	EditText rowEdit, colEdit ;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_board_size);
		
		rowEdit =(EditText) findViewById (R.id.row_edit) ;
		rowEdit.setText(getIntent().getIntExtra("row", 3) +"");
		colEdit =(EditText) findViewById (R.id.col_edit) ;
		colEdit.setText(getIntent().getIntExtra("col", 3) +"");
	}
	
	public void rowUpClick (View v) {
		
		int maxRow =Integer.parseInt(rowEdit.getText().toString()) +1 ;
		
		if (maxRow <= MAX)
			rowEdit.setText(maxRow +"") ;
	}
	
	public void rowDownClick (View v) {
		
		int minRow =Integer.parseInt(rowEdit.getText().toString()) -1 ;
		
		if (minRow >= MIN)
			rowEdit.setText(minRow +"") ;
	}
	
	public void colUpClick (View v) {
		
		int maxCol =Integer.parseInt(colEdit.getText().toString()) +1 ;
		
		if (maxCol <= MAX)
			colEdit.setText(maxCol +"") ;
	}
	
	public void colDownClick (View v) {
		
		int minCol =Integer.parseInt(colEdit.getText().toString()) -1 ;
		
		if (minCol >= MIN)
			colEdit.setText(minCol +"") ;
	}
	
	// SharedPreferences Row Col Save
	public void okClick (View v) {
		
		int row =Integer.parseInt(rowEdit.getText().toString()),
				col =Integer.parseInt(colEdit.getText().toString()) ;
		
		Intent intent =new Intent (getResources().getString(R.string.size_recv)) ;
		intent.putExtra("row", row) ;
		intent.putExtra("col", col) ;
		sendBroadcast(intent);
		
		finish () ;
	}
}
	