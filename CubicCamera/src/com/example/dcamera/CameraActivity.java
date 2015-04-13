package com.example.dcamera;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.os.Message; 

public class CameraActivity extends Activity {

	MainActivity mainAct ;
	CameraFace camera;	// 카메라 화면
	FocusThread focusThread ;	// 포커스
	AfterImgThread aftImgThread ;	// 잔상
	PreViewMiniThread auto ;	// 프레임화
	
	protected static ArrayList<Bitmap> img ;	// 찍은 이미지 저장
	protected static int imgNum ;	// 현재 보여지는 이미지의 index
	protected static boolean afterFlag, captureFlag, focusFlag ;
	
	private RelativeLayout viewRel, preRel ;	// viewRel 카메라 화면  preRel 프리뷰 화면
	private ImageView focusImg, autoImg, aftImg ;	
	private Toast toast  =null ;	// toast 중복 방지
	private Handler handler =new Handler () {
		public void handleMessage (Message msg) {
			aftImg =(ImageView) findViewById (R.id.camera_afterImg) ;
			autoImg =(ImageView) findViewById (R.id.camera_autoImg) ;
			switch (msg.what) {
			case 0 :	// 보낸 메세지가 0일때
				// 찍은 사진을 프레임화
				if (img.size() > 0) imgNum =onPreView (imgNum+1) ;	// onPreView 호출
				break ;
			case 1 :	// 보낸 메세지가 1일때
				// 방금찍은 사진을 투명하게 보여줌  (잔상효과)
				aftImg.setImageBitmap(img.get(img.size() -1)) ;
				aftImg.setAlpha(150) ;
				break ;
			case 2:	// 보낸 메세지가 2일때
				// 잔상을 없앰 (캡쳐 중)
				aftImg.setAlpha(0) ;
				break ;
			case 3 :	// 보낸 메세지가 3일때
				// 초점이 맞춰진 상태
				focusImg.setImageResource(R.drawable.focus_t) ;
				break ;
			case 4 :	// 보낸 메세지가 4일때
				// 캡쳐후 변경 (캡쳐 완료)
				focusImg.setImageResource(R.drawable.focus_f) ;
			}
		}
	} ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.camera); // 카메라화면 띄움
		
		img =new ArrayList<Bitmap> () ;
		
		afterFlag =false ;	// 잔상 여부
		focusFlag =false ;	// 초점 여부
		
		camera = new CameraFace(this); // CameraFace 객체 생성
		viewRel=(RelativeLayout) findViewById (R.id.camera_viewRel) ;
		viewRel.addView(camera) ;	// viewRel 레이아웃에 카메라화면을 띄운다.
		
		// 현재까지 찍은 사진들을 프레임화하여 preRel 레이아웃에 띄운다.
		preRel =(RelativeLayout) findViewById (R.id.camera_preRel) ;
		RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(
				(int)mainAct.scaleHeight /8, (int)mainAct.scaleHeight /8) ;
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM) ;
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT) ;
		preRel.setLayoutParams(params) ;
		
		// 포커스 이미지 레이아웃 셋
		focusImg =(ImageView) findViewById (R.id.camera_focus) ;
		params =new RelativeLayout.LayoutParams(
				(int)mainAct.scaleHeight /10, (int)mainAct.scaleHeight /10) ;
		params.addRule(RelativeLayout.CENTER_IN_PARENT) ;
		focusImg.setLayoutParams(params) ;
		focusImg.setScaleType(ScaleType.FIT_XY) ;
		
		// undo클릭 레이아웃 셋
		ImageButton undo =(ImageButton) findViewById (R.id.camera_undo),
				play =(ImageButton) findViewById (R.id.camera_play),
				save =(ImageButton) findViewById (R.id.camera_save) ;
		params =new RelativeLayout.LayoutParams(
				(int)mainAct.scaleHeight /10, (int)mainAct.scaleHeight /10) ;
		params.addRule(RelativeLayout.LEFT_OF, R.id.camera_save) ;
		params.setMargins(15, 0, 15, 0) ;
		undo.setLayoutParams(params) ;
		undo.setScaleType(ScaleType.FIT_XY) ;
		undo.setAdjustViewBounds(true) ;
		
		// save클릭 레이아웃 셋
		params =new RelativeLayout.LayoutParams(
				(int)mainAct.scaleHeight /10, (int)mainAct.scaleHeight /10) ;
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT) ;
		params.setMargins(15, 0, 15, 0) ;
		save.setLayoutParams(params) ;
		save.setScaleType(ScaleType.FIT_XY) ;
		save.setAdjustViewBounds(true) ;
		
		// play클릭 레이아웃 셋
		params =new RelativeLayout.LayoutParams(
				(int)mainAct.scaleHeight /10, (int)mainAct.scaleHeight /10) ;
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT) ;
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM) ;
		params.setMargins(15, 0, 15, 15) ;
		play.setLayoutParams(params) ;
		play.setScaleType(ScaleType.FIT_XY) ;
		play.setAdjustViewBounds(true) ;

		// 잔상 효과 스레드 
		aftImgThread =new AfterImgThread (handler) ;
		aftImgThread.setDaemon(true) ;
		aftImgThread.start() ;
		
		// 포커싱 스레드
		focusThread =new FocusThread (handler) ;
		focusThread.setDaemon(true) ;
		focusThread.start() ;
		
		// 프레임화 스레드
		auto =new PreViewMiniThread (handler) ;
		auto.setDaemon(true) ;
		auto.start() ;
		auto.autoFlag =true ;	// true로 설정함으로써 계속 실행
	}
	
	// 각 사진을 프레임처럼 표현
	public int onPreView (int num) {	
		if (num >= img.size()) num =0 ;	// index가 이미지보다 커지면 0으로 초기화
		autoImg.setImageBitmap (img.get(num)) ;	// 이미지 변경
		return num ;
	}
	
	// undo 클릭
	public void undoClick (View v) {
		// 이미지 사이즈가 없을 때
		if (img.size () == 0) {	
			if (toast == null) 
				toast =Toast.makeText(this, "취소 할 사진이 없습니다.", Toast.LENGTH_LONG) ;
			toast.show () ;
			
			return ;
		}
		// 이미지가 한 장이라도 있을 때
		img.remove(img.size() -1) ;	// 최근에 찍은 사진을 지운다.
		if (img.size () == 0) onRestart() ;	// 이미지가 없을 경우 restart (잔상 및 프레임 제거)
		Toast.makeText(this, "이전에 찍은 사진이 취소 되었습니다.", Toast.LENGTH_LONG).show () ;
	}
	
	// save 클릭
	public void saveClick (View v) {
		// 이미지가 없을 때
		if (img.size () == 0) {
			if (toast == null) 
				toast =Toast.makeText(this, "저장 할 사진이 없습니다.", Toast.LENGTH_LONG) ;
			toast.show () ;
			
			return ;
		}
		// 이미지가 있을 때
		final ProgressDialog saver = new ProgressDialog(this);
		saver.setMessage("저장 중입니다.");
		saver.show () ;
		
		// 기본 경로와 어플 폴더 경로 설정
		File pathOrg =Environment.getExternalStorageDirectory() ;
		String dir ="/CubicCamera/Cubic_" ;
		int num =1 ;
		// 저장
		try {
			File pathApp =new File (pathOrg +dir +num) ; // 저장경로
			//if (!pathApp.isDirectory()) pathApp.mkdirs(); // 없으면 만듬

			while (true) {
				if (pathApp.exists()) 	// 경로상의 폴더가 있으면
					pathApp =new File (pathOrg +dir +(++num)) ;	// 다음 폴더 탐색
				else {	// 경로상의 폴더가 없으면
					pathApp.mkdirs() ;	// 폴더 생성
					break ;
				}
			}
			// png확장자로 생성된 폴더에 저장
			for (int i = 0; i < img.size(); i++) {
				FileOutputStream outFile = new FileOutputStream(pathApp +"/Cubic_" + (i) + ".png"); // 저장
				img.get(i).compress(Bitmap.CompressFormat.PNG, 100, outFile); // 비트맵을PNG로
			}
		} catch (FileNotFoundException e) {
			Toast.makeText(this, "사진을 저장하는 도중 오류가 발생 하였습니다.", Toast.LENGTH_LONG).show () ;
			finish () ; 
			}
		saver.dismiss() ;	// 프로그레스바 제거
		img.removeAll(img) ;	// 리스트 초기화
		onRestart () ;	// 잔상 및 프레임 제거
		Toast.makeText(this, "사진이 저장 되었습니다.", Toast.LENGTH_LONG).show () ;
	}
	
	// play 클릭
	public void previewClick (View v) {
		// 이미지가 없을 때
		if (img.size() == 0) {
			if (toast == null) 
				toast =Toast.makeText(this, "구현 할 사진이 없습니다.", Toast.LENGTH_LONG) ;
			toast.show () ;
			
			return ;
		}
		// 이미지가 있을 때 PreViewActivity Intent 연결
		Intent preview =new Intent (CameraActivity.this, PreViewActivity.class) ;
		startActivity (preview) ;
	}	
	
	// 뒤로 가기 버튼클릭시
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (KeyCode == KeyEvent.KEYCODE_BACK) {
				img.removeAll(img);	// 리스트 초기화
				finish(); // Activity를 종료

				return true;
			}
		}
		return super.onKeyDown(KeyCode, event);
	}

	// Activity Restart 
	public void onRestart () {
		super.onRestart() ;
		// Restart시에 이미지가 없으면 잔상 및 프레임 제거
		if (img.size () == 0) {
			autoImg.setImageResource(R.drawable.alpha) ;
			aftImg.setAlpha(0) ;
		}
	}
	
	// Activity Stop 
	public void onStop () {
		super.onStop() ;
		// 현재 구동중인 스레드 모두 중지
		auto.interrupt () ;	
		auto.runFlag =true ;
		focusThread.interrupt () ;
		focusThread.runFlag =true ;
		aftImgThread.interrupt() ;
		aftImgThread.runFlag =true ;
	}
}
