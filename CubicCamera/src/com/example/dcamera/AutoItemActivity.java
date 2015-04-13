package com.example.dcamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Files;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.graphics.Bitmap; 

public class AutoItemActivity extends Activity {
	
	MainActivity mainAct ;
	PreViewThread auto ;	// 프레임화 스레드
	SensorThread sensorThread ;	// 센서 스레드

	private int imgNum ;	// 현재 보여지는 이미지의 index
	private float preX, preRoll ;	// 터치 좌표, 센서 값
	private boolean motionFlag ;	// 센서 플래그
	private ArrayList <Bitmap> img ;	// 이지미 저장 리스트
	private LinearLayout linearLay ;		// 메뉴 레이아웃
	private ImageView autoImg ;
	private VelocityTracker velocityTracker ;
	private Handler preHandler =new Handler () {
		public void handleMessage (Message msg) {
			switch (msg.what) {
			case 0 :	// 보낸 메세지가 0일때
				// 찍은 사진을 프레임화
				if (img.size () > 0) 
					imgNum =onPreView (imgNum+1) ;
				break ;
			case 1 :	// 보낸 메세지가 1일때
				// 현재 핸드폰의 회전 값을 통해 찍은 사진을 프레임화
				if (img.size () > 0) {
					// 핸드폰 회전 limit값을 42도로 설정 각 사진 마다 각도가 부여됨
					int size =42 /img.size() ;
					// 현재 회전 각도와 이전 각도의 차이 계산
					float tempVal =(Float)msg.obj -preRoll ;
					// 왼쪽 회전
					if (imgNum < img.size () -1 && tempVal > size) {
						imgNum =onPreView (imgNum +1) ;
					}
					// 오른쪽 회전
					else if (imgNum > 0 && tempVal < size *(-1)) {
						imgNum =onPreView (imgNum -1) ;
					}
				}
				preRoll =(Float)msg.obj ;	// 현재 회전 값을 저장
				break ;
			}
		}
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView (R.layout.item) ;
		
		img =new ArrayList<Bitmap> () ;
		
		linearLay =(LinearLayout) findViewById (R.id.item_menu) ;
		
		// play클릭 레이아웃 셋
		ImageButton play =(ImageButton) findViewById (R.id.item_play) ;
		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(
				(int)mainAct.scaleHeight /10, (int)mainAct.scaleHeight /10) ;
		params.addRule(RelativeLayout.CENTER_HORIZONTAL) ;
		params.setMargins(15, 0, 15, 0) ;
		play.setLayoutParams(params) ;
		play.setAdjustViewBounds(true) ;
		play.setScaleType(ScaleType.FIT_XY) ;
		
		// motion 여부 레이아웃 셋
		ImageButton motion =(ImageButton) findViewById (R.id.item_motion) ;
		params =new RelativeLayout.LayoutParams(
				(int)mainAct.scaleHeight /10, (int)mainAct.scaleHeight /10) ;
		params.addRule(RelativeLayout.RIGHT_OF, R.id.item_play) ;
		params.setMargins(15, 0, 15, 0) ;
		motion.setLayoutParams(params) ;
		motion.setAdjustViewBounds(true) ;
		motion.setScaleType(ScaleType.FIT_XY) ;
		
		// 센서 스레드
		sensorThread =new SensorThread (this, preHandler) ;
		sensorThread.setDaemon(true) ;
		sensorThread.start() ;
		
		// 프레임화 스레드
		auto =new PreViewThread (preHandler) ;
		auto.setDaemon(true) ;
		auto.start() ;
	}

	// 각 사진을 프레임처럼 표현
	public int onPreView (int num) {
		if (num >= img.size()) num =0 ;	// index가 이미지보다 커지면 0으로 초기화
		else if (num < 0) num =img.size () -1 ;	// index가 0보다 작아지면 마지막으로 초기화
		
		autoImg.setImageBitmap (img.get(num)) ;	// 이미지 변경
		return num ;
	}
	
