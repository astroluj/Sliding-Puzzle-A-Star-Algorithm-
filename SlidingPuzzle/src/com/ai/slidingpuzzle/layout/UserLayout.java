package com.ai.slidingpuzzle.layout;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ai.slidingpuzzle.R;
import com.ai.slidingpuzzle.basic.Scale;

public class UserLayout {

	// View Board Info
	public static void initBoardUserView (Scale scale, 
			RelativeLayout userRelativeLayout,
			ImageButton hintBtn, ImageButton solveBtn, ImageButton sizeBtn, ImageButton shuffleBtn,
			ImageView hintImgView) {
		
		int height =(int)(scale.getScaleHeight() -(userRelativeLayout.getTop() *2)) ; 
		
		// UserRelativeLayout 
		// width 30%, height 100% 
		RelativeLayout.LayoutParams params ;
		userRelativeLayout.removeAllViews();
		
		// Hint ImageButton
		// width 100%, height 15%
		params =new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, height /100 *15) ;
		shuffleBtn.setLayoutParams(params);
		
		// Solve ImageButton
		// Width 100%, height 15%
		params =new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, height /100 *15) ;
		params.addRule(RelativeLayout.BELOW, shuffleBtn.getId()) ;
		hintBtn.setLayoutParams(params);
		
		// Size ImageButton
		// Width 100%, height 15%
		params =new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, height /100 *15) ;
		params.addRule(RelativeLayout.BELOW, hintBtn.getId()) ;
		solveBtn.setLayoutParams(params);
		
		// Shuffle ImageButton
		// Width 100%, height 15%
		params =new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, height /100 *15) ;
		params.addRule(RelativeLayout.BELOW, solveBtn.getId()) ;
		sizeBtn.setLayoutParams(params);
		
		// OrgImageView
		// Width 100%, height 50%
		params =new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, height /100 *50) ;
		params.addRule(RelativeLayout.BELOW, sizeBtn.getId());
		hintImgView.setImageResource(R.drawable.raba) ;
		hintImgView.setLayoutParams(params) ;
		
		// Set ChildView
		userRelativeLayout.addView(hintBtn);
		userRelativeLayout.addView(solveBtn);
		userRelativeLayout.addView(sizeBtn);
		userRelativeLayout.addView(shuffleBtn) ;
		userRelativeLayout.addView(hintImgView) ;
		userRelativeLayout.invalidate();
	}
}
