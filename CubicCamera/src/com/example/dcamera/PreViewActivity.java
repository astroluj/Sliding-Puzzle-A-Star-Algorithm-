package com.example.dcamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class PreViewActivity extends Activity {

	MainActivity mainAct ;
	CameraActivity cameraAct;	// 카메라 화면
	PreViewThread auto ;		// 프레임화
	SensorThread sensorThread ;	// 센서
	
	private boolean saveFlag, motionFlag ;	// 저장 및 센서 플래그
	private float preX, preRoll ;	// 터치 좌표, 센서 값
	private RelativeLayout viewRel ;	// 찍은 화면이 보여질 레이아웃
	private ImageView autoImg ;
	private Toast toast ; // toast 중복 방지
	private VelocityTracker velocityTracker ;
	private Handler preHandler =new Handler () {
		public void handleMessage (Message msg) {
			switch (msg.what) {
			case 0 :	// 보낸 메세지가 0일때
				// 찍은 사진을 프레임화
				if (cameraAct.img.size() > 0) 
					cameraAct.imgNum =onPreView (cameraAct.imgNum+1) ;
				break ;
			case 1 :	// 보낸 메세지가 1일때
				// 현재 핸드폰의 회전 값을 통해 찍은 사진을 프레임화
				if (cameraAct.img.size () > 0) {
					// 핸드폰 회전 limit값을 42도로 설정 각 사진 마다 각도가 부여됨
					int size =42 /cameraAct.img.size() ;	
					// 현재 회전 각도와 이전 각도의 차이 계산
					float tempVal =(Float)msg.obj -preRoll ;
					// 왼쪽 회전
					if (cameraAct.imgNum < cameraAct.img.size () -1 && tempVal > size) {
						cameraAct.imgNum =onPreView (cameraAct.imgNum +1) ;
					}
					// 오른쪽 회전
					else if (cameraAct.imgNum > 0 && tempVal < size *(-1)) {
						cameraAct.imgNum =onPreView (cameraAct.imgNum -1) ;
					}
				}
				preRoll =(Float)msg.obj ;	// 현재 회전 값을 저장 
				break ;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview);
		
		ImageButton play =(ImageButton) findViewById (R.id.preview_play),
				save =(ImageButton) findViewById (R.id.preview_save),
				motion =(ImageButton) findViewById (R.id.preview_motion) ;
		
		// save 클릭 레이아웃 셋
		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(
				(int)mainAct.scaleHeight /10, (int)mainAct.scaleHeight /10) ;
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT) ;
		params.setMargins(15, 0, 15, 0) ;
		save.setLayoutParams(params) ;
		save.setAdjustViewBounds(true) ;
		save.setScaleType(ScaleType.FIT_XY) ;
		
		// play 클릭 레이아웃 셋
		params =new RelativeLayout.LayoutParams(
				(int)mainAct.scaleHeight /10, (int)mainAct.scaleHeight /10) ;
		params.addRule(RelativeLayout.CENTER_HORIZONTAL) ;
		params.setMargins(15, 0, 15, 0) ;
		play.setLayoutParams(params) ;
		play.setAdjustViewBounds(true) ;
		play.setScaleType(ScaleType.FIT_XY) ;
		
		// motion 여부 레이아웃 셋
		params =new RelativeLayout.LayoutParams(
				(int)mainAct.scaleHeight /10, (int)mainAct.scaleHeight /10) ;
		params.addRule(RelativeLayout.RIGHT_OF, R.id.preview_play) ;
		params.setMargins(15, 0, 15, 0) ;
		motion.setLayoutParams(params) ;
		motion.setAdjustViewBounds(true) ;
		motion.setScaleType(ScaleType.FIT_XY) ;
		
		// 처음에 화면에 비춰질 사진 셋
		autoImg =(ImageView) findViewById (R.id.preview_autoImg) ;
		autoImg.setImageBitmap(cameraAct.img.get(0)) ;
		
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
		if (num >= cameraAct.img.size()) num =0 ;	// index가 이미지보다 커지면 0으로 초기화
		else if (num < 0) num =cameraAct.img.size () -1 ;	// index가 0보다 작아지면 마지막으로 초기화
		
		autoImg.setImageBitmap (cameraAct.img.get(num)) ; 	// 이미지 변경
		
		return num ;
	}
	
	// auto 클릭
	public void autoClick (View v) {
		auto.autoFlag =!auto.autoFlag ;	// 스레드 시작 중지
		ImageButton play =(ImageButton) findViewById (R.id.preview_play) ;
		// 프레임 스레드가 구동 될 때 
		if (auto.autoFlag) {
			play.setBackgroundResource(R.drawable.stop_selector) ;
			sensorThread.stopSensorFlag =true ;	// 센서 스레드를 멈춘다.
		}
		// 프레임 스레드가 중지 될 때
		else {
			play.setBackgroundResource(R.drawable.play_selector) ;
			// 센서 스레드가 중지 되었을 때 스레드 구동
			if (!motionFlag)
				sensorThread.startSensorFlag =true ;
		}
	}
	
	// motion 클릭
	public void motionClick (View v) {
		motionFlag =!motionFlag ;	// 스레드 시작 중지
		ImageButton motion =(ImageButton) findViewById (R.id.preview_motion) ;
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
	
	// save 클릭
	public void saveClick (View v) {
		// 저장할 이미지가 없을 때
		if (cameraAct.img.size () == 0) {
			if (toast == null)
				toast =Toast.makeText(this, "저장 할 사진이 없습니다.", Toast.LENGTH_LONG) ;
			toast.show() ;
			return ;
		}
	
		// 이미지가 있을 때
		final ProgressDialog saver = new ProgressDialog(this);
		saver.setMessage("저장 중입니다.");	// 프로그레스바 노출	
		saver.show() ;
		
		// 기본 경로와 어플 폴더 경로 설정
		File pathOrg =Environment.getExternalStorageDirectory() ;
		String dir ="/CubicCamera/Cubic_" ;
		int num =1 ;
		// 저장
		try {
			File pathApp =new File (pathOrg +dir +num) ; // 저장경로
			//if (!pathApp.isDirectory()) pathApp.mkdirs(); // 없으면 만듬

			while (true) {
				if (pathApp.isDirectory())	// 경로상의 폴더가 있으면
					pathApp =new File (pathOrg +dir +(++num)) ;	// 다음 폴더 탐색
				else {	// 경로상의 폴더가 없으면
					pathApp.mkdirs() ;	// 폴더 생성
					break ;
				}
			}
			// png확장자로 생성된 폴더에 저장
			for (int i = 0; i < cameraAct.img.size(); i++) {
				FileOutputStream outFile = new FileOutputStream(pathApp +"/Cubic_" + (i) + ".png"); // 저장
				cameraAct.img.get(i).compress(Bitmap.CompressFormat.PNG, 100, outFile); // 비트맵을PNG로
			}
		} catch (FileNotFoundException e) {
			Toast.makeText(this, "사진을 저장하는 도중 오류가 발생 하였습니다.", Toast.LENGTH_LONG).show () ;
			finish () ; 
			}
		saver.dismiss() ;		// 프로그레스바 제거
		saveFlag =true ;	// 저장 했다는 것을 체크
		Toast.makeText(this, "사진이 저장 되었습니다.", Toast.LENGTH_LONG).show () ;
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
		case MotionEvent.ACTION_MOVE :	// 드래그 일 때
	          float dX = X - preX;	// 현재 터치 좌표와 이전 터치좌표를 통한 값 계산

	          if (dX > 10.0f)  cameraAct.imgNum++ ;	// 왼쪽 드래그
	          else if (dX < 0 && dX > -10.0f)  cameraAct.imgNum-- ;	// 오른쪽 드래그
	          
	          cameraAct.imgNum =onPreView (cameraAct.imgNum) ;	// 화면을 갱신
		}
        preX = X ;	// 현재 터치된 좌표 저장
		return true;
	}
	
	// 뒤로 가기 버튼클릭시
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (KeyCode == KeyEvent.KEYCODE_BACK) {
				// 저장을 안한 상태로 뒤로 가기 버튼을 눌렀을 때 행동에 대한 대화상자 띄움
				if (!saveFlag) {
					AlertDialog.Builder altYN = new AlertDialog.Builder(this); 
					// 대화상자 설정 메세지를 설정하고 대화상자를 지울 수 있는지 설정 Yes버튼의 Text와 그에 따른 리스너 설정
					altYN.setMessage("이어서 찍겠습니까?").setCancelable(true)
							.setPositiveButton("Connect",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											sensorThread.stopSensorFlag =true ;
											finish();	// 이어서 찍을 경우 Activity만 종료
										}
										// No버튼의 Text와 그에 따른 리스너 설정
									})
							.setNegativeButton("new Capture",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											dialog.cancel(); // 대화상자를 닫음
											// 새로 찍을 경우 찍었던 사진들은 삭제
											cameraAct.img.removeAll(cameraAct.img);
											sensorThread.stopSensorFlag =true ;
											finish();
										}
									});
					AlertDialog alt = altYN.create(); // 대화상자의 유형
					alt.setTitle("ReCapture"); // Title의 Text 설정
					alt.show(); // 화면을 띄움
				}
				// 저장을 한 상태 일 경우
				else {
					// 찍었던 사진들은 삭제
					cameraAct.img.removeAll(cameraAct.img) ;
					sensorThread.stopSensorFlag =true ;
					finish();
				}
				return true;
			}
		}
		return super.onKeyDown(KeyCode, event) ;
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
	
	// Activity Stop
	public void onStop () {
		super.onStop() ;
		// 구동중인 스레드 중지
		auto.interrupt () ;
		auto.runFlag =true ;
		sensorThread.interrupt() ;
		sensorThread.runFlag=true ;
	}
}