	// play 클릭
	public void autoClick (View v) {
		auto.autoFlag =!auto.autoFlag ;	// 스레드 시작 중지
		ImageButton play =(ImageButton) findViewById (R.id.item_play) ;
		// 프레임 스레드가 구동 될 때 
		if (auto.autoFlag) {
			play.setBackgroundResource(R.drawable.stop_selector) ;
			sensorThread.stopSensorFlag =true ;	// 센서 스레드를 멈춘다.
		}
		// 프레임 스레드가 중지 될 때
		else {
			// 센서 스레드가 중지 되었을 때 스레드 구동
			play.setBackgroundResource(R.drawable.play_selector) ;
			if (!motionFlag)
				sensorThread.startSensorFlag =true ;
		}
	}
	
	// motion 클릭
	public void motionClick (View v) {
		motionFlag =!motionFlag ;	// 스레드 시작 중지
		ImageButton motion =(ImageButton) findViewById (R.id.item_motion) ;
		// 센서 스레드가 중지 될 때
		if (motionFlag) {
			motion.setBackgroundResource(R.drawable.non_motion_selector) ;
			sensorThread.stopSensorFlag =true ;	// 센서 스레드를 멈춘다.
		}
		// 센서 스레드가 구동 될 때
		else {
			motion.setBackgroundResource(R.drawable.motion_selector) ;
			sensorThread.startSensorFlag =true ;	// 센서 스레드 구동
		}
	}
	
	// 시간순 정렬
	public void sortFiles (File [] files) {
		// 기본 sort에서 시간순 정렬 추가
		Arrays.sort (files, new Comparator<Object> () {
			public int compare (Object obj1, Object obj2) {
				String s1 =((File)obj1).lastModified () +"" ;
				String s2 =((File)obj2).lastModified () +"" ;
			
				return s1.compareTo (s2) ;	// s1과 s2 비교
			}
		}) ;
	}
	
	// 파일 이름 변경
	public void renameFiles (String path) {
		// 파라미터로 받은 경로로 파일 설정 후 하위 파일 탐색
		File pathFile =new File (path) ;
		File[] childFiles =pathFile.listFiles() ;
		String dir ="/Cubic_" ;

		sortFiles (childFiles) ;	// listFiles()로 얻어낸 하위 파일들은 시간 순으로 정렬
		// 0부분은 .nomedia파일이므로 생략
		for (int i =1 ; i < childFiles.length ; i++) {
			// 순서에 따라 파일 탐색
			File temp =new File (path +dir +i) ;
			if (!temp.exists()) {	// 파일이 없으면
				try {
					// 정렬된 하위파일의 이름을 변경
					childFiles[i].renameTo(new File (path +dir +i)) ;
				} catch (Exception e) {}
			}
		}
	}
	
	// 파일 삭제
	public void deleteFiles (String path) {
		// 파라미터로 받은 경로로 파일 설정 후 하위 파일 탐색
		File pathFile =new File (path) ;
		File[] childFiles =pathFile.listFiles() ;
		
		for (File childFile : childFiles) {
			// 하위 파일가 존재 할 경우 재귀호출로 하위 파일 먼저 삭제
			if (childFile.isDirectory()) 	
				deleteFile (childFile.getAbsolutePath()) ;
			// 하위 파일이 없으면 삭제
			else childFile.delete() ;
		}
		pathFile.delete() ;	// 현재 파일 삭제
	}
	
	// delete 클릭
	public void deleteClick (View v) {
		// Intent를 통해 받은 값과 기본경로 및 어플 폴더 경로 설정
		Bundle bundle = getIntent().getExtras();
		File pathOrg = Environment.getExternalStorageDirectory();
		String dir = "/CubicCamera/Cubic_";
		
		deleteFiles (pathOrg +dir +bundle.getInt("item")) ;	// 삭제
		renameFiles (pathOrg +"/CubicCamera") ;	// 나머지 파일들의 이름 재 설정
				
		img.removeAll(img);	// 리스트 초기화
		// 구동중인 스레드 종료
		auto.interrupt () ;
		auto.runFlag =true ;
		sensorThread.interrupt() ;
		sensorThread.runFlag =true ;
		finish(); // Activity를 종료하고
	}
	
	// add 클릭
	public void addClick (View v) {
		linearLay.setVisibility(View.GONE) ;	// 메뉴 레이아웃을 없애고
		// cameraActivity Intent 연결
		Intent camera =new Intent (this, CameraActivity.class) ;
		startActivity (camera) ;
	}
	
	// 터치에 대한 이벤트
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

	     // 현재 측정된 터치 지점의 x, y 좌표
        float X = event.getX();

