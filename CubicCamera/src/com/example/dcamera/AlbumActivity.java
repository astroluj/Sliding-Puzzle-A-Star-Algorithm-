package com.example.dcamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AlbumActivity extends Activity {

	MainActivity mainAct ;
	
	private int checkCnt ;	// 체크된 갯수
	private boolean[] checking ;	// 각 뷰 마다의 체크 여부
	private ArrayList<Bitmap> img ;	// 이미지 리스트
	private LinearLayout linearLay ;	// 메뉴 레이아웃
	private Toast toast =null ;	// toast 중복 방지	
	private GridView gridView ;	// 그리드 뷰

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.album) ;

		linearLay =(LinearLayout) findViewById (R.id.album_menu) ;
		gridView =(GridView) findViewById (R.id.album_gridView) ;
		GridAdapter gridAdapter =new GridAdapter (this) ;
		gridView.setAdapter(gridAdapter) ;	// 그리드 뷰에 어댑터 셋
		
		// 사진을 선택 했을 경우
		gridView.setOnItemClickListener(new OnItemClickListener () {
			public void onItemClick (AdapterView <?> parent, View view, int position, long id) {
				// AutoItemActivity로 연결 및 선택한 포지션 전달
				Intent item =new Intent (AlbumActivity.this, AutoItemActivity.class) ;
				item.putExtra("item", (position +1)) ;
				startActivity (item) ;
			}
		}) ;
		gridView.setOnScrollListener(new OnScrollListener () {
			public void onScrollStateChanged (AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE :	// 정지
					break ;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL :	// 터치
					break ;
				case OnScrollListener.SCROLL_STATE_FLING :	// 이동
					break ;
				}
			}
			
			public void onScroll (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				
			}
		}) ;
	}
	
	// 어댑터 클래스
	public class GridAdapter extends BaseAdapter {
		
		private Context context ;
		private LayoutInflater inflater ;

		public GridAdapter (Context context) {
			this.context =context ;
			this.inflater =getLayoutInflater() ;
		}

		// 뷰의 갯수
		public int getCount() {
			return (null != img) ? img.size () : 0 ;
		}

		// 뷰의 ID
		public Object getItem(int position) {
			return (null != img) ? img.get(position) : 0 ;
		}

		// 뷰의 position
		public long getItemId(int position) {
			return position ;
		}

		// 뷰 셋
		public View getView(int position, View convertView, ViewGroup parent) {
		
				/*ImageView imgView =null ;
				if (null == convertView) {
					imgView = new ImageView(context);
					imgView.setLayoutParams(new GridView.LayoutParams(
							(int) (mainAct.scaleWidth / 2) - 25,
							(int) (mainAct.scaleWidth / 2) - 25));
					imgView.setAdjustViewBounds(true);
					imgView.setScaleType(ScaleType.CENTER_CROP);
				} else {
					Log.d ("D", "A") ;
					imgView = (ImageView) convertView;
				}
				imgView.setImageBitmap(img.get(position));

				return imgView;*/

			// convertView를 inflate 연결 한다.
			convertView = inflater.inflate(R.layout.album_checkbox, null);
			ImageView imgView = (ImageView) convertView.findViewById(R.id.check_img);
			CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.check_checkbox);
			checkBox.setTag(position) ;	// 각 체크박스에 현재 자기 뷰의 position 셋
			
			// 메뉴 레이아웃이 안보일 때 체크 박스도 안보이게
			if (linearLay.getVisibility() == View.GONE)
				checkBox.setVisibility(View.GONE);
			// 메뉴 레이아웃이 보이면 체크박스도 보이게
			else {
				checkBox.setVisibility(View.VISIBLE);
				checkBox.setChecked(checking[position]) ;	// 체크박스 상태 유지
			}
			// 체크박스가 체킹이 변할 때
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton view, boolean isChecked) {
					// 체크박스 체킹 상태가 바뀌면 그 상태 저장
					checking[(Integer)view.getTag ()] =isChecked ;
					// 체크된 갯수 계산
					if (view.isChecked()) checkCnt++ ;	
					else checkCnt-- ;
				}
			});
			// 뷰의 크기 및 보여질 이미지 셋
			convertView.setLayoutParams(new GridView.LayoutParams(
					(int) (mainAct.scaleWidth / 2) - 25, (int) (mainAct.scaleWidth / 2) - 25));
			imgView.setImageBitmap(img.get(position));

			return convertView;
		}
	}
	
	// 시간순 정렬
	public void sortFiles(File[] files) {
		// 기본 sort에서 시간순 정렬 추가
		Arrays.sort(files, new Comparator<Object>() {
			public int compare(Object obj1, Object obj2) {
				String s1 = ((File) obj1).lastModified() + "";
				String s2 = ((File) obj2).lastModified() + "";

				return s1.compareTo(s2); // s1과 s2 비교
			}
		});
	}
	
	// 파일 이름 변경
	public void renameFiles(String path) {
		// 파라미터로 받은 경로로 파일 설정 후 하위 파일 탐색
		File pathFile = new File(path);
		File[] childFiles = pathFile.listFiles();
		String dir = "/Cubic_";

		sortFiles(childFiles); // listFiles()로 얻어낸 하위 파일들은 시간 순으로 정렬
		// 0부분은 .nomedia파일이므로 생략
		for (int i = 1; i < childFiles.length; i++) {
			// 순서에 따라 파일 탐색
			File temp = new File(path + dir + i);
			if (!temp.exists()) { // 파일이 없으면
				try {
					// 정렬된 하위파일의 이름을 변경
					childFiles[i].renameTo(new File(path + dir + i));
				} catch (Exception e) {
				}
			}
		}
	}
	
	// 파일 삭제
	public void deleteFiles(String path) {
		// 파라미터로 받은 경로로 파일 설정 후 하위 파일 탐색
		File pathFile = new File(path);
		File[] childFiles = pathFile.listFiles();

		for (File childFile : childFiles) {
			// 하위 파일가 존재 할 경우 재귀호출로 하위 파일 먼저 삭제
			if (childFile.isDirectory())
				deleteFile(childFile.getAbsolutePath());
			// 하위 파일이 없으면 삭제
			else
				childFile.delete();
		}
		pathFile.delete(); // 현재 파일 삭제
	}
	
	// delete 클릭
	public void deleteClick (View v) {
		// 체크가 하나도 없을 경우
		if (checkCnt <= 0) {
			if (toast == null) 
				toast =Toast.makeText(this, "선택한 항목이 없습니다.", Toast.LENGTH_LONG) ;
			toast.show() ;
			
			return ;
		}
		// 기본경로 및 어플 폴더 경로 설정
		File pathOrg = Environment.getExternalStorageDirectory();
		String dir = "/CubicCamera/Cubic_";
		
		// 각 뷰의 마다 체크가 되어있었으면 삭제
		for (int i =0 ; i < checking.length ; i++) {
			if (checking[i]) deleteFiles (pathOrg +dir +(i+1)) ;
		}
		renameFiles (pathOrg +"/CubicCamera") ;	// 나머지 파일들의 이름 재 설정
		onResume () ;	// 그리드 뷰 갱신
	}
	
	// add 클릭
	public void addClick (View v) {
		linearLay.setVisibility(View.GONE) ;	// 메뉴 레이아웃을 닫고
		gridView.invalidateViews() ;	// 그리드뷰 갱신
		// cameraActivity Intent 연결
		Intent camera =new Intent (this, CameraActivity.class) ;
		startActivity (camera) ;
	}

	// 뒤로 가기 버튼클릭시
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (KeyCode == KeyEvent.KEYCODE_BACK) {
				// 메뉴 레이아웃이 보이면 없앤다.
				if (linearLay.getVisibility() == View.VISIBLE) {
					linearLay.setVisibility(View.GONE) ;
					for (int i =0 ; i < checking.length ; i++) checking[i] =false ;	// 초기화
					gridView.invalidateViews() ;	// 그리드 뷰 갱신
					return true ;
				}
				img.removeAll(img);	// 리스트 초기화
				finish(); // Activity를 종료

				return true;
			}
			else if (KeyCode == KeyEvent.KEYCODE_MENU) {
				// 메뉴 레이아웃이 안보일 때
				if (linearLay.getVisibility() == View.GONE)
					linearLay.setVisibility(View.VISIBLE) ;	// 메뉴 레이아웃 노출
				else {
					linearLay.setVisibility(View.GONE) ;	// 메뉴 레이아웃 제거
					for (int i =0 ; i < checking.length ; i++) checking[i] =false ;	// 초기화
				}
				gridView.invalidateViews() ;	// 그리드 뷰 갱신
				//openOptionsMenu(); 
				
				return true ;
			}
		}
		return super.onKeyDown(KeyCode, event);
	}
	
	// Activity Resume
	public void onResume () {
		super.onResume () ;
		checkCnt =0 ;	// 초기화
		img = new ArrayList<Bitmap>();	// 초기화
		// 기본경로 및 어플 폴더 경로 설정
		File pathOrg = Environment.getExternalStorageDirectory();
		String dir = "/CubicCamera/Cubic_";
		int num = 1;
		// 저장
		try {
			File pathApp = new File(pathOrg + dir + num); // Load경로
			// if (!pathApp.isDirectory()) pathApp.mkdirs(); // 없으면 만듬

			while (true) {
				if (pathApp.exists()) {	// 파일이 있으면
					img.add(BitmapFactory.decodeFile(pathApp + "/Cubic_0.png"));	// Load
					pathApp = new File(pathOrg + dir + (++num));	// 다음 파일 탐색
				}
				// 파일이 없으면 루프 탈출
				else break;
			}
		} catch (Exception e) {
			Toast.makeText(this, "사진을 불러오는 도중 오류가 발생 하였습니다.", Toast.LENGTH_LONG).show();
			finish();
		}
		checking = new boolean[img.size()];	// 뷰의 갯수만큼 재 할당
		gridView.invalidateViews() ;	// 그리드 뷰 갱신
	}
}
