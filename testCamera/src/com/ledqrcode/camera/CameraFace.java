package com.ledqrcode.camera;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

//Camera SurfaceView
public class CameraFace extends SurfaceView implements SurfaceHolder.Callback {
	
	private RectDraw rectDraw ;
	
	private Context context ;
	private SurfaceHolder nHolder;
	private Camera camera ;
	
	public CameraFace(Context context, Camera camera, RectDraw rectDraw) { // ������
		super(context);

		this.context = context ;
		this.camera = camera ;
		this.rectDraw = rectDraw ;
		
		nHolder = getHolder(); // callback���� �����Ȱ��� ������
		nHolder.addCallback(this); // callback ȣ��
		nHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	// Camera Surface View
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open(); // H/W Camera Open
		camera.setDisplayOrientation(90); // 90도 회전
		try {
			camera.setPreviewDisplay(holder); // Camera View Holding
			/*
			 * Camera.Parameters p =camera.getParameters() ;
			 * p.setPictureSize(1300, 1600) ; camera.setParameters(p) ;
			 */
			// Add Canvas in camera View 
			((Activity) context).addContentView(rectDraw, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
		} catch (IOException exception) {
			camera.release();
			camera = null;
		}
	}

	// View Change
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

		// Get Camera Layout Size
		Camera.Parameters parameters = camera.getParameters(); 

		parameters.setPreviewSize(w, h);
		camera.setParameters(parameters);
		camera.startPreview(); 
	}

	// View Destroy
	public void surfaceDestroyed(SurfaceHolder holder) {

		if (camera != null) {
			camera.stopPreview(); // Camera Stopping
			camera.release(); // Release Resource
			camera = null;
		}
	}
}