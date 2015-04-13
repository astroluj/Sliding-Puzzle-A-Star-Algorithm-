package com.example.dcamera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class CameraFace extends SurfaceView implements SurfaceHolder.Callback {

	MainActivity mainAct ;
	CameraActivity cameraAct ;
	
	protected static SurfaceHolder holder ;
	
	private Camera camera = null;
	private Bitmap img;

	public CameraFace(Context context) {
		super(context);

		holder = getHolder(); // callback으로 생성된것을 가져옴
		holder.addCallback(this); // callback 호출
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open(); // 사용할 카메라 생성
		camera.setDisplayOrientation(90); // 가로모드기 때문에 90회전
		try {
			camera.setPreviewDisplay(holder); // 이상없을시 화면 출력
			/*
			 * Camera.Parameters p =camera.getParameters() ;
			 * p.setPictureSize(1300, 1600) ; camera.setParameters(p) ;
			 */
		} catch (IOException exception) {
			camera.release();	// 카메라 해제
			camera = null;
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Camera.Parameters parameters = camera.getParameters(); // 카메라화면의 사이즈
		parameters.setPreviewSize(w, h);
		camera.setParameters(parameters);
		camera.startPreview(); // 사이즈로 화면 출력
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview(); // 카메라 화면을 멈추고
		if (img != null) img.recycle() ;	// 메모리상의 이미지 제거
		camera.release(); // Resource 해제
		camera = null;
	}

	// 터치에 대한 이벤트
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		switch (event.getAction() & event.ACTION_MASK) { // 이벤트를 가져오고
		case MotionEvent.ACTION_UP: // 손이 떼어 질 때
			camera.autoFocus(autoFocus);	// autoFocus 호출
		}
		return true;
	}

	// 미리보기를 가져오기
	public Camera.PreviewCallback timerShutter = new Camera.PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Parameters params = camera.getParameters();
			int cameraWidth = params.getPreviewSize().width, cameraHeight = params
					.getPreviewSize().height, cameraFormat = params
					.getPreviewFormat();

			Rect area = new Rect(0, 0, cameraWidth, cameraHeight);
			YuvImage yuv;
			// prieview로 찍은 건 YuvImage로 처리
			yuv = new YuvImage(data, cameraFormat, cameraWidth, cameraHeight,
					null);
			ByteArrayOutputStream outByte = new ByteArrayOutputStream();
			yuv.compressToJpeg(area, 100, outByte);
			img = BitmapFactory.decodeByteArray(outByte.toByteArray(), 0,
					outByte.size());
			// 회전 및 자르기
			Matrix m = new Matrix();
			m.postRotate(90); // 90도 회전
			img = Bitmap.createBitmap(img, 0, 0, img.getWidth(),
					img.getHeight(), m, true); // 회전
			cameraAct.img.add(Bitmap.createBitmap(img));
		}
	};
	
	// 포커싱 여부
	public Camera.AutoFocusCallback autoFocus = new Camera.AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			if (success) {	// 포커싱이 되었을 때
				shutter.start() ;	// 그화면을 캡쳐하고
				cameraAct.focusFlag =true ;	// 초점 여부 플래그 true
				cameraAct.captureFlag =true ;	// 캡쳐 여부 플래그 true
			}
			//camera.autoFocus(null);

			// requestPreview();
		}
	};
	
	// 캡쳐
	CountDownTimer shutter = new CountDownTimer(1000, 800) {
		public void onTick(long millisUntilFinished) {
			// 미리보기 캡쳐
			camera.setOneShotPreviewCallback(timerShutter);
		}

		public void onFinish() {
			// 찍었다는 표시
			camera.stopPreview();
			camera.setOneShotPreviewCallback(null);
			camera.startPreview(); // 카메라 미리보기 화면 다시 띄움
			cameraAct.afterFlag =true ;	// 잔상 여부 true
			cameraAct.focusFlag =false ;	// 초점 여부 false
		}
	} ;
}
