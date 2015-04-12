package com.ledqrcode.camera;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

//Camera SurfaceView
public class CameraFace extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder nHolder;
	private Camera camera = null;

	public CameraFace(Context context) { // ������
		super(context);

		nHolder = getHolder(); // callback���� �����Ȱ��� ������
		nHolder.addCallback(this); // callback ȣ��
		nHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	// ó�� ī�޶� ȭ�� ������
	public void surfaceCreated(SurfaceHolder holder) {
		camera = CameraFace.open(); // ����� ī�޶� ����
		camera.setDisplayOrientation(90); // ���θ��� ������ 90ȸ��
		try {
			camera.setPreviewDisplay(holder); // �̻������ ȭ�� ���
			/*
			 * Camera.Parameters p =camera.getParameters() ;
			 * p.setPictureSize(1300, 1600) ; camera.setParameters(p) ;
			 */

			rectD = new rectDraw(getApplicationContext()); // �簢���� �׸��� ����
															// ��ü
			addContentView(rectD, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
		} catch (IOException exception) {
			camera.release();
			camera = null;
		}
	}

	private void setRepeatAcii() {
		String tempStr = "";

		Log.d("AAA", textLED);

		while (textLED.length() > 1) {
			String repeat = textLED.substring(0, 2);
			int exposeCnt = 0; // ������ 2�� �̻� �Ǵ��� �Ǵ� �ȵǸ� ���� �ؽ�Ʈ

			for (int i = 0; i < textLED.length(); i += 2) {
				if (textLED.substring(i, i + 2).equals(repeat)) {
					exposeCnt++;
					textLED = textLED.subSequence(0, i)
							+ textLED.substring(i + 2);
					i -= 2;
				}

			}

			if (exposeCnt >= 2 && exposeFlag)
				tempStr += repeat; // � ���ڿ��� 2�� �̻� ������
			else if (!exposeFlag)
				tempStr += repeat;
		}

		// �빮�ڰ� ó������
		for (int i = 0; i < tempStr.length(); i++) {
			if ('A' <= tempStr.charAt(i) && tempStr.charAt(i) <= 'Z') {
				tempStr = tempStr.substring(i) + tempStr.substring(0, i);
				break;
			}
		}
		textLED += tempStr;
	}

	private void sortInterval() {

		long firstTime = captureTime.get(0);

		Log.i("size", "captureCnt : " + captureCnt + " imgBytes.size : "
				+ imgBytes.size() + " captureTime.size : " + captureTime.size());

		ArrayList<Integer> temp = new ArrayList<Integer>();
		temp.add(acii_str.get(0));
		temp.add(acii_str.get(1));

		String tempp = "";
		for (int s : acii_str)
			tempp += (char) s;
		Log.d("DDD", tempp);

		for (int i = 1; i < captureTime.size(); i++) {
			long tempTime = captureTime.get(i);

			if ((tempTime - firstTime) % INTERVAL <= INTERVAL / MIN_SECTOR) {
				temp.add(acii_str.get(i));
				temp.add(acii_str.get(i + 1));
			}
		}

		for (int s : acii_str)
			textLED += (char) s;
		setRepeatAcii();
		rectD.invalidate();

		acii_str.removeAll(acii_str);
		captureTime.removeAll(captureTime);
		imgBytes.removeAll(imgBytes);
		img.removeAll(img);
	}

	private void readImgAcii() {
		// 캡쳐 멈춤
		camera.stopPreview();
		camera.setOneShotPreviewCallback(null);
		camera.startPreview(); // ī�޶� �̸����� ȭ�� �ٽ� ���

		Camera.Parameters params = camera.getParameters();
		int cameraWidth = params.getPreviewSize().width, cameraHeight = params
				.getPreviewSize().height, cameraFormat = params
				.getPreviewFormat();
		float width = Math.abs(x_1 - x_2), height = Math.abs(y_1 - y_2);

		// Create Rectangle
		x_1 = Math.min(x_1, x_2);
		y_1 = Math.min(y_1, y_2);

		Rect area = new Rect(0, 0, cameraWidth, cameraHeight);
		YuvImage yuv;

		Log.i("size", "captureCnt : " + captureCnt + " imgBytes.size : "
				+ imgBytes.size() + " captureTime.size : " + captureTime.size());
		for (int i = 0, delCnt = 0; i < captureCnt; i++) {
			Bitmap tempImg;

			// prieview Capture Image to YuvImage
			yuv = new YuvImage(imgBytes.get(i), cameraFormat, cameraWidth,
					cameraHeight, null);

			ByteArrayOutputStream outByte = new ByteArrayOutputStream();
			yuv.compressToJpeg(area, 100, outByte);

			tempImg = BitmapFactory.decodeByteArray(outByte.toByteArray(), 0,
					outByte.size());

			Matrix m = new Matrix();
			m.postRotate(90); // 90도 회전
			tempImg = Bitmap.createBitmap(tempImg, 0, 0, tempImg.getWidth(),
					tempImg.getHeight(), m, true); // ȸ��
			tempImg = Bitmap.createBitmap(tempImg,
					(int) (x_1 * tempImg.getWidth() / scaleWidth), (int) (y_1
							* tempImg.getHeight() / scaleHeight), (int) (width
							* tempImg.getWidth() / scaleWidth), (int) (height
							* tempImg.getHeight() / scaleHeight)); // �ػ󵵷� �߶�

			rectD.onDrawTable(tempImg); // img 그리기

			if (!rectD.getEmptyFlag())
				img.add(tempImg); // ���� �׸��� �ƴϸ� �߰�

			else {
				captureTime.remove(i - delCnt++); // ���� �׸��� ��� ����
				rectD.setEmptryFlag(false);
			}

		}
		x_1 = x_2 = y_1 = y_2 = 0; // 좌표 초기화
	}

	// ī�޶� ȭ���� �ٲ� �� ����
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

		CameraFace.Parameters parameters = camera.getParameters(); // ī�޶�ȭ����
																	// ������
		parameters.setPreviewSize(w, h);
		camera.setParameters(parameters);
		camera.startPreview(); // ������� ȭ�� ���
	}

	// ī�޶� ȭ�� �����
	public void surfaceDestroyed(SurfaceHolder holder) {

		if (camera != null) {
			camera.stopPreview(); // ī�޶� ȭ���� ���߰�
			camera.release(); // Resource ����
			camera = null;
		}
	}

	// ��ġ�� ���� �̺�Ʈ
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		if (timerThread.captureFlag)
			return false; // �۾� �߿� ��ġ ����

		switch (event.getAction() & event.ACTION_MASK) { // �̺�Ʈ�� ��������
		case MotionEvent.ACTION_DOWN: // �����϶�
			x_1 = event.getX();
			y_1 = event.getY(); // ��ǥ�� ���
			mode = DRAG; // mode�� �巡�׷� ����

			break;
		case MotionEvent.ACTION_MOVE: // �����϶�
			dragFlag = true;
			if (mode == DRAG) { // mode�� �巡���̸�
				x_2 = event.getX();
				y_2 = event.getY(); // ����ǥ������
				rectD.invalidate(); // �簢���� �׸�
			} else if (mode == ZOOM) { // mode�� Zoom�̸�
				x_1 = event.getX(0);
				y_1 = event.getY(0); // �������� 0,1 ��ǥ�� ������
				x_2 = event.getX(1);
				y_2 = event.getY(1);
				rectD.invalidate();
			}
			break;
		case MotionEvent.ACTION_UP: // ��ġ�� ���� ��
		case MotionEvent.ACTION_POINTER_UP:
			if (mode == ZOOM) {
				mode = NONE;

				break;
			}
			if (camera != null && dragFlag) {
				captureCnt = 0;

				textLED = "";

				timerThread.captureFlag = true;

				dragFlag = !dragFlag;
				captureStartTime = System.currentTimeMillis();
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN: // ��Ƽ��ġ
			dragFlag = true;
			mode = ZOOM;
			x_2 = event.getX();
			y_2 = event.getY();
			rectD.invalidate();
			break;
		case MotionEvent.ACTION_CANCEL:
		default:
			break;
		}
		return true;
	}
}