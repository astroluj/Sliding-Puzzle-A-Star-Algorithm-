package com.example.fillinfillin;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public class ArrayFill3D 
{
	MainActivity mainAct ;
	PlayCrazy playC ;
	
	private final int WIDTH =mainAct.FILL_WIDTH, HEIGHT =mainAct.FILL_HEIGHT,
			SHAPE =mainAct.FILL_SHAPE_COUNT ;
	
	public ArrayFill3D (Context context)
	{
		super () ;
		
	}
	
	// 비교하여 바꾸기
	protected void changeFill(int orgImg) {
		playC.coord[0][0][0] = true; // (0, 0)은 항상 true
		checkFill(); // (0, 0)부터 탐색

		BitmapFactory.Options resizeOption = new Options();
		resizeOption.inSampleSize = 4;

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				for (int k = 0; k < WIDTH; k++) {

					if (playC.coord[i][j][k]) { // 좌표가 같은 flood상태 일 때
						playC.bitmapId[i][j][k] = orgImg; // ResourceId로 (0, 0)의 Id로
													// 변경
					}
				}
			}
		}
	}

	// (0, 0)부터 같은색의 범위 찾기
	protected void checkFill() {
		int index = 0;
		Data data = new Data(), tempData = new Data();

		while (playC.dataList.size() > 0) {

			int cnt = 0, row = 0, col = 0, face = 0, tempFace = 0;
			data = playC.dataList.get(index);

			row = data.getRow();
			col = data.getCol();
			face = data.getFace();
			tempFace = face;

			// Right
			if (col + 1 == WIDTH)
				tempData = checkFace(face, data, 0);
			else
				tempData.setData(data.getFace(), data.getRow(),
						data.getCol() + 1);

			if (!playC.coord[tempData.getFace()][tempData.getRow()][tempData.getCol()]) {
				if (playC.bitmapId[0][0][0] == playC.bitmapId[tempData.getFace()][tempData
						.getRow()][tempData.getCol()]) {

					playC.coord[tempData.getFace()][tempData.getRow()][tempData
							.getCol()] = true;
					cnt++;
					playC.dataList.add(tempData.clone());
					playC.checkCnt++;
				}
			} else
				cnt++;

			// Down
			if (row + 1 == HEIGHT)
				tempData = checkFace(face, data, 1);
			else
				tempData.setData(data.getFace(), data.getRow() + 1,
						data.getCol());

			if (!playC.coord[tempData.getFace()][tempData.getRow()][tempData.getCol()]) {
				if (playC.bitmapId[0][0][0] == playC.bitmapId[tempData.getFace()][tempData
						.getRow()][tempData.getCol()]) {

					playC.coord[tempData.getFace()][tempData.getRow()][tempData
							.getCol()] = true;
					cnt++;
					playC.dataList.add(tempData.clone());
					playC.checkCnt++;
				}
			} else
				cnt++;

			// Left
			if (col == 0)
				tempData = checkFace(face, data, 2);
			else
				tempData.setData(data.getFace(), data.getRow(),
						data.getCol() - 1);

			if (!playC.coord[tempData.getFace()][tempData.getRow()][tempData.getCol()]) {
				if (playC.bitmapId[0][0][0] == playC.bitmapId[tempData.getFace()][tempData
						.getRow()][tempData.getCol()]) {

					playC.coord[tempData.getFace()][tempData.getRow()][tempData
							.getCol()] = true;
					cnt++;
					playC.dataList.add(tempData.clone());
					playC.checkCnt++;
				}
			} else
				cnt++;

			// Up
			if (row == 0)
				tempData = checkFace(face, data, 3);
			else
				tempData.setData(data.getFace(), data.getRow() - 1,
						data.getCol());

			if (!playC.coord[tempData.getFace()][tempData.getRow()][tempData.getCol()]) {
				if (playC.bitmapId[0][0][0] == playC.bitmapId[tempData.getFace()][tempData
						.getRow()][tempData.getCol()]) {

					playC.coord[tempData.getFace()][tempData.getRow()][tempData
							.getCol()] = true;
					cnt++;
					playC.dataList.add(tempData.clone());
					playC.checkCnt++;
				}
			} else
				cnt++;

			if (cnt == 4)
				playC.dataList.remove(index);
			else if (index + 1 == playC.dataList.size())
				break;
			else
				index++;

			if (index >= playC.dataList.size())
				break;
		}
	}

	// 면이 바뀔 때 좌표 바꾸기
	protected Data checkFace(int face, Data data, int state) {
		Data tempData = new Data(data);

		// right
		if (state == 0) {
			if (face == 0) {
				tempData.setData(1, -1, 0);
				return tempData.clone();
			} else if (face == 1) {
				tempData.setData(2, -1, 0);
				return tempData.clone();
			} else if (face == 2) {
				tempData.setData(3, -1, 0);
				return tempData.clone();
			} else if (face == 3) {
				tempData.setData(0, -1, 0);
				return tempData.clone();
			} else if (face == 4) {
				tempData.setData(1, HEIGHT - 1, tempData.getRow());
				return tempData.clone();
			} else {
				tempData.setData(1, 0, tempData.getRow());
				return tempData.clone();
			}
		}
		// down
		else if (state == 1) {
			if (face == 0) {
				tempData.setData(4, 0, -1);
				return tempData.clone();
			} else if (face == 1) {
				tempData.setData(4, tempData.getCol(), WIDTH - 1);
				return tempData.clone();
			} else if (face == 2) {
				tempData.setData(4, HEIGHT - 1, WIDTH - (tempData.getCol() + 1));
				return tempData.clone();
			} else if (face == 3) {
				tempData.setData(4, tempData.getCol(), 0);
				return tempData.clone();
			} else if (face == 4) {
				tempData.setData(2, HEIGHT - 1, WIDTH - (tempData.getCol() + 1));
				return tempData.clone();
			} else {
				tempData.setData(0, 0, -1);
				return tempData.clone();
			}
		}
		// left
		else if (state == 2) {
			if (face == 0) {
				tempData.setData(3, -1, WIDTH - 1);
				return tempData.clone();
			} else if (face == 1) {
				tempData.setData(0, -1, WIDTH - 1);
				return tempData.clone();
			} else if (face == 2) {
				tempData.setData(1, -1, WIDTH - 1);
				return tempData.clone();
			} else if (face == 3) {
				tempData.setData(2, -1, WIDTH - 1);
				return tempData.clone();
			} else if (face == 4) {
				tempData.setData(3, HEIGHT - 1, tempData.getRow());
				return tempData.clone();
			} else {
				tempData.setData(3, 0, tempData.getRow());
				return tempData.clone();
			}
		}
		// up
		else {
			if (face == 0) {
				tempData.setData(5, HEIGHT - 1, -1);
				return tempData.clone();
			} else if (face == 1) {
				tempData.setData(5, tempData.getCol(), WIDTH - 1);
				return tempData.clone();
			} else if (face == 2) {
				tempData.setData(5, 0, WIDTH - (tempData.getCol() + 1));
				return tempData.clone();
			} else if (face == 3) {
				tempData.setData(5, tempData.getCol(), 0);
				return tempData.clone();
			} else if (face == 4) {
				tempData.setData(0, HEIGHT - 1, -1);
				return tempData.clone();
			} else {
				tempData.setData(2, 0, WIDTH - (tempData.getCol() + 1));
				return tempData.clone();
			}
		}
	}

	public void recycle ()
	{
		for (int i =0 ; i < SHAPE ; i++)
			playC.img[i].recycle() ;
		for (int i =0 ; i < 6 ; i++)
			playC.setImg[i].recycle () ;
		playC.pattern.recycle() ;
	}
}