        // 인스턴스 생성
        if (velocityTracker == null) {
        	velocityTracker =VelocityTracker.obtain() ;
        }
        velocityTracker.addMovement(event) ;
        
		switch (event.getAction() & event.ACTION_MASK) { // 이벤트를 가져오고
		case MotionEvent.ACTION_MOVE :	// 드래그 일때
	          float dX = X - preX ;	// 현재 터치 좌표와 이전 터치좌표를 통한 값 계산

	          if (dX > 10.0f)  imgNum++ ;	// 왼쪽 드래그
	          else if (dX < 0 && dX > -10.0f)  imgNum-- ;	// 오른쪽 드래그
	          
	          imgNum =onPreView (imgNum) ;	// 화면을 갱신한다.
	          break ;
		case MotionEvent.ACTION_DOWN :	// 누름 일때
			// 메뉴 레이아웃이 보이면 없앤다.
			if (linearLay.getVisibility() == View.VISIBLE) {
				linearLay.setVisibility(View.GONE) ;

				return true ;
			}
		}
		// 이전 터치 지점 변경
        preX = X ;	// 현재 터치된 좌표 저장
		return true;
	}
	
	// 화면이 보이고 있을 때 여부
	public void onWindowFocusChanged (boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus) ;
		// 화면이 현재 보이고 이미지 사이즈가 0이 아니면
		if (hasFocus && img.size() == 0) {
			// Intent를 통해 받은 값과 기본경로 및 어플 폴더 경로 설정
			Bundle bundle = getIntent().getExtras();
			File pathOrg = Environment.getExternalStorageDirectory();
			String dir = "/CubicCamera/Cubic_" + bundle.getInt("item");
			int num = 0;
			// Load
			try {
				File pathApp = new File(pathOrg + dir + "/Cubic_" + num+ ".png"); // Load경로
				// if (!pathApp.isDirectory()) pathApp.mkdirs(); // 없으면 만듬

				while (true) {
					if (pathApp.exists()) {	// 이미지 파일이 있으면
						img.add(BitmapFactory.decodeFile(pathApp + ""));	// 이미지를 Load하여 리스트에 추가
						pathApp = new File(pathOrg + dir + "/Cubic_" + (++num) + ".png");	// 다음 이미지 탐색
					}
					// 이미지 파일이 없으면 루프 탈출
					else break ;
				}
			} catch (Exception e) {
				Toast.makeText(this, "사진을 불러오는 도중 오류가 발생 하였습니다.",Toast.LENGTH_LONG).show();
				finish();
			}
			// 처음 보여지는 이미지 셋
			autoImg =(ImageView) findViewById (R.id.item_autoImg) ;
			autoImg.setImageBitmap(img.get(0)) ;	
		}
	}

	// 뒤로 가기 버튼클릭시
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (KeyCode == KeyEvent.KEYCODE_BACK) {	
				// 메뉴 레이아웃이 보이면 없앤다.
				if (linearLay.getVisibility() == View.VISIBLE) {
					linearLay.setVisibility(View.GONE) ;

					return true ;
				}
				// 메뉴 레이아웃이 보이지 않을 때
				img.removeAll(img);	// 리스트 초기화
				sensorThread.stopSensorFlag =true ;
				finish(); // Activity를 종료하고

				return true;
			}
			else if (KeyCode == KeyEvent.KEYCODE_MENU) {
				if (linearLay.getVisibility() == View.GONE)
					linearLay.setVisibility(View.VISIBLE) ;
				else linearLay.setVisibility(View.GONE) ;
				//openOptionsMenu(); 
				
				return true ;
			}
		}
		return super.onKeyDown(KeyCode, event);
	}
	
	// Activity Restart
	public void onResume () {
		super.onResume () ;
		sensorThread.startSensorFlag =true ;	// 센서 스레드 구동
	}
	
	// Activity Pause
	public void onPause () {
		super.onPause() ;
		sensorThread.stopSensorFlag =true ;	// 센서 스레드 중지
	}
	
	// Acitivity Stop
	public void onStop () {
		super.onStop() ;
		// 구동중인 스레드 중지
		auto.interrupt () ;
		auto.runFlag =true ;
		sensorThread.runFlag =true ;
		sensorThread.interrupt() ;
	}
}
