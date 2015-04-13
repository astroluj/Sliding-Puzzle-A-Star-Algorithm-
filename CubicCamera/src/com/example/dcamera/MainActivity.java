package com.example.dcamera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SystemClock;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

	protected static float scaleWidth, scaleHeight; // 해상도 가로 세로를 구함

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Display dis = ((WindowManager) MainActivity.this
				.getSystemService(MainActivity.this.WINDOW_SERVICE))
				.getDefaultDisplay();
		scaleWidth = dis.getWidth();
		scaleHeight = dis.getHeight(); // 해상도 가로 세로를 구함
		
		setContentView(R.layout.activity_main) ;
		
		// 미디어 스캔 방지
		File noScan =new File (Environment.getExternalStorageDirectory() +"/CubicCamera/.nomedia") ;
		if (!noScan.exists()) noScan.mkdirs() ;
	}
	
	// 촬영 클릭
	public void cameraClick (View v) {
		Intent camera =new Intent (MainActivity.this, CameraActivity.class) ;
		startActivity (camera) ;
	}
	
	// 앨범 클릭
	public void albumClick (View v) {
		Intent album =new Intent (MainActivity.this, AlbumActivity.class) ;
		startActivity (album) ;
	}
}
