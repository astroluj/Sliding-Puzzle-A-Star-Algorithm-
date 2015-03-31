package com.ai.slidingpuzzle.layout;

import com.ai.slidingpuzzle.R;
import com.ai.slidingpuzzle.basic.Scale;

import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InfoLayout {

	public static void initInfoView (Scale scale, 
			RelativeLayout infoRelativeLayout, TextView textView, EditText cntEdit) {
		
		int width =(int)(scale.getScaleWidht() /100 *10),
				height =(int)(scale.getScaleHeight() -(infoRelativeLayout.getTop() *2)) ; 
		
		// UserRelativeLayout 
		// width 30%, height 100% 
		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(width, height) ;
		params.addRule(RelativeLayout.RIGHT_OF, R.id.img_layout) ;
		infoRelativeLayout.setLayoutParams(params) ;
		
		infoRelativeLayout.removeAllViews();
		
		infoRelativeLayout.addView(cntEdit);
		infoRelativeLayout.addView(textView);
		infoRelativeLayout.invalidate();
	}
	
	public static void setMoveCntView (EditText cntEdit, int cnt) {
		
		cntEdit.setText(cnt +"");
	}
}
