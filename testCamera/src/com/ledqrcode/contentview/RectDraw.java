package com.ledqrcode.contentview;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import com.ledqrcode.basic.Coordinate;
import com.ledqrcode.basic.Scale;

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
import android.util.Log;
import android.view.View;

public class RectDraw extends View {

	private final int MIN_SECTOR = 10;

	private Coordinate leftTopCoordinate, rightBottomCoordinate;
	private Scale scale;

	private ArrayList<Bitmap> img ;
	private ArrayList<Long> captureTime;
	private ArrayList<Integer> acii_str;

	private String textLED ;
	
	public RectDraw(Context context, Scale scale) {

		super(context);

		this.scale = scale;
		this.leftTopCoordinate = new Coordinate();
		this.rightBottomCoordinate = new Coordinate();

		img =new ArrayList <Bitmap> () ;
		captureTime =new ArrayList <Long> () ;
		acii_str =new ArrayList <Integer> () ;
		
		textLED ="" ;
	}

	// LED Table Drawing
	public void onDrawTable(Bitmap img) {
		int imageWidth = img.getWidth() / 4, imageHeight = img.getHeight() / 4;
		byte aciiValue = 0x00;

		int aciiValue_1 = 0, aciiValue_2 = 0;

		Canvas canvas = new Canvas(img);
		// canvas.setBitmap(img);

		Paint paint = new Paint();
		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.WHITE);
		paint.setTextSize(imageWidth / 2);

		for (int i = 0, bitCnt = 1; i < 4; i++) {
			for (int j = 0; j < 4; j++, bitCnt++) {
				// 각 영역별 LED STATE
				if (comparePixel(imageHeight * i, imageHeight * (i + 1),
						imageWidth * j, imageWidth * (j + 1), img))
					aciiValue |= 0x01;
				else if (emptyFlag)
					return; // emptyImg�ϱ� ����

				// 8bit �� ����Ÿ�� �о ó��
				if ((bitCnt % 8) == 0) {
					if (aciiValue == 0x00) {
						emptyFlag = true;
						return;
					}
					// �Ϲ����� �ƽ�Ű �ڵ� ��
					else if (' ' <= acii && acii <= '~') {
						if (aciiValue_1 == 0)
							aciiValue_1 = aciiValue;
						else
							aciiValue_2 = aciiValue;
					}
					// Ư���� �ƽ�Ű �ڵ尪
					else {
						emptyFlag = true;
						return;
					}
					aciiValue = 0x00;
				}

				// Shift Left 1
				aciiValue <<= 1;
			}
		}

		acii_str.add(aciiValue_1);
		acii_str.add(aciiValue_2);
	}

	// Drawing Rectangle
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Paint paint = new Paint();
		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.WHITE);

		// Draw Rectangle
		canvas.drawRect(leftTopCoordinate.getX(), leftTopCoordinate.getY(),
				rightBottomCoordinate.getX(), rightBottomCoordinate.getY(),
				paint);

		paint.setTextSize(20);
		canvas.drawText("Left : " + leftTopCoordinate.getX() + " Top : "
				+ leftTopCoordinate.getY() + " Right : "
				+ rightBottomCoordinate.getX() + " Bottom : "
				+ rightBottomCoordinate.getY(), 20, 20, paint);
		canvas.drawText(
				"width : "
						+ Math.abs(leftTopCoordinate.getX()
								- rightBottomCoordinate.getX())
						+ " height : "
						+ Math.abs(leftTopCoordinate.getY()
								- rightBottomCoordinate.getY()), 20, 40, paint);

		paint.setTextSize(30);
		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(textLED, scale.getScaleWidht() / 2, 600, paint);
	}

	public void setRepeatAcii() {
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

	public void sortInterval() {

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

	// ReadImage
	public void ImageAnalyze() {
		// 캡쳐 멈춤
		camera.stopPreview();
		camera.setOneShotPreviewCallback(null);
		camera.startPreview(); // View 깜빡임 효과

		// 현재 카메라가 차지하는 크기
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

	// Get LED State
	private boolean comparePixel(int y, int height, int x, int width, Bitmap img) {
		int colorCnt = 0;
		int redSector, greenSector, blueSector;

		// LED Color Example Yellow
		for (int i = y; i < height; i++) {
			for (int j = x; j < width; j++) {
				// RGB Selector
				redSector = (img.getPixel(j, i) & 0x00ff0000) >> 16;
				greenSector = (img.getPixel(j, i) & 0x0000ff00) >> 8;
				blueSector = img.getPixel(j, i) & 0x000000ff;

				// Log.d ("D", "red : " +redSector +" green : " +greenSector
				// +" blue : " +blueSector) ;
				if (Math.abs(redSector - greenSector) > 50)
					continue;

				if (65 <= redSector && 65 <= greenSector && blueSector <= 50) {
					// img.setPixel(j, i, Color.RED) ;
					if (++colorCnt > (((height - y) * (width - x)) / 5) * 2)
						return true;

				}

				// ������� 5����2 �̻� ������ ���� �Ŷ��
				if (colorCnt + (height * width) - (i * j) < (((height - y) * (width - x)) / 5) * 2) {
					// ������� ������� ���� ��� ��������
					if (colorCnt >= ((height - y) * (width - x)) / MIN_SECTOR)
						emptyFlag = true;

					return false;
				}
			}
		}

		return true;
	}
}
