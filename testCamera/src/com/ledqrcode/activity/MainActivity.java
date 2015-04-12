package com.ledqrcode.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.ledqrcode.R;
import com.ledqrcode.basic.Scale;
import com.ledqrcode.camera.CameraFace;
import com.ledqrcode.camera.RectDraw;
import com.ledqrcode.thread.TimerThread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private final int MIN_SECTOR =10, CAPTURE_TIME =35,
			NONE =0, DRAG =1, ZOOM =2 ;	// clicked
	private final int INTERVAL_RESULT_OK = 1, CAPTURE = 0 ;
	private int INTERVAL =80 ;
	
	private TimerThread timerThread ;
	private RectDraw rectD ;
	private CameraFace cameraFace ;
	private Scale scale ;
	
	private Camera camera ;
	
	
	
	private ArrayList<Bitmap> img ;
	private ArrayList <byte []> imgBytes ;
	private ArrayList <Long> captureTime ;
	private ArrayList <Integer> acii_str ;

	private String textLED ;
	private boolean dragFlag, exposeFlag ;	
	private float x_1 =0, x_2 =0, y_1 =0, y_2 =0 ;	// 좌표
	private float scaleWidth, scaleHeight ;	// Phone Scale
	private int mode =NONE ;	// Click Mode
	private int captureCnt ;
	private long captureStartTime  ;
	
	private Handler timerHandlerCallback = new Handler (new TimerHandlerCallback()) ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// CameraFace init
		cameraFace = new CameraFace (getApplicationContext(), camera) ;
		
		// Get Scale
		DisplayMetrics disM =new DisplayMetrics () ;
		getWindowManager ().getDefaultDisplay().getMetrics(disM) ;
		scale =new Scale (disM) ;

		
		// init
		img =new ArrayList <Bitmap> () ;
		imgBytes =new ArrayList <byte []> () ;
		captureTime =new ArrayList <Long> () ;
		acii_str =new ArrayList <Integer> () ;
		
		textLED ="" ;
		dragFlag =false ;
		exposeFlag =false ;
		
		captureCnt =0 ;
		captureStartTime =0 ;
	}
	
	// Capture STart
	public void camera (View v)
	{
		setContentView (cameraFace) ;	
		// starting Thread
		startTimerThread () ;
	}
	
	// Camera Preview Callback
	private Camera.PreviewCallback timerShutter = new Camera.PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {

			// Data add
			imgBytes.add(data);
			captureCnt++;
			captureTime.add(System.currentTimeMillis()); // �� ���� ���� ����
															// ���� ���

			if (System.currentTimeMillis() - captureStartTime >= INTERVAL
					* CAPTURE_TIME
					&& timerThread.getRun()) {
				timerThread.setRun(false);

				readImgAcii(); // Read LED QRCode Data
				sortInterval(); // Data Analyze
			}
		}
	};
	
	private void startTimerThread () {
		timerThread =new TimerThread (timerHandlerCallback) ;
		timerThread.setDaemon(true) ;
		timerThread.setRun(true);
		timerThread.start() ;
	}
	
	private void releaseTimerThread () {
		try {
			timerThread.setRun(false);
			timerThread.interrupt() ;
		} catch (Exception e) {
			timerThread = null ;
		}
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    // Threading Check
	    if (timerThread != null) 
			releaseTimerThread () ;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu) ;
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
       
		switch (item.getItemId()) {
		case R.id.menu_interval :		// Setting Interval
        	Intent intent =new Intent (MainActivity.this, IntervalActivity.class) ;
        	startActivityForResult (intent, INTERVAL_RESULT_OK) ;
        	
        	return true ;
        	
		case R.id.menu_expose :
			if (exposeFlag) item.setTitle("Set expose Contorl Enabled") ;
			else item.setTitle("Set expose Contorl disEnabled") ;
			
			exposeFlag =!exposeFlag ;
			return true ;
        }
        return false ;
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		// Activity Complete Destory
		if (resultCode == Activity.RESULT_OK)
			// InformationInput에서 호출한 경우에만 처리합니다.
			if (requestCode == INTERVAL_RESULT_OK)
				if (!getIntent ().getStringExtra("interval").equals(""))
		 			INTERVAL = Integer.parseInt (getIntent ().getStringExtra("interval")) ;
	}
	
	private class TimerHandlerCallback implements Handler.Callback {
		public boolean handleMessage (Message msg) {
			 
			switch (msg.what) {
			case CAPTURE : 
				try {
					if (msg.what == 0)  
						camera.setOneShotPreviewCallback(timerShutter) ;
				} catch (Exception e) {}
			}
			
			return true ;
		}
	}
}
