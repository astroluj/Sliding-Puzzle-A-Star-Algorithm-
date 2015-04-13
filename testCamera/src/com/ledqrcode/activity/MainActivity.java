package com.ledqrcode.activity;

import java.util.ArrayList;

import com.ledqrcode.R;
import com.ledqrcode.basic.Coordinate;
import com.ledqrcode.basic.Scale;
import com.ledqrcode.contentview.CameraFace;
import com.ledqrcode.contentview.RectDraw;
import com.ledqrcode.thread.TimerThread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends Activity {
	
	private final int NONE =0, DRAG =1, ZOOM =2 ;	// Touch
	private final int MAX_ASCII =10 ;
	private final int INTERVAL_RESULT_OK = 1, CAPTURE = 0 ;
	
	// LED Interval
	private int INTERVAL =80 ;
	
	private TimerThread timerThread ;
	private CameraFace cameraFace ;
	private RectDraw rectDraw ;
	private Scale scale ;
	private Coordinate leftTopCoordinate, rightBottomCoordinate ;

	private Camera camera ;
	private ArrayList<byte[]> imgBytes;
	
	private boolean dragFlag, exposeMenuFlag ;	
	private int mode = NONE ;
	private long captureStartTime  ;
	
	private Handler timerHandlerCallback = new Handler (new TimerHandlerCallback()) ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Get Scale
		DisplayMetrics disM =new DisplayMetrics () ;
		getWindowManager ().getDefaultDisplay().getMetrics(disM) ;
		scale =new Scale (disM) ;

		// RectDraw Init
		rectDraw = new RectDraw (getApplicationContext(), scale) ;
		// CameraFace init
		cameraFace = new CameraFace (getApplicationContext(), camera, rectDraw) ;
		
		// init
		
		imgBytes =new ArrayList <byte []> () ;
		
		dragFlag =false ;
		exposeMenuFlag =false ;
		
		captureStartTime =0 ;
	}
	
	// Camera Display STart
	public void camera (View v) {
		setContentView (cameraFace) ;	
	}
	
	private void startTimerThread () {
		timerThread =new TimerThread (timerHandlerCallback, (long)INTERVAL / 10) ;
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
	
	public boolean onOptionsItemSelected(MenuItem item) {
       
		switch (item.getItemId()) {
		case R.id.menu_interval :		// Setting Interval
        	Intent intent =new Intent (MainActivity.this, IntervalActivity.class) ;
        	startActivityForResult (intent, INTERVAL_RESULT_OK) ;
        	
        	return true ;
        	
		case R.id.menu_expose :
			if (exposeMenuFlag) item.setTitle("Set expose Contorl Enabled") ;
			else item.setTitle("Set expose Contorl disEnabled") ;
			
			exposeMenuFlag =!exposeMenuFlag ;
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
	
	// Camera Preview Callback
	private Camera.PreviewCallback timerShutter = new Camera.PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {

			// Data add
			imgBytes.add(data);

			// Inteval Max Capture 10, Max Ascii 10
			if (timerThread != null 
					&& (System.currentTimeMillis() - captureStartTime) >= (INTERVAL * MAX_ASCII)) {
				// Release Thread
				releaseTimerThread();

				rectDraw.ImageAnalyze(); // Read LED QRCode Data
				rectDraw.sortInterval(); // Data Analyze
			}
		}
	};
	
	private class TimerHandlerCallback implements Handler.Callback {
		public boolean handleMessage (Message msg) {
			 
			switch (msg.what) {
			case CAPTURE : 
				try {
					if (msg.what == 0)  
						// Camera Capture
						camera.setOneShotPreviewCallback(timerShutter) ;
				} catch (Exception e) {}
			}
			
			return true ;
		}
	}
	
	// View Touch Event
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		// Capture 중에 터치 조작 금지
		if (timerThread != null)
			return false; 

		// Multi Touch Accept
		switch (event.getAction() & event.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: // Touching
			// Set Coordinate
			leftTopCoordinate.setCoordinate (event.getX(), event.getY());
			mode = DRAG; // mode Init DRAG

			break;
		case MotionEvent.ACTION_MOVE: // Moving
			dragFlag = true;
			if (mode == DRAG) { // mode Equal DRAG
				// Set Coordinate
				rightBottomCoordinate.setCoordinate (event.getX(), event.getY());
				
			} else if (mode == ZOOM) { // mode Equal ZOOM
				leftTopCoordinate.setCoordinate(0, 0);
				rightBottomCoordinate.setCoordinate(0, 0);
			}
			rectDraw.invalidate(); //View invalidate
			
			break;
		case MotionEvent.ACTION_UP: // Pop Touch and Multi Touch
		case MotionEvent.ACTION_POINTER_UP:
			if (mode == ZOOM) { // mode Equal ZOOM
				mode = NONE;

				break;
			}
			// Dragging
			if (camera != null && dragFlag) {
				
				dragFlag = false ;
				
				// Capture
				// starting Thread
				if (timerThread == null)
					startTimerThread () ;
				
				captureStartTime = System.currentTimeMillis();
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN: // Multi Touch 
			dragFlag = true;
			mode = ZOOM;
			rightBottomCoordinate.setCoordinate(event.getX(), event.getY()) ;
			rectDraw.invalidate();
			
			break;
		case MotionEvent.ACTION_CANCEL:
		default:
			break;
		}
		return true;
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
}
