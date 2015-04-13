package com.example.fillinfillin;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.app.Activity;

public class Guide extends Activity {
	
	MainActivity mainAct ;

	private ImageView img ;
	private int page ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide);
		
		img =(ImageView) findViewById (R.id.guide) ;
		img.setBackgroundResource(R.drawable.guide_1) ;
		page=1 ;
	}
	
	public void prevClick (View v)
	{
		if (page ==2 ) {
			img.setBackgroundResource(R.drawable.guide_1) ;
			page =1 ;
		}
		else if (page ==3) {
			img.setBackgroundResource(R.drawable.guide_2) ;
			page =2 ;
		}
	}
	
	public void nextClick (View v)
	{
		if (page ==1) {
			img.setBackgroundResource(R.drawable.guide_2) ;
			page =2 ;
		}
		else if (page ==2) {
			img.setBackgroundResource(R.drawable.guide_3) ;
			page =3 ;
		}
	}
}