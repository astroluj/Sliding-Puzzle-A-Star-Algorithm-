package com.ledqrcode.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class RectDraw extends View {
	
	private boolean emptyFlag ;
	
	public RectDraw(Context context) {
		
		super(context);
		
		this.emptyFlag = false ;
	}

	public boolean getEmptyFlag () {
		return this.emptyFlag ;
	}
	
	public void setEmptryFlag (boolean emptyFlag) {
		this.emptyFlag = emptyFlag ;
	}
	
	public void onDrawTable(Bitmap img) {
		int w = img.getWidth() / 4, h = img.getHeight() / 4;
		byte acii = 0x00;

		int acii_1 = 0, acii_2 = 0;

		Canvas canvas = new Canvas();
		canvas.setBitmap(img);

		Paint paint = new Paint();
		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.WHITE);
		paint.setTextSize(w / 2);

		for (int i = 0, bitCnt = 1; i < 4; i++) {
			for (int j = 0; j < 4; j++, bitCnt++) {
				if (comparePixel(h * i, h * (i + 1), w * j, w * (j + 1), img))
					acii |= 0x01;
				else if (emptyFlag)
					return; // emptyImg�ϱ� ����

				// 8bit �� ����Ÿ�� �о ó��
				if ((bitCnt % 8) == 0) {
					if (acii == 0x00) {
						emptyFlag = true;
						return;
					}
					// �Ϲ����� �ƽ�Ű �ڵ� ��
					else if (' ' <= acii && acii <= '~') {
						if (acii_1 == 0)
							acii_1 = acii;
						else
							acii_2 = acii;
					}
					// Ư���� �ƽ�Ű �ڵ尪
					else {
						emptyFlag = true;
						return;
					}
					acii = 0x00;
				}

				acii <<= 1;
			}
		}

		acii_str.add(acii_1);
		acii_str.add(acii_2);
	}

	// �巡�� �����׸���
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.WHITE);

		canvas.drawRect(x_1, y_1, x_2, y_2, paint);

		paint.setTextSize(20);
		canvas.drawText("x_1 : " + x_1 + " y_1 : " + y_1 + " x_2 : " + x_2
				+ " y_2 : " + y_2, 20, 20, paint);
		canvas.drawText(
				"width : " + Math.abs(x_1 - x_2) + " height : "
						+ Math.abs(y_1 - y_2), 20, 40, paint);

		paint.setTextSize(30);
		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(textLED, scaleWidth / 2, 600, paint);
	}

	private boolean comparePixel(int y, int h, int x, int w, Bitmap img) {
		int colorCnt = 0;
		int redSector, greenSector, blueSector;

		for (int i = y; i < h; i++) {
			for (int j = x; j < w; j++) {
				redSector = (img.getPixel(j, i) & 0x00ff0000) >> 16;
				greenSector = (img.getPixel(j, i) & 0x0000ff00) >> 8;
				blueSector = img.getPixel(j, i) & 0x000000ff;

				// Log.d ("D", "red : " +redSector +" green : " +greenSector
				// +" blue : " +blueSector) ;
				if (Math.abs(redSector - greenSector) > 50)
					continue;

				if (65 <= redSector && 65 <= greenSector && blueSector <= 50) {
					// img.setPixel(j, i, Color.RED) ;
					if (++colorCnt > (((h - y) * (w - x)) / 5) * 2)
						return true;

				}

				// ������� 5����2 �̻� ������ ���� �Ŷ��
				if (colorCnt + (h * w) - (i * j) < (((h - y) * (w - x)) / 5) * 2) {
					// ������� ������� ���� ��� ��������
					if (colorCnt >= ((h - y) * (w - x)) / MIN_SECTOR)
						emptyFlag = true;

					return false;
				}
			}

		}
		return true;
	}
}
