package com.ledqrcode.basic;

import android.util.DisplayMetrics;

public class Scale {

	private float scaleWidth, scaleHeight ;
	
	public Scale (DisplayMetrics disM) {
	
		this.scaleWidth =disM.widthPixels ;
		this.scaleHeight =disM.heightPixels ;
	}
	
	public float getScaleWidht () {
		return this.scaleWidth ;
	}
	
	public float getScaleHeight () {
		return this.scaleHeight ;
	}
}
